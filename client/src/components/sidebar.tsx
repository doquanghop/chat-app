"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { api } from "@/lib/api";
import { ConversationResponse, PageResponse } from "@/types";

export default function Sidebar() {
    const [conversations, setConversations] = useState<ConversationResponse[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchConversations = async () => {
            try {
                const response = await api.get<PageResponse<ConversationResponse>
                >("/api/v1/conversation", {
                    params: { page: 0, pageSize: 20 },
                });
                console.log("Conversations:", response);
                setConversations(response.data);
            } catch (error) {
                console.error("Failed to fetch conversations:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchConversations();
    }, []);

    if (loading) return <div className="p-4">Loading...</div>;

    return (
        <div className="w-64 bg-gray-100 h-screen p-4">
            <h2 className="text-xl font-bold mb-4">Conversations</h2>
            <ul>
                {conversations.map((conv) => (
                    <li key={conv.id} className="mb-2">
                        <Link
                            href={`/chat/${conv.id}`}
                            className="block p-2 hover:bg-gray-200 rounded"
                        >
                            {conv.name || `Conversation ${conv.id}`}
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
}