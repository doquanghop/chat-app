"use client";

import { useEffect, useState, useCallback } from "react";
import { api } from "@/lib/api";
import { connectWebSocket, disconnectWebSocket } from "@/lib/websocket";
import {
    MessageResponse,
    PageResponse,
    ConversationEvent,
    SendMessageRequest,
} from "@/types";
import { useAuth } from "@/contexts/auth-context";
import MessageInput from "./message-input";

interface ChatAreaProps {
    conversationId: string;
}

export default function ChatArea({ conversationId }: ChatAreaProps) {
    const { user } = useAuth();
    const [messages, setMessages] = useState<MessageResponse[]>([]);
    const [loading, setLoading] = useState(true);

    // Fetch initial messages
    useEffect(() => {
        const fetchMessages = async () => {
            try {
                const response = await api.get<
                    PageResponse<MessageResponse>
                >(`/api/v1/message`, {
                    params: { conversationId, page: 0, size: 50 },
                });
                setMessages(response.content.reverse());
            } catch (error) {
                console.error("Failed to fetch messages:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchMessages();
    }, [conversationId]);

    // WebSocket connection
    useEffect(() => {
        if (!user?.token.accessToken) return;

        const handleMessageReceived = (
            event: ConversationEvent<MessageResponse>
        ) => {
            setMessages((prev) => [...prev, event.payload]);
        };

        connectWebSocket(
            conversationId,
            user.token.accessToken,
            handleMessageReceived,
            () => console.log("WebSocket connected"),
            (error) => console.error("WebSocket error:", error)
        );

        return () => {
            disconnectWebSocket();
        };
    }, [conversationId, user?.token.accessToken]);

    // Send message via API
    const handleSendMessage = useCallback(
        async (content: string) => {
            if (!user?.token.accessToken || !content.trim()) return;

            try {
                const request: SendMessageRequest = { content };
                await api.post<MessageResponse>(
                    `/api/v1/message/conversation/${conversationId}`,
                    request
                );
                // Note: Message will be received via WebSocket, so no need to update state here
            } catch (error) {
                console.error("Failed to send message:", error);
            }
        },
        [conversationId, user?.token.accessToken]
    );

    if (loading) return <div className="p-4">Loading...</div>;

    return (
        <div className="flex-1 flex flex-col">
            <div className="flex-1 p-4 overflow-y-auto">
                {messages.map((msg) => (
                    <div
                        key={msg.id}
                        className={`mb-4 ${msg.sender.id === user?.id ? "text-right" : "text-left"
                            }`}
                    >
                        <div
                            className={`inline-block p-2 rounded ${msg.sender.id === user?.id ? "bg-blue-500 text-white" : "bg-gray-200"
                                }`}
                        >
                            <p>{msg.content}</p>
                            <p className="text-xs opacity-70">
                                {new Date(msg.createdAt).toLocaleTimeString()}
                            </p>
                        </div>
                    </div>
                ))}
            </div>
            <MessageInput onSend={handleSendMessage} />
        </div>
    );
}