'use client'

import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

interface WebSocketMessage {
    type: string;
    payload: string;
    timestamp: number;
}

export default function WebSocketTest() {
    const [client, setClient] = useState<Client | null>(null);
    const [isConnected, setIsConnected] = useState(false);
    const [message, setMessage] = useState("");
    const [receivedMessages, setReceivedMessages] = useState<WebSocketMessage[]>([]);
    const [token, setToken] = useState<string>(
        "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLXNlcnZpY2UiLCJzdWIiOiI5ZDdkOTQyZi04NWMwLTRmYWUtYjdjYy0yOGEwNTFhZmIwNmUiLCJyb2xlIjpbIlVTRVIiXSwiZXhwIjoxNzQ0MzU4NDc0LCJpYXQiOjE3NDQyNzIwNzQsImp0aSI6ImUzNjZjYjhlLTdjZGYtNDAxOC05ZWQ4LWY2NTM4ZWNjYTViMiJ9.jC3c_eGVJ-_glzMnXxzRh900EtbJhFaZEdsyY_QtBu0"
    );

    // Hàm giả lập refresh token (thay bằng API thực tế nếu có)
    const refreshToken = async (): Promise<string> => {
        console.log("Refreshing token...");
        return token; // Thay bằng logic lấy token mới từ server
    };

    // Hàm khởi tạo và cấu hình STOMP client
    const initializeClient = (currentToken: string) => {
        const stompClient = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
            connectHeaders: {
                Authorization: `Bearer ${currentToken}`,
            },
            reconnectDelay: 5000, // Reconnect tự động sau 5 giây nếu ngắt (giữ để xử lý lỗi mạng)
            heartbeatIncoming: 4000, // Nhận heartbeat từ server
            heartbeatOutgoing: 4000, // Gửi heartbeat tới server
            onConnect: () => {
                console.log("Connected to WebSocket");
                setIsConnected(true);
                stompClient.subscribe("/topic/messages", (msg) => {
                    const parsedMessage: WebSocketMessage = JSON.parse(msg.body);
                    console.log("Received:", parsedMessage);
                    setReceivedMessages((prev) => [...prev, parsedMessage]);
                });
            },
            onDisconnect: () => {
                console.log("Disconnected from WebSocket");
                setIsConnected(false);
            },
            onStompError: async (frame) => {
                console.error("STOMP error:", frame);
                if (frame.headers?.message?.includes("token")) {
                    const newToken = await refreshToken();
                    setToken(newToken);
                    stompClient.deactivate();
                    initializeClient(newToken).activate();
                }
            },
            onWebSocketClose: () => {
                console.log("WebSocket closed");
                setIsConnected(false);
            },
            debug: (str) => {
                console.log("STOMP Debug:", str);
            },
        });
        return stompClient;
    };

    useEffect(() => {
        const stompClient = initializeClient(token);
        stompClient.activate();
        setClient(stompClient);

        // Cleanup khi component unmount
        return () => {
            stompClient.deactivate();
            console.log("Client deactivated");
        };
    }, []); // Dependency array rỗng để chỉ chạy một lần khi mount

    const sendMessage = () => {
        if (client && client.connected && message.trim()) {
            const wsMessage: WebSocketMessage = {
                type: "chat",
                payload: message,
                timestamp: Date.now(),
            };
            client.publish({
                destination: "/app/send",
                body: JSON.stringify(wsMessage),
            });
            setMessage("");
        } else {
            console.warn("Cannot send message: Not connected or empty message");
        }
    };

    return (
        <div style={{ padding: "20px" }}>
            <h1>WebSocket Test</h1>
            <p>Status: {isConnected ? "Connected" : "Disconnected"}</p>
            <div>
                <h3>Messages:</h3>
                {receivedMessages.length > 0 ? (
                    <ul>
                        {receivedMessages.map((msg, idx) => (
                            <li key={idx}>
                                {msg.payload} - {new Date(msg.timestamp).toLocaleTimeString()}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No messages yet</p>
                )}
            </div>
            <div>
                <input
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="Type a message"
                    disabled={!isConnected}
                    style={{ marginRight: "10px" }}
                />
                <button onClick={sendMessage} disabled={!isConnected}>
                    Send
                </button>
            </div>
        </div>
    );
}