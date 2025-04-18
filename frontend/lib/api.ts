import axios from "axios"
import { mockUsers, mockConversations, mockMessages, generateId, getCurrentTimestamp } from "./mock-data"
import { mockWebSocket, WebSocketEventType } from "./websocket"

// Create an axios instance with default config
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:3001/api",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Important for cookies/sessions
})

// Add a request interceptor to include auth token in all requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token")
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// Current user state (for mock data)
let currentUser = null

// Authentication APIs with mock implementation
export const loginUser = async (email: string, password: string) => {
  // For real API:
  // const response = await api.post("/auth/login", { email, password })
  // localStorage.setItem("token", response.data.token)
  // return response.data.user

  // Mock implementation:
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const user = mockUsers.find((u) => u.email === email && u.password === password)
      if (user) {
        const { password, ...userWithoutPassword } = user
        currentUser = userWithoutPassword
        localStorage.setItem("token", "mock-token-" + user.id)
        resolve(userWithoutPassword)
      } else {
        reject(new Error("Invalid email or password"))
      }
    }, 500) // Simulate network delay
  })
}

export const registerUser = async (name: string, email: string, password: string) => {
  // For real API:
  // const response = await api.post("/auth/register", { name, email, password })
  // localStorage.setItem("token", response.data.token)
  // return response.data.user

  // Mock implementation:
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const existingUser = mockUsers.find((u) => u.email === email)
      if (existingUser) {
        reject(new Error("Email already in use"))
      } else {
        const newUser = {
          id: `user${mockUsers.length + 1}`,
          name,
          email,
          password,
        }
        mockUsers.push(newUser)
        const { password: _, ...userWithoutPassword } = newUser
        currentUser = userWithoutPassword
        localStorage.setItem("token", "mock-token-" + newUser.id)
        resolve(userWithoutPassword)
      }
    }, 500) // Simulate network delay
  })
}

export const getCurrentUser = async () => {
  // For real API:
  // const response = await api.get("/auth/me")
  // return response.data

  // Mock implementation:
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const token = localStorage.getItem("token")
      if (token) {
        const userId = token.replace("mock-token-", "")
        const user = mockUsers.find((u) => u.id === userId)
        if (user) {
          const { password, ...userWithoutPassword } = user
          currentUser = userWithoutPassword
          resolve(userWithoutPassword)
        } else {
          localStorage.removeItem("token")
          reject(new Error("User not found"))
        }
      } else {
        reject(new Error("Not authenticated"))
      }
    }, 300) // Simulate network delay
  })
}

export const logoutUser = async () => {
  // For real API:
  // await api.post("/auth/logout")
  // localStorage.removeItem("token")

  // Mock implementation:
  return new Promise((resolve) => {
    setTimeout(() => {
      localStorage.removeItem("token")
      currentUser = null
      resolve(null)
    }, 300) // Simulate network delay
  })
}

// Conversation APIs with mock implementation
export const fetchConversations = async () => {
  // For real API:
  // const response = await api.get("/conversations")
  // return response.data

  // Mock implementation:
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!currentUser) {
        reject(new Error("Not authenticated"))
        return
      }

      // Filter conversations where the current user is a participant
      const userConversations = mockConversations.filter((conv) => conv.participants.includes(currentUser.id))
      resolve(userConversations)
    }, 500) // Simulate network delay
  })
}

export const searchConversations = async (query: string) => {
  // For real API:
  // const response = await api.get(`/conversations/search?q=${query}`)
  // return response.data

  // Mock implementation:
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!currentUser) {
        reject(new Error("Not authenticated"))
        return
      }

      // Filter conversations where the current user is a participant
      // and the name matches the query
      const userConversations = mockConversations.filter(
        (conv) => conv.participants.includes(currentUser.id) && conv.name.toLowerCase().includes(query.toLowerCase()),
      )
      resolve(userConversations)
    }, 500) // Simulate network delay
  })
}

// Messages APIs with mock implementation
export const fetchMessages = async (conversationId: string) => {
  // For real API:
  // const response = await api.get(`/conversations/${conversationId}/messages`)
  // return response.data

  // Mock implementation:
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!currentUser) {
        reject(new Error("Not authenticated"))
        return
      }

      const conversation = mockConversations.find((c) => c.id === conversationId)
      if (!conversation) {
        reject(new Error("Conversation not found"))
        return
      }

      if (!conversation.participants.includes(currentUser.id)) {
        reject(new Error("Not authorized to view this conversation"))
        return
      }

      const messages = mockMessages[conversationId] || []
      resolve(messages)
    }, 500) // Simulate network delay
  })
}

export const sendMessage = async (conversationId: string, text: string) => {
  // For real API:
  // const response = await api.post(`/conversations/${conversationId}/messages`, { text })
  // return response.data

  // Mock implementation:
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!currentUser) {
        reject(new Error("Not authenticated"))
        return
      }

      const conversation = mockConversations.find((c) => c.id === conversationId)
      if (!conversation) {
        reject(new Error("Conversation not found"))
        return
      }

      if (!conversation.participants.includes(currentUser.id)) {
        reject(new Error("Not authorized to send message to this conversation"))
        return
      }

      const newMessage = {
        id: generateId(),
        conversationId,
        senderId: currentUser.id,
        text,
        timestamp: getCurrentTimestamp(),
      }

      // Add message to mock data
      if (!mockMessages[conversationId]) {
        mockMessages[conversationId] = []
      }
      mockMessages[conversationId].push(newMessage)

      // Update last message in conversation
      conversation.lastMessage = text
      conversation.timestamp = newMessage.timestamp

      // Send message via WebSocket
      mockWebSocket.send({
        type: WebSocketEventType.NEW_MESSAGE,
        payload: newMessage,
      })

      resolve(newMessage)
    }, 300) // Simulate network delay
  })
}

// Initialize WebSocket connection
export const initializeWebSocket = () => {
  return mockWebSocket.connect()
}

// Close WebSocket connection
export const closeWebSocket = () => {
  mockWebSocket.disconnect()
}
