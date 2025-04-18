"use client"

import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import { loginUser, registerUser, getCurrentUser, logoutUser, initializeWebSocket, closeWebSocket } from "@/lib/api"

interface User {
  id: string
  name: string
  email: string
}

interface AuthContextType {
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (email: string, password: string) => Promise<void>
  register: (name: string, email: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const userData = await getCurrentUser()
        setUser(userData)

        // Initialize WebSocket connection if user is authenticated
        if (userData) {
          initializeWebSocket()
        }
      } catch (error) {
        setUser(null)
      } finally {
        setIsLoading(false)
      }
    }

    checkAuth()

    // Clean up WebSocket connection when component unmounts
    return () => {
      closeWebSocket()
    }
  }, [])

  const login = async (email: string, password: string) => {
    const userData = await loginUser(email, password)
    setUser(userData)

    // Initialize WebSocket connection after successful login
    initializeWebSocket()
  }

  const register = async (name: string, email: string, password: string) => {
    const userData = await registerUser(name, email, password)
    setUser(userData)

    // Initialize WebSocket connection after successful registration
    initializeWebSocket()
  }

  const logout = async () => {
    await logoutUser()
    setUser(null)

    // Close WebSocket connection on logout
    closeWebSocket()
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
