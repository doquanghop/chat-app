"use client"

import { useState } from "react"
import { Menu, Phone, Video, Send, Paperclip, Smile, Mic, ChevronLeft } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import Image from "next/image"
import MessageBubble from "./message-bubble"
import type { ChatWindowProps } from "@/types/chat"
import type { Message } from "@/types/message"
import type { ChatDetails } from "@/types/chat"

// Sample messages data
const MESSAGES: Record<string, Message[]> = {
    "1": [
        { id: "1", text: "Hey, how are you doing?", sender: "other", time: "10:30 AM" },
        { id: "2", text: "I'm good, thanks! How about you?", sender: "me", time: "10:31 AM" },
        { id: "3", text: "I'm doing well. Just working on a new project.", sender: "other", time: "10:32 AM" },
        { id: "4", text: "That sounds interesting! What kind of project?", sender: "me", time: "10:33 AM" },
        {
            id: "5",
            text: "It's a web application for a client. Using React and Node.js.",
            sender: "other",
            time: "10:35 AM",
        },
    ],
    "2": [
        { id: "1", text: "Can we meet tomorrow?", sender: "other", time: "Yesterday" },
        { id: "2", text: "Sure, what time works for you?", sender: "me", time: "Yesterday" },
        { id: "3", text: "How about 2 PM?", sender: "other", time: "Yesterday" },
        { id: "4", text: "That works for me. See you then!", sender: "me", time: "Yesterday" },
    ],
    "3": [
        { id: "1", text: "I just pushed the new changes", sender: "other", name: "Alice", time: "Yesterday" },
        { id: "2", text: "Great! I'll review them soon.", sender: "me", time: "Yesterday" },
        { id: "3", text: "The new UI looks much better now.", sender: "other", name: "Bob", time: "Yesterday" },
        { id: "4", text: "I agree. Good job team!", sender: "me", time: "Yesterday" },
        { id: "5", text: "When is our next meeting?", sender: "other", name: "Charlie", time: "Yesterday" },
    ],
}

// Sample chat data
const CHATS: Record<string, ChatDetails> = {
    "1": { id: "1", name: "John Doe", avatar: "/placeholder.svg?height=40&width=40", online: true },
    "2": { id: "2", name: "Jane Smith", avatar: "/placeholder.svg?height=40&width=40", online: false },
    "3": { id: "3", name: "Web Development Group", avatar: "/placeholder.svg?height=40&width=40", members: 15 },
}

export default function ChatWindow({ selectedChat, toggleSidebar, showSidebar }: ChatWindowProps) {
    const [message, setMessage] = useState("")

    const handleSendMessage = () => {
        if (message.trim() === "") return
        // In a real app, you would send the message to the server here
        setMessage("")
    }

    // If no chat is selected, show a placeholder
    if (!selectedChat) {
        return (
            <div className="flex flex-col items-center justify-center h-full bg-gray-50">
                <div className="text-center p-6">
                    <h2 className="text-xl font-semibold text-gray-700 mb-2">Select a chat to start messaging</h2>
                    <p className="text-gray-500">Choose from your existing conversations or start a new one</p>
                    <Button variant="outline" className="mt-4" onClick={toggleSidebar}>
                        {showSidebar ? "Hide" : "Show"} Chats
                    </Button>
                </div>
            </div>
        )
    }

    const chat = CHATS[selectedChat]
    const chatMessages = MESSAGES[selectedChat] || []

    return (
        <>
            {/* Chat header */}
            <div className="flex items-center p-3 border-b border-gray-200 bg-white">
                <Button variant="ghost" size="icon" className="md:hidden mr-2" onClick={toggleSidebar}>
                    {showSidebar ? <ChevronLeft /> : <Menu />}
                </Button>

                <div className="flex items-center flex-1">
                    <div className="relative h-10 w-10 rounded-full overflow-hidden mr-3">
                        <Image src={chat.avatar || "/placeholder.svg"} alt={chat.name} fill className="object-cover" />
                    </div>
                    <div>
                        <h2 className="font-medium">{chat.name}</h2>
                        <p className="text-xs text-gray-500">
                            {chat.online ? "Online" : chat.members ? `${chat.members} members` : "Last seen recently"}
                        </p>
                    </div>
                </div>

                <div className="flex items-center space-x-2">
                    <Button variant="ghost" size="icon">
                        <Phone className="h-5 w-5 text-gray-500" />
                    </Button>
                    <Button variant="ghost" size="icon">
                        <Video className="h-5 w-5 text-gray-500" />
                    </Button>
                    <Button variant="ghost" size="icon">
                        <Menu className="h-5 w-5 text-gray-500" />
                    </Button>
                </div>
            </div>

            {/* Messages area */}
            <div className="flex-1 overflow-y-auto p-4 bg-gray-50">
                {chatMessages.map((msg) => (
                    <MessageBubble key={msg.id} message={msg} isGroup={selectedChat === "3"} />
                ))}
            </div>

            {/* Message input */}
            <div className="p-3 border-t border-gray-200 bg-white">
                <div className="flex items-center">
                    <Button variant="ghost" size="icon">
                        <Paperclip className="h-5 w-5 text-gray-500" />
                    </Button>
                    <Input
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        placeholder="Write a message..."
                        className="flex-1 mx-2 bg-gray-100 border-none"
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                handleSendMessage()
                            }
                        }}
                    />
                    {message.trim() === "" ? (
                        <>
                            <Button variant="ghost" size="icon">
                                <Smile className="h-5 w-5 text-gray-500" />
                            </Button>
                            <Button variant="ghost" size="icon">
                                <Mic className="h-5 w-5 text-gray-500" />
                            </Button>
                        </>
                    ) : (
                        <Button
                            size="icon"
                            className="bg-blue-500 hover:bg-blue-600 text-white rounded-full"
                            onClick={handleSendMessage}
                        >
                            <Send className="h-5 w-5" />
                        </Button>
                    )}
                </div>
            </div>
        </>
    )
}
