"use client"

import type React from "react"

import { useState, useEffect, useRef } from "react"
import { Send } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { sendMessage } from "@/lib/api"
import { useAuth } from "@/context/auth-context"
import { useChat } from "@/context/chat-context"

interface ChatWindowProps {
  conversation: any
}

export default function ChatWindow({ conversation }: ChatWindowProps) {
  const [newMessage, setNewMessage] = useState("")
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const { user } = useAuth()
  const { messages, addMessage, loadMessages, isLoadingMessages } = useChat()

  // Get messages for the current conversation
  const conversationMessages = messages[conversation.id] || []

  useEffect(() => {
    if (conversation) {
      loadMessages(conversation.id)
    }
  }, [conversation, loadMessages])

  useEffect(() => {
    scrollToBottom()
  }, [conversationMessages])

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!newMessage.trim()) return

    try {
      const sentMessage = await sendMessage(conversation.id, newMessage)

      // Add the message directly to our state
      addMessage(sentMessage)

      // Clear the input field
      setNewMessage("")
    } catch (error) {
      console.error("Failed to send message:", error)
    }
  }

  const isOwnMessage = (senderId: string) => {
    return user?.id === senderId
  }

  return (
    <div className="flex flex-col h-full">
      <div className="p-4 border-b bg-white">
        <h2 className="font-medium">{conversation.name}</h2>
      </div>

      <div className="flex-1 overflow-y-auto p-4 bg-gray-50">
        {isLoadingMessages ? (
          <div className="flex justify-center items-center h-20">
            <p className="text-gray-500">Loading messages...</p>
          </div>
        ) : conversationMessages.length === 0 ? (
          <div className="flex justify-center items-center h-20">
            <p className="text-gray-500">No messages yet</p>
          </div>
        ) : (
          <div className="space-y-4">
            {conversationMessages.map((message) => (
              <div
                key={message.id}
                className={`flex ${isOwnMessage(message.senderId) ? "justify-end" : "justify-start"}`}
              >
                <div
                  className={`max-w-[70%] px-4 py-2 rounded-lg ${
                    isOwnMessage(message.senderId) ? "bg-blue-500 text-white" : "bg-white border"
                  }`}
                >
                  <p>{message.text}</p>
                  <p className={`text-xs mt-1 ${isOwnMessage(message.senderId) ? "text-blue-100" : "text-gray-500"}`}>
                    {new Date(message.timestamp).toLocaleTimeString([], {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </p>
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>
        )}
      </div>

      <div className="p-4 bg-white border-t">
        <form onSubmit={handleSendMessage} className="flex items-center gap-2">
          <Input
            placeholder="Type a message..."
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            className="flex-1"
          />
          <Button type="submit" size="icon">
            <Send className="h-4 w-4" />
          </Button>
        </form>
      </div>
    </div>
  )
}
