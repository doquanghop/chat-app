// WebSocket event types
export enum WebSocketEventType {
  CONNECT = "connect",
  DISCONNECT = "disconnect",
  NEW_MESSAGE = "new_message",
  USER_TYPING = "user_typing",
  USER_ONLINE = "user_online",
  USER_OFFLINE = "user_offline",
}

// WebSocket event interface
export interface WebSocketEvent {
  type: WebSocketEventType
  payload: any
}

// Mock WebSocket class to simulate real-time communication
class MockWebSocket {
  private listeners: { [key: string]: ((event: any) => void)[] } = {}
  private connected = false
  private static instance: MockWebSocket | null = null

  // Singleton pattern
  public static getInstance(): MockWebSocket {
    if (!MockWebSocket.instance) {
      MockWebSocket.instance = new MockWebSocket()
    }
    return MockWebSocket.instance
  }

  // Connect to the mock WebSocket server
  connect() {
    if (!this.connected) {
      this.connected = true
      setTimeout(() => {
        this.emit("open", {})
      }, 500)
      console.log("WebSocket connected")
    }
    return this
  }

  // Disconnect from the mock WebSocket server
  disconnect() {
    if (this.connected) {
      this.connected = false
      this.emit("close", {})
      console.log("WebSocket disconnected")
    }
  }

  // Add event listener
  on(event: string, callback: (event: any) => void) {
    if (!this.listeners[event]) {
      this.listeners[event] = []
    }
    this.listeners[event].push(callback)
    return this
  }

  // Remove event listener
  off(event: string, callback: (event: any) => void) {
    if (this.listeners[event]) {
      this.listeners[event] = this.listeners[event].filter((cb) => cb !== callback)
    }
    return this
  }

  // Emit event to all listeners
  private emit(event: string, data: any) {
    if (this.listeners[event]) {
      this.listeners[event].forEach((callback) => callback(data))
    }
  }

  // Send message to the mock WebSocket server
  send(event: WebSocketEvent) {
    if (!this.connected) {
      console.error("WebSocket is not connected")
      return
    }

    console.log("Sending WebSocket event:", event)

    // Simulate server processing and response
    setTimeout(() => {
      // Handle different event types
      switch (event.type) {
        case WebSocketEventType.NEW_MESSAGE:
          // Echo the message back as if it came from the server
          this.emit("message", {
            data: JSON.stringify({
              type: WebSocketEventType.NEW_MESSAGE,
              payload: event.payload,
            }),
          })
          break
        case WebSocketEventType.USER_TYPING:
          // Echo typing notification
          this.emit("message", {
            data: JSON.stringify({
              type: WebSocketEventType.USER_TYPING,
              payload: event.payload,
            }),
          })
          break
        default:
          console.log("Unhandled event type:", event.type)
      }
    }, 200) // Simulate network delay
  }

  // Check if WebSocket is connected
  isConnected() {
    return this.connected
  }
}

// Export singleton instance
export const mockWebSocket = MockWebSocket.getInstance()

// Helper function to parse WebSocket message data
export function parseWebSocketMessage(event: MessageEvent): WebSocketEvent {
  try {
    return JSON.parse(event.data)
  } catch (error) {
    console.error("Failed to parse WebSocket message:", error)
    return {
      type: WebSocketEventType.CONNECT,
      payload: {},
    }
  }
}
