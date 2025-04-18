"use client"

import type React from "react"

import { useState } from "react"
import { Search } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { useChat } from "@/context/chat-context"

export default function ConversationList() {
  const [searchQuery, setSearchQuery] = useState("")
  const { conversations, selectedConversation, setSelectedConversation, searchConversations, isLoadingConversations } =
    useChat()

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    await searchConversations(searchQuery)
  }

  const handleSelectConversation = (conversation) => {
    setSelectedConversation(conversation)
  }

  return (
    <div className="flex flex-col h-full">
      <div className="p-4 border-b">
        <form onSubmit={handleSearch} className="relative">
          <Input
            placeholder="Search conversations"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <Button
            type="submit"
            variant="ghost"
            size="sm"
            className="absolute right-2 top-1/2 transform -translate-y-1/2"
          >
            Search
          </Button>
        </form>
      </div>

      <div className="flex-1 overflow-y-auto">
        {isLoadingConversations ? (
          <div className="flex justify-center items-center h-20">
            <p className="text-gray-500">Loading conversations...</p>
          </div>
        ) : conversations.length === 0 ? (
          <div className="flex justify-center items-center h-20">
            <p className="text-gray-500">No conversations found</p>
          </div>
        ) : (
          <ul className="divide-y">
            {conversations.map((conversation) => (
              <li
                key={conversation.id}
                onClick={() => handleSelectConversation(conversation)}
                className={`p-4 cursor-pointer hover:bg-gray-50 ${
                  selectedConversation?.id === conversation.id ? "bg-gray-100" : ""
                }`}
              >
                <div className="flex flex-col">
                  <div className="flex justify-between">
                    <span className="font-medium">{conversation.name}</span>
                    <span className="text-xs text-gray-500">
                      {new Date(conversation.timestamp).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </span>
                  </div>
                  <p className="text-sm text-gray-600 truncate mt-1">{conversation.lastMessage}</p>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}
