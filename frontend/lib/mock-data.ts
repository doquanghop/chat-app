// Mock user data
export const mockUsers = [
  {
    id: "user1",
    name: "John Doe",
    email: "john@example.com",
    password: "password123", // In a real app, passwords would be hashed
  },
  {
    id: "user2",
    name: "Jane Smith",
    email: "jane@example.com",
    password: "password123",
  },
  {
    id: "user3",
    name: "Bob Johnson",
    email: "bob@example.com",
    password: "password123",
  },
]

// Mock conversation data
export const mockConversations = [
  {
    id: "conv1",
    name: "Jane Smith",
    participants: ["user1", "user2"],
    lastMessage: "Hey, how are you doing?",
    timestamp: "2023-04-15T14:30:00Z",
  },
  {
    id: "conv2",
    name: "Bob Johnson",
    participants: ["user1", "user3"],
    lastMessage: "Can we meet tomorrow?",
    timestamp: "2023-04-15T10:15:00Z",
  },
  {
    id: "conv3",
    name: "Work Group",
    participants: ["user1", "user2", "user3"],
    lastMessage: "The meeting is at 3 PM",
    timestamp: "2023-04-14T16:45:00Z",
  },
]

// Mock message data
export const mockMessages = {
  conv1: [
    {
      id: "msg1",
      conversationId: "conv1",
      senderId: "user2",
      text: "Hey, how are you?",
      timestamp: "2023-04-15T14:25:00Z",
    },
    {
      id: "msg2",
      conversationId: "conv1",
      senderId: "user1",
      text: "I'm good, thanks! How about you?",
      timestamp: "2023-04-15T14:27:00Z",
    },
    {
      id: "msg3",
      conversationId: "conv1",
      senderId: "user2",
      text: "Hey, how are you doing?",
      timestamp: "2023-04-15T14:30:00Z",
    },
  ],
  conv2: [
    {
      id: "msg4",
      conversationId: "conv2",
      senderId: "user3",
      text: "Hi John, are you free tomorrow?",
      timestamp: "2023-04-15T10:10:00Z",
    },
    {
      id: "msg5",
      conversationId: "conv2",
      senderId: "user1",
      text: "Yes, what time?",
      timestamp: "2023-04-15T10:12:00Z",
    },
    {
      id: "msg6",
      conversationId: "conv2",
      senderId: "user3",
      text: "Can we meet tomorrow?",
      timestamp: "2023-04-15T10:15:00Z",
    },
  ],
  conv3: [
    {
      id: "msg7",
      conversationId: "conv3",
      senderId: "user2",
      text: "Hello everyone!",
      timestamp: "2023-04-14T16:30:00Z",
    },
    {
      id: "msg8",
      conversationId: "conv3",
      senderId: "user3",
      text: "We need to discuss the project timeline",
      timestamp: "2023-04-14T16:35:00Z",
    },
    {
      id: "msg9",
      conversationId: "conv3",
      senderId: "user2",
      text: "The meeting is at 3 PM",
      timestamp: "2023-04-14T16:45:00Z",
    },
  ],
}

// Helper function to generate a unique ID
export function generateId() {
  return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)
}

// Helper function to get current timestamp in ISO format
export function getCurrentTimestamp() {
  return new Date().toISOString()
}
