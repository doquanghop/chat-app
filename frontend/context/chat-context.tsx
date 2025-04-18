"use client"

import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from "react"
import { fetchConversations, fetchMessages } from "@/lib/api"
import { mockWebSocket, WebSocketEventType, parseWebSocketMessage } from "@/lib/websocket"
import { useAuth } from "./auth-context"

// Define types
interface Message {
  id: string
  conversationId: string
  senderId: string
  text: string
  timestamp: string
}

interface Conversation {
  id: string
  name: string
  lastMessage: string
  timestamp: string
  participants: string[]
}

interface ChatContextType {
  conversations: Conversation[]
  messages: { [conversationId: string]: Message[] }
  selectedConversation: Conversation | null
  setSelectedConversation: (conversation: Conversation | null) => void
  addMessage: (message: Message) => void
  loadMessages: (conversationId: string) => Promise<void>
  loadConversations: () => Promise<void>
  moveConversationToTop: (conversationId: string) => void
  searchConversations: (query: string) => Promise<void>
  isLoadingConversations: boolean
  isLoadingMessages: boolean
}

const ChatContext = createContext<ChatContextType | undefined>(undefined)

export function ChatProvider({ children }: { children: ReactNode }) {
  const [conversations, setConversations] = useState<Conversation[]>([])
  const [messages, setMessages] = useState<{ [conversationId: string]: Message[] }>({})
  const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null)
  const [isLoadingConversations, setIsLoadingConversations] = useState(false)
  const [isLoadingMessages, setIsLoadingMessages] = useState(false)
  const { user, isAuthenticated } = useAuth()

  // Load conversations
  const loadConversations = useCallback(async () => {
    if (!isAuthenticated) return

    try {
      setIsLoadingConversations(true)
      const data = await fetchConversations()
      // Sort conversations by timestamp (newest first)
      const sortedConversations = [...data].sort(
        (a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime(),
      )
      setConversations(sortedConversations)
    } catch (error) {
      console.error("Failed to fetch conversations:", error)
    } finally {
      setIsLoadingConversations(false)
    }
  }, [isAuthenticated])

  // Load messages for a specific conversation
  const loadMessages = useCallback(
    async (conversationId: string) => {
      if (!isAuthenticated) return

      try {
        setIsLoadingMessages(true)
        // Check if we already have messages for this conversation
        if (!messages[conversationId]) {
          const data = await fetchMessages(conversationId)
          setMessages((prev) => ({
            ...prev,
            [conversationId]: data,
          }))
        }
      } catch (error) {
        console.error(`Failed to fetch messages for conversation ${conversationId}:`, error)
      } finally {
        setIsLoadingMessages(false)
      }
    },
    [isAuthenticated, messages],
  )

  // Add a new message to the messages state
  const addMessage = useCallback((message: Message) => {
    setMessages((prev) => {
      const conversationMessages = prev[message.conversationId] || []

      // Check if message already exists to avoid duplicates
      if (conversationMessages.some((m) => m.id === message.id)) {
        return prev
      }

      return {
        ...prev,
        [message.conversationId]: [...conversationMessages, message],
      }
    })

    // Move the conversation to the top of the list
    moveConversationToTop(message.conversationId)
  }, [])

  // Move a conversation to the top of the list
  const moveConversationToTop = useCallback(
    (conversationId: string) => {
      setConversations((prev) => {
        const conversation = prev.find((c) => c.id === conversationId)
        if (!conversation) return prev

        // Remove the conversation from the current position
        const filteredConversations = prev.filter((c) => c.id !== conversationId)

        // Update the conversation with the latest message if available
        const updatedConversation = { ...conversation }
        const conversationMessages = messages[conversationId]
        if (conversationMessages && conversationMessages.length > 0) {
          const latestMessage = conversationMessages[conversationMessages.length - 1]
          updatedConversation.lastMessage = latestMessage.text
          updatedConversation.timestamp = latestMessage.timestamp
        }

        // Add the conversation to the beginning of the array
        return [updatedConversation, ...filteredConversations]
      })
    },
    [messages],
  )

  // Search conversations
  const searchConversations = useCallback(
    async (query: string) => {
      if (!isAuthenticated) return

      try {
        setIsLoadingConversations(true)
        if (!query.trim()) {
          // If search is empty, load all conversations
          await loadConversations()
        } else {
          // Filter conversations locally based on the query
          const filteredConversations = conversations.filter((conv) =>
            conv.name.toLowerCase().includes(query.toLowerCase()),
          )
          setConversations(filteredConversations)
        }
      } catch (error) {
        console.error("Search failed:", error)
      } finally {
        setIsLoadingConversations(false)
      }
    },
    [isAuthenticated, loadConversations, conversations],
  )

  // Load initial conversations
  useEffect(() => {
    if (isAuthenticated) {
      loadConversations()
    }
  }, [isAuthenticated, loadConversations])

  // Set up WebSocket event listeners
  useEffect(() => {
    if (!isAuthenticated) return

    const handleWebSocketMessage = (event: MessageEvent) => {
      const wsEvent = parseWebSocketMessage(event)

      if (wsEvent.type === WebSocketEventType.NEW_MESSAGE) {
        const newMessage = wsEvent.payload

        // Add the message to our state
        addMessage(newMessage)

        // Update the conversation's last message
        setConversations((prev) => {
          return prev.map((conv) => {
            if (conv.id === newMessage.conversationId) {
              return {
                ...conv,
                lastMessage: newMessage.text,
                timestamp: newMessage.timestamp,
              }
            }
            return conv
          })
        })
      }
    }

    // Add WebSocket event listener
    mockWebSocket.on("message", handleWebSocketMessage)

    // Clean up event listener when component unmounts
    return () => {
      mockWebSocket.off("message", handleWebSocketMessage)
    }
  }, [isAuthenticated, addMessage])

  return (
    <ChatContext.Provider
      value={{
        conversations,
        messages,
        selectedConversation,
        setSelectedConversation,
        addMessage,
        loadMessages,
        loadConversations,
        moveConversationToTop,
        searchConversations,
        isLoadingConversations,
        isLoadingMessages,
      }}
    >
      {children}
    </ChatContext.Provider>
  )
}

export function useChat() {
  const context = useContext(ChatContext)
  if (context === undefined) {
    throw new Error("useChat must be used within a ChatProvider")
  }
  return context
}
