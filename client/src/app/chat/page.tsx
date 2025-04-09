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
        "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLXNlcnZpY2UiLCJzdWIiOiI5ZDdkOTQyZi04NWMwLTRmYWUtYjdjYy0yOGEwNTFhZmIwNmUiLCJyb2xlIjpbIlVTRVIiXSwiZXhwIjoxNzQ0Mjk5MzAwLCJpYXQiOjE3NDQyMTI5MDAsImp0aSI6ImMxMWJhODQyLWNkMjEtNDM0Yi04MGZjLWNkZGE1YTMwZjRkNSJ9.yRGvtt4yJ7GgsPzdVK-2x9w2Y8RwcPWQFLifOyYnXdU"
    );

    // Hàm giả lập refresh token (thay bằng API thực tế nếu có)
    const refreshToken = async (): Promise<string> => {
        // Giả lập gọi API để lấy token mới
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
            reconnectDelay: 5000, // Reconnect sau 5 giây nếu ngắt
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
                    // Nếu lỗi liên quan đến token, refresh token và reconnect
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
        let stompClient = initializeClient(token);
        stompClient.activate();
        setClient(stompClient);

        // Reconnect định kỳ (ví dụ: mỗi 10 phút)
        const reconnectInterval = setInterval(async () => {
            console.log("Proactively reconnecting...");
            stompClient.deactivate();
            const newToken = await refreshToken(); // Refresh token trước khi reconnect
            setToken(newToken);
            stompClient = initializeClient(newToken);
            stompClient.activate();
            setClient(stompClient);
        }, 1 * 60 * 1000);

        return () => {
            clearInterval(reconnectInterval);
            stompClient.deactivate();
            console.log("Client deactivated");
        };
    }, []);

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