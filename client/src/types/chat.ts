// Types related to chats/conversations

export interface Chat {
    id: string
    name: string
    avatar: string
    lastMessage: string
    time: string
    unread: number
}

export interface ChatDetails {
    id: string
    name: string
    avatar: string
    online?: boolean
    members?: number
    lastSeen?: string
}

export interface ChatSidebarProps {
    onChatSelect: (chatId: string) => void
    selectedChat: string | null
    onLogout: () => void
}

export interface ChatListItemProps {
    chat: Chat
    isSelected: boolean
    onClick: () => void
}

export interface ChatWindowProps {
    selectedChat: string | null
    toggleSidebar: () => void
    showSidebar: boolean
}

export interface ChatInterfaceProps {
    initialSelectedChat?: string | null
}

export interface Attachment {
    id: string
    filename: string
    url: string
    size: number
    type: string
}

// WebSocket specific types
export interface WebSocketMessage {
    type: string
    payload: string | ChatMessage | any
    timestamp: number
    senderId?: string
    chatId?: string
}

export interface ChatMessage {
    id: string
    text: string
    senderId: string
    senderName: string
    senderAvatar?: string
    timestamp: number
    chatId: string
    attachments?: Attachment[]
    status: "sending" | "sent" | "delivered" | "read" | "failed"
}

export interface ChatRoom {
    id: string
    name: string
    type: "private" | "group"
    participants: string[] // User IDs
    avatar?: string
    lastMessage?: ChatMessage
    unreadCount: number
    isActive: boolean
}

export interface ChatState {
    rooms: Record<string, ChatRoom>
    activeRoomId: string | null
    messages: Record<string, ChatMessage[]>
    isConnected: boolean
    isConnecting: boolean
    error: string | null
}
