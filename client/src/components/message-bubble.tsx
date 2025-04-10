import { cn } from "@/lib/utils"
import type { MessageBubbleProps } from "@/types/message"

export default function MessageBubble({ message, isGroup }: MessageBubbleProps) {
    const isMe = message.sender === "me"

    return (
        <div className={cn("flex mb-3", isMe ? "justify-end" : "justify-start")}>
            <div
                className={cn(
                    "max-w-[75%] rounded-lg p-3",
                    isMe ? "bg-blue-500 text-white rounded-tr-none" : "bg-white text-gray-800 rounded-tl-none shadow-sm",
                )}
            >
                {isGroup && !isMe && message.name && (
                    <div className="font-medium text-blue-600 text-sm mb-1">{message.name}</div>
                )}
                <p className="text-sm">{message.text}</p>
                <div className={cn("text-xs mt-1", isMe ? "text-blue-100" : "text-gray-500")}>{message.time}</div>
            </div>
        </div>
    )
}
