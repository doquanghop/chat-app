"use client";

import { useState } from "react";

interface MessageInputProps {
    onSend: (content: string) => void;
}

export default function MessageInput({ onSend }: MessageInputProps) {
    const [content, setContent] = useState("");

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (content.trim()) {
            onSend(content);
            setContent("");
        }
    };

    return (
        <form onSubmit={handleSubmit} className="p-4 border-t">
            <div className="flex">
                <input
                    type="text"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    className="flex-1 p-2 border rounded-l"
                    placeholder="Type a message..."
                />
                <button
                    type="submit"
                    className="p-2 bg-blue-500 text-white rounded-r"
                >
                    Send
                </button>
            </div>
        </form>
    );
}