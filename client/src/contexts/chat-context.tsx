"use client"

import { createContext, useContext, useEffect, useReducer, useState, type ReactNode } from "react"
import { Client } from "@stomp/stompjs"
import SockJS from "sockjs-client"
import { AuthContext } from "./auth-context"
import { ChatMessage, ChatRoom, ChatState, WebSocketMessage } from "@/types/chat"

// Define the actions for our reducer
type ChatAction =
    | { type: "SET_CONNECTED"; payload: boolean }
    | { type: "SET_CONNECTING"; payload: boolean }
    | { type: "SET_ERROR"; payload: string | null }
    | { type: "SET_ACTIVE_ROOM"; payload: string }
    | { type: "ADD_ROOM"; payload: ChatRoom }
    | { type: "UPDATE_ROOM"; payload: { roomId: string; data: Partial<ChatRoom> } }
    | { type: "REMOVE_ROOM"; payload: string }
    | { type: "ADD_MESSAGE"; payload: { roomId: string; message: ChatMessage } }
    | { type: "UPDATE_MESSAGE"; payload: { roomId: string; messageId: string; data: Partial<ChatMessage> } }
    | { type: "SET_MESSAGES"; payload: { roomId: string; messages: ChatMessage[] } }
    | { type: "MARK_MESSAGES_READ"; payload: { roomId: string } }

// Define the context type
interface ChatContextType extends ChatState {
    connect: () => void
    disconnect: () => void
    sendMessage: (roomId: string, text: string, attachments?: any[]) => Promise<void>
    joinRoom: (roomId: string) => Promise<void>
    leaveRoom: (roomId: string) => Promise<void>
    createRoom: (name: string, participants: string[], type: "private" | "group") => Promise<string>
    setActiveRoom: (roomId: string) => void
    markAsRead: (roomId: string) => void
}

// Initial state
const initialState: ChatState = {
    rooms: {},
    activeRoomId: null,
    messages: {},
    isConnected: false,
    isConnecting: false,
    error: null,
}

// Create the context
export const ChatContext = createContext<ChatContextType>({
    ...initialState,
    connect: () => { },
    disconnect: () => { },
    sendMessage: async () => { },
    joinRoom: async () => { },
    leaveRoom: async () => { },
    createRoom: async () => "",
    setActiveRoom: () => { },
    markAsRead: () => { },
})

// Reducer function to handle state updates
function chatReducer(state: ChatState, action: ChatAction): ChatState {
    switch (action.type) {
        case "SET_CONNECTED":
            return { ...state, isConnected: action.payload, isConnecting: false }

        case "SET_CONNECTING":
            return { ...state, isConnecting: action.payload }

        case "SET_ERROR":
            return { ...state, error: action.payload }

        case "SET_ACTIVE_ROOM":
            return { ...state, activeRoomId: action.payload }

        case "ADD_ROOM":
            return {
                ...state,
                rooms: {
                    ...state.rooms,
                    [action.payload.id]: action.payload,
                },
                // Initialize empty messages array for this room if it doesn't exist
                messages: {
                    ...state.messages,
                    [action.payload.id]: state.messages[action.payload.id] || [],
                },
            }

        case "UPDATE_ROOM":
            return {
                ...state,
                rooms: {
                    ...state.rooms,
                    [action.payload.roomId]: {
                        ...state.rooms[action.payload.roomId],
                        ...action.payload.data,
                    },
                },
            }

        case "REMOVE_ROOM":
            const { [action.payload]: _, ...remainingRooms } = state.rooms
            const { [action.payload]: __, ...remainingMessages } = state.messages
            return {
                ...state,
                rooms: remainingRooms,
                messages: remainingMessages,
                activeRoomId: state.activeRoomId === action.payload ? null : state.activeRoomId,
            }

        case "ADD_MESSAGE": {
            const { roomId, message } = action.payload
            const roomMessages = [...(state.messages[roomId] || []), message]

            // Update the room's last message and unread count
            const room = state.rooms[roomId]
            const isActiveRoom = state.activeRoomId === roomId
            const unreadCount = isActiveRoom ? 0 : room.unreadCount + (message.senderId !== "current-user" ? 1 : 0)

            return {
                ...state,
                messages: {
                    ...state.messages,
                    [roomId]: roomMessages,
                },
                rooms: {
                    ...state.rooms,
                    [roomId]: {
                        ...room,
                        lastMessage: message,
                        unreadCount,
                    },
                },
            }
        }

        case "UPDATE_MESSAGE": {
            const { roomId, messageId, data } = action.payload
            const roomMessages = state.messages[roomId] || []
            const updatedMessages = roomMessages.map((msg) => (msg.id === messageId ? { ...msg, ...data } : msg))

            return {
                ...state,
                messages: {
                    ...state.messages,
                    [roomId]: updatedMessages,
                },
            }
        }

        case "SET_MESSAGES":
            return {
                ...state,
                messages: {
                    ...state.messages,
                    [action.payload.roomId]: action.payload.messages,
                },
            }

        case "MARK_MESSAGES_READ": {
            const { roomId } = action.payload
            const room = state.rooms[roomId]

            if (!room) return state

            // Mark all messages as read
            const updatedMessages = (state.messages[roomId] || []).map((msg) =>
                msg.status === "delivered" ? { ...msg, status: "read" } : msg,
            )

            return {
                ...state,
                messages: {
                    ...state.messages,
                    [roomId]: updatedMessages,
                },
                rooms: {
                    ...state.rooms,
                    [roomId]: {
                        ...room,
                        unreadCount: 0,
                    },
                },
            }
        }

        default:
            return state
    }
}

// Provider component
interface ChatProviderProps {
    children: ReactNode
    apiUrl?: string
}

export function ChatProvider({ children, apiUrl = "http://localhost:8080" }: ChatProviderProps) {
    const [state, dispatch] = useReducer(chatReducer, initialState)
    const [client, setClient] = useState<Client | null>(null)
    const { user, isAuthenticated } = useContext(AuthContext)

    // Function to initialize STOMP client
    const initializeClient = (token: string) => {
        const stompClient = new Client({
            webSocketFactory: () => new SockJS(`${apiUrl}/ws`),
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: () => {
                console.log("Connected to chat WebSocket")
                dispatch({ type: "SET_CONNECTED", payload: true })

                // Subscribe to personal channel for user-specific messages
                if (user?.id) {
                    stompClient.subscribe(`/user/${user.id}/queue/messages`, handleIncomingMessage)
                }

                // Subscribe to global notifications
                stompClient.subscribe("/topic/notifications", handleNotification)

                // Subscribe to active rooms
                Object.keys(state.rooms).forEach((roomId) => {
                    if (state.rooms[roomId].isActive) {
                        stompClient.subscribe(`/topic/chat/${roomId}`, handleIncomingMessage)
                    }
                })
            },
            onDisconnect: () => {
                console.log("Disconnected from chat WebSocket")
                dispatch({ type: "SET_CONNECTED", payload: false })
            },
            onStompError: (frame) => {
                console.error("STOMP error:", frame)
                dispatch({ type: "SET_ERROR", payload: frame.headers?.message || "Connection error" })

                // Handle token expiration
                if (frame.headers?.message?.includes("token")) {
                    // Trigger token refresh logic here if needed
                    disconnect()
                }
            },
            onWebSocketClose: () => {
                console.log("WebSocket closed")
                dispatch({ type: "SET_CONNECTED", payload: false })
            },
            debug: (str) => {
                console.log("STOMP Debug:", str)
            },
        })

        return stompClient
    }

    // Handle incoming chat messages
    const handleIncomingMessage = (message: any) => {
        try {
            const wsMessage: WebSocketMessage = JSON.parse(message.body)
            console.log("Received message:", wsMessage)

            if (wsMessage.type === "chat" && typeof wsMessage.payload === "object") {
                const chatMessage = wsMessage.payload as ChatMessage

                if (chatMessage.chatId) {
                    // Add message to the appropriate room
                    dispatch({
                        type: "ADD_MESSAGE",
                        payload: {
                            roomId: chatMessage.chatId,
                            message: {
                                ...chatMessage,
                                status: "delivered",
                            },
                        },
                    })
                }
            }
        } catch (error) {
            console.error("Error processing incoming message:", error)
        }
    }

    // Handle system notifications
    const handleNotification = (message: any) => {
        try {
            const notification: WebSocketMessage = JSON.parse(message.body)
            console.log("Received notification:", notification)

            // Handle different notification types
            switch (notification.type) {
                case "room_update":
                    if (notification.chatId && typeof notification.payload === "object") {
                        dispatch({
                            type: "UPDATE_ROOM",
                            payload: {
                                roomId: notification.chatId,
                                data: notification.payload,
                            },
                        })
                    }
                    break

                case "new_room":
                    if (typeof notification.payload === "object") {
                        const room = notification.payload as ChatRoom
                        dispatch({
                            type: "ADD_ROOM",
                            payload: room,
                        })
                    }
                    break

                case "message_status":
                    if (notification.chatId && notification.payload?.messageId) {
                        dispatch({
                            type: "UPDATE_MESSAGE",
                            payload: {
                                roomId: notification.chatId,
                                messageId: notification.payload.messageId,
                                data: { status: notification.payload.status },
                            },
                        })
                    }
                    break
            }
        } catch (error) {
            console.error("Error processing notification:", error)
        }
    }

    // Connect to WebSocket
    const connect = () => {
        if (!isAuthenticated || !user?.token?.accessToken) {
            dispatch({ type: "SET_ERROR", payload: "Authentication required" })
            return
        }

        dispatch({ type: "SET_CONNECTING", payload: true })

        try {
            const stompClient = initializeClient(user.token.accessToken)
            stompClient.activate()
            setClient(stompClient)
        } catch (error) {
            console.error("Failed to connect:", error)
            dispatch({ type: "SET_ERROR", payload: "Failed to connect to chat server" })
            dispatch({ type: "SET_CONNECTING", payload: false })
        }
    }

    // Disconnect from WebSocket
    const disconnect = () => {
        if (client) {
            client.deactivate()
            setClient(null)
            dispatch({ type: "SET_CONNECTED", payload: false })
        }
    }

    // Send a message to a chat room
    const sendMessage = async (roomId: string, text: string, attachments: any[] = []) => {
        if (!client || !client.connected) {
            dispatch({ type: "SET_ERROR", payload: "Not connected to chat server" })
            return
        }

        if (!text.trim() && attachments.length === 0) {
            return
        }

        try {
            // Create a temporary message ID
            const tempId = `temp-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`

            // Create the message object
            const message: ChatMessage = {
                id: tempId,
                text,
                senderId: user?.id || "current-user",
                senderName: user?.name || "You",
                senderAvatar: user?.avatar,
                timestamp: Date.now(),
                chatId: roomId,
                attachments: attachments,
                status: "sending",
            }

            // Add the message to our local state first (optimistic update)
            dispatch({
                type: "ADD_MESSAGE",
                payload: {
                    roomId,
                    message,
                },
            })

            // Prepare the WebSocket message
            const wsMessage: WebSocketMessage = {
                type: "chat",
                payload: message,
                timestamp: Date.now(),
                senderId: user?.id,
                chatId: roomId,
            }

            // Send the message through WebSocket
            client.publish({
                destination: `/app/chat/${roomId}/send`,
                body: JSON.stringify(wsMessage),
                headers: { "content-type": "application/json" },
            })

            // In a real app, you might want to update the message status based on server response
            // For now, we'll simulate a successful send after a short delay
            setTimeout(() => {
                dispatch({
                    type: "UPDATE_MESSAGE",
                    payload: {
                        roomId,
                        messageId: tempId,
                        data: { status: "sent" },
                    },
                })
            }, 500)
        } catch (error) {
            console.error("Error sending message:", error)
            dispatch({ type: "SET_ERROR", payload: "Failed to send message" })
        }
    }

    // Join a chat room
    const joinRoom = async (roomId: string): Promise<void> => {
        if (!client || !client.connected) {
            dispatch({ type: "SET_ERROR", payload: "Not connected to chat server" })
            return
        }

        try {
            // Subscribe to the room's messages
            client.subscribe(`/topic/chat/${roomId}`, handleIncomingMessage)

            // Update room status in our state
            dispatch({
                type: "UPDATE_ROOM",
                payload: {
                    roomId,
                    data: { isActive: true },
                },
            })

            // Fetch room history if needed
            await fetchRoomHistory(roomId)
        } catch (error) {
            console.error("Error joining room:", error)
            dispatch({ type: "SET_ERROR", payload: "Failed to join chat room" })
        }
    }

    // Leave a chat room
    const leaveRoom = async (roomId: string): Promise<void> => {
        if (!client || !client.connected) {
            return
        }

        try {
            // Unsubscribe from the room
            client.unsubscribe(`/topic/chat/${roomId}`)

            // Update room status in our state
            dispatch({
                type: "UPDATE_ROOM",
                payload: {
                    roomId,
                    data: { isActive: false },
                },
            })
        } catch (error) {
            console.error("Error leaving room:", error)
        }
    }

    // Create a new chat room
    const createRoom = async (name: string, participants: string[], type: "private" | "group"): Promise<string> => {
        if (!client || !client.connected) {
            dispatch({ type: "SET_ERROR", payload: "Not connected to chat server" })
            return ""
        }

        try {
            // In a real app, you would make an API call to create the room
            // For this example, we'll simulate it
            const roomId = `room-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`

            const newRoom: ChatRoom = {
                id: roomId,
                name,
                type,
                participants: [...participants, user?.id || "current-user"],
                unreadCount: 0,
                isActive: true,
            }

            // Add the room to our state
            dispatch({
                type: "ADD_ROOM",
                payload: newRoom,
            })

            // Subscribe to the room
            client.subscribe(`/topic/chat/${roomId}`, handleIncomingMessage)

            return roomId
        } catch (error) {
            console.error("Error creating room:", error)
            dispatch({ type: "SET_ERROR", payload: "Failed to create chat room" })
            return ""
        }
    }

    // Set the active chat room
    const setActiveRoom = (roomId: string) => {
        dispatch({ type: "SET_ACTIVE_ROOM", payload: roomId })

        // Mark messages as read when switching to a room
        markAsRead(roomId)
    }

    // Mark messages in a room as read
    const markAsRead = (roomId: string) => {
        dispatch({ type: "MARK_MESSAGES_READ", payload: { roomId } })

        // In a real app, you would also notify the server about read status
        if (client && client.connected) {
            client.publish({
                destination: `/app/chat/${roomId}/read`,
                body: JSON.stringify({
                    type: "read_receipt",
                    chatId: roomId,
                    timestamp: Date.now(),
                    senderId: user?.id,
                }),
            })
        }
    }

    // Fetch chat history for a room
    const fetchRoomHistory = async (roomId: string) => {
        try {
            // In a real app, you would make an API call to fetch history
            // For this example, we'll simulate it with empty history
            dispatch({
                type: "SET_MESSAGES",
                payload: {
                    roomId,
                    messages: [],
                },
            })
        } catch (error) {
            console.error("Error fetching room history:", error)
        }
    }

    // Connect/disconnect based on authentication status
    useEffect(() => {
        if (isAuthenticated && user?.token?.accessToken) {
            connect()
        } else {
            disconnect()
        }

        return () => {
            disconnect()
        }
    }, [isAuthenticated, user?.token?.accessToken])

    // Value to be provided by the context
    const value: ChatContextType = {
        ...state,
        connect,
        disconnect,
        sendMessage,
        joinRoom,
        leaveRoom,
        createRoom,
        setActiveRoom,
        markAsRead,
    }

    return <ChatContext.Provider value={value}>{children}</ChatContext.Provider>
}

// Custom hook for using the chat context
export const useChat = () => useContext(ChatContext)
