"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import ConversationList from "@/components/conversation-list"
import ChatWindow from "@/components/chat-window"
import { useAuth } from "@/context/auth-context"
import { useChat } from "@/context/chat-context"
import { initializeWebSocket } from "@/lib/api"

export default function Chat() {
  const { user, isAuthenticated } = useAuth()
  const { selectedConversation } = useChat()
  const router = useRouter()

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/")
    } else {
      // Initialize WebSocket when chat page loads
      if (typeof window !== "undefined") {
        // Make mockWebSocket available globally for components to access
        ;(window as any).mockWebSocket = initializeWebSocket()
      }
    }

    // Clean up function
    return () => {
      // WebSocket cleanup is handled in auth-context.tsx
    }
  }, [isAuthenticated, router])

  if (!isAuthenticated) {
    return null
  }

  return (
    <div className="flex h-screen bg-gray-100">
      <div className="w-1/3 border-r border-gray-200 bg-white">
        <ConversationList />
      </div>
      <div className="w-2/3">
        {selectedConversation ? (
          <ChatWindow conversation={selectedConversation} />
        ) : (
          <div className="flex h-full items-center justify-center text-gray-500">
            Select a conversation to start messaging
          </div>
        )}
      </div>
    </div>
  )
}
