"use client"

import { LogOut, Search } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import ChatListItem from "./chat-list-item"
import Image from "next/image"
import type { Chat, ChatSidebarProps } from "@/types/chat"
import type { User } from "@/types/user"

// Sample chat data
const CHATS: Chat[] = [
    {
        id: "1",
        name: "John Doe",
        avatar: "/placeholder.svg?height=40&width=40",
        lastMessage: "Hey, how are you doing?",
        time: "10:30 AM",
        unread: 2,
    },
    {
        id: "2",
        name: "Jane Smith",
        avatar: "/placeholder.svg?height=40&width=40",
        lastMessage: "Can we meet tomorrow?",
        time: "Yesterday",
        unread: 0,
    },
    {
        id: "3",
        name: "Web Development Group",
        avatar: "/placeholder.svg?height=40&width=40",
        lastMessage: "Alice: I just pushed the new changes",
        time: "Yesterday",
        unread: 5,
    },
    {
        id: "4",
        name: "Sarah Johnson",
        avatar: "/placeholder.svg?height=40&width=40",
        lastMessage: "Thanks for your help!",
        time: "Monday",
        unread: 0,
    },
    {
        id: "5",
        name: "Tech Support",
        avatar: "/placeholder.svg?height=40&width=40",
        lastMessage: "Your ticket has been resolved",
        time: "Monday",
        unread: 0,
    },
]

// Current user data
const CURRENT_USER: User = {
    id: "current",
    name: "Current User",
    avatar: "/placeholder.svg?height=32&width=32",
    email: "user@example.com",
    status: "online",
}

export default function ChatSidebar({ onChatSelect, selectedChat, onLogout }: ChatSidebarProps) {
    return (
        <div className="flex flex-col w-full h-full">
            {/* Header */}
            <div className="p-4 flex items-center justify-between border-b border-gray-200">
                <div className="flex items-center">
                    <h1 className="text-xl font-semibold text-blue-500">Telegram</h1>
                </div>
                <div className="flex items-center space-x-2">
                    <div className="relative group">
                        <div className="relative h-8 w-8 rounded-full overflow-hidden cursor-pointer">
                            <Image
                                src={CURRENT_USER.avatar || "/placeholder.svg"}
                                alt={CURRENT_USER.name}
                                fill
                                className="object-cover"
                            />
                        </div>
                        <div className="absolute right-0 top-full mt-2 w-48 bg-white shadow-lg rounded-md border border-gray-200 hidden group-hover:block z-10">
                            <div className="p-3 border-b border-gray-200">
                                <p className="font-medium">{CURRENT_USER.name}</p>
                                <p className="text-sm text-gray-500">{CURRENT_USER.email}</p>
                            </div>
                            <div className="p-2">
                                <button className="w-full text-left px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md">
                                    Settings
                                </button>
                            </div>
                        </div>
                    </div>
                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={onLogout}
                        className="text-gray-500 hover:text-red-500"
                        title="Logout"
                    >
                        <LogOut className="h-5 w-5" />
                    </Button>
                </div>
            </div>

            {/* Search */}
            <div className="p-3 border-b border-gray-200">
                <div className="relative">
                    <Search className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
                    <Input placeholder="Search" className="pl-9 bg-gray-100 border-none" />
                </div>
            </div>

            {/* Chat list */}
            <div className="flex-1 overflow-y-auto">
                {CHATS.map((chat) => (
                    <ChatListItem
                        key={chat.id}
                        chat={chat}
                        isSelected={chat.id === selectedChat}
                        onClick={() => onChatSelect(chat.id)}
                    />
                ))}
            </div>
        </div>
    )
}
