"use client";

import ChatArea from "@/components/chat-area";
import Sidebar from "@/components/sidebar";
import { useAuth } from "@/contexts/auth-context";


export default function ChatPage({
    params,
}: {
    params: { conversationId: string };
}) {
    const { isAuthenticated, isInitialized } = useAuth();

    if (!isInitialized) return <div>Loading...</div>;
    if (!isAuthenticated) {
        // Redirect to login page (not implemented here)
        return <div>Please log in to view this page.</div>;
    }

    return (
        <div className="flex h-screen">
            <Sidebar />
            <ChatArea conversationId={params.conversationId} />
        </div>
    );
}