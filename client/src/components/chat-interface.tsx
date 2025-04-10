"use client"

import { useState } from "react"
import type { ChatInterfaceProps } from "@/types/chat"
import ChatSidebar from "./chat-sidebar"
import ChatWindow from "./chat-window"
import { useIsMobile } from "./ui/use-mobile"

export default function ChatInterface({ initialSelectedChat = null }: ChatInterfaceProps) {
    const [selectedChat, setSelectedChat] = useState<string | null>(initialSelectedChat)
    const isMobile = useIsMobile()
    const [showSidebar, setShowSidebar] = useState(!isMobile)

    // Toggle sidebar on mobile
    const toggleSidebar = () => {
        if (isMobile) {
            setShowSidebar(!showSidebar)
        }
    }

    // Handle chat selection
    const handleChatSelect = (chatId: string) => {
        setSelectedChat(chatId)
        if (isMobile) {
            setShowSidebar(false)
        }
    }

    // Handle logout
    const handleLogout = () => {
        alert("Logged out successfully")
        // In a real app, you would clear auth tokens, redirect to login page, etc.
    }

    return (
        <div className="flex h-screen w-full overflow-hidden bg-white">
            {/* Sidebar */}
            <div
                className={`${showSidebar ? "flex" : "hidden"
                    } md:flex h-full ${isMobile ? "w-full absolute z-10" : "w-[320px]"} border-r border-gray-200 bg-white`}
            >
                <ChatSidebar onChatSelect={handleChatSelect} selectedChat={selectedChat} onLogout={handleLogout} />
            </div>

            {/* Main chat area */}
            <div className="flex-1 flex flex-col h-full">
                <ChatWindow selectedChat={selectedChat} toggleSidebar={toggleSidebar} showSidebar={showSidebar} />
            </div>
        </div>
    )
}
