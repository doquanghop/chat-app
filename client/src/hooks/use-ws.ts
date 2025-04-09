import WebSocketClient from "@/lib/ws";
import { WebSocketMessage, WebSocketState } from "@/types/ws";
import { useState, useEffect, useRef } from "react";

export const useWebSocket = (url: string, token: string) => {
    const [state, setState] = useState<WebSocketState>({
        isConnected: false,
        lastMessage: null,
    });
    const wsClientRef = useRef<WebSocketClient | null>(null);

    useEffect(() => {
        wsClientRef.current = new WebSocketClient(url, {
            onMessage: (message: WebSocketMessage) =>
                setState((prev) => ({ ...prev, lastMessage: message })),
            onConnect: () => setState((prev) => ({ ...prev, isConnected: true })),
            onDisconnect: () => setState((prev) => ({ ...prev, isConnected: false })),
        });

        wsClientRef.current.connect(token);

        return () => {
            wsClientRef.current?.disconnect();
        };
    }, [url, token]);

    const sendMessage = (message: WebSocketMessage) => {
        wsClientRef.current?.send(message);
    };

    return { state, sendMessage };
};