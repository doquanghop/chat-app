import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { ConversationEvent, MessageResponse } from "@/types";

let stompClient: Client | null = null;

export const connectWebSocket = (
  conversationId: string,
  token: string,
  onMessageReceived: (message: ConversationEvent<MessageResponse>) => void,
  onConnected?: () => void,
  onError?: (error: any) => void
): Client => {
  // Return existing client if already connected
  if (stompClient && stompClient.connected) {
    return stompClient;
  }

  // Create new SockJS WebSocket connection
  const socketFactory = () => new SockJS("http://localhost:8080/ws");
  stompClient = new Client({
    webSocketFactory: socketFactory,
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    debug: (str) => {
      console.log(str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  // Handle connection
  stompClient.onConnect = () => {
    if (stompClient) {
      stompClient.subscribe(
        `/conversation/${conversationId}`,
        (message) => {
          const event: ConversationEvent<MessageResponse> = JSON.parse(message.body);
          if (event.type === "MESSAGE") {
            onMessageReceived(event);
          }
        },
        { Authorization: `Bearer ${token}` } // Add headers to subscription
      );
      onConnected?.();
    }
  };

  // Handle errors
  stompClient.onStompError = (frame) => {
    console.error("Broker reported error: " + frame.headers["message"]);
    console.error("Additional details: " + frame.body);
    onError?.(frame);
  };

  // Handle unexpected disconnect
  stompClient.onWebSocketClose = (event) => {
    console.error("WebSocket closed:", event);
    stompClient = null; // Reset client to allow reconnection
  };

  // Activate the client
  stompClient.activate();

  return stompClient;
};

export const disconnectWebSocket = () => {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
};