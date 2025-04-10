"use client"

import { cn } from "@/lib/utils"
import Image from "next/image"
import type { ChatListItemProps } from "@/types/chat"

export default function ChatListItem({ chat, isSelected, onClick }: ChatListItemProps) {
    return (
        <div
            className={cn(
                "flex items-center p-3 cursor-pointer hover:bg-gray-100 transition-colors",
                isSelected && "bg-blue-50 hover:bg-blue-50",
            )}
            onClick={onClick}
        >
            {/* Avatar */}
            <div className="relative h-12 w-12 rounded-full overflow-hidden mr-3">
                <Image src={chat.avatar || "/placeholder.svg"} alt={chat.name} fill className="object-cover" />
            </div>

            {/* Chat info */}
            <div className="flex-1 min-w-0">
                <div className="flex justify-between items-center">
                    <h3 className="font-medium text-gray-900 truncate">{chat.name}</h3>
                    <span className="text-xs text-gray-500">{chat.time}</span>
                </div>
                <div className="flex justify-between items-center mt-1">
                    <p className="text-sm text-gray-500 truncate">{chat.lastMessage}</p>
                    {chat.unread > 0 && (
                        <span className="flex items-center justify-center bg-blue-500 text-white text-xs rounded-full h-5 min-w-5 px-1">
                            {chat.unread}
                        </span>
                    )}
                </div>
            </div>
        </div>
    )
}
