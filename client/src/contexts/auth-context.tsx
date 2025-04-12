"use client"

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import type { LoginRequest, RegisterRequest } from "@/types/auth"
import { api } from "@/lib/api"

// Assuming Account type for reference
export interface Account {
  id: string
  token: {
    accessToken: string
    accessTokenExpiry: string
  }
  // Other fields as needed
}

interface AuthContextType {
  user: Account | null
  isLoading: boolean
  isAuthenticated: boolean
  isInitialized: boolean
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoading: false,
  isAuthenticated: false,
  isInitialized: false,
  login: async () => { },
  register: async () => { },
  logout: () => { },
})

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<Account | null>(null)
  const [isLoading, setIsLoading] = useState<boolean>(false)
  const [isInitialized, setIsInitialized] = useState<boolean>(false)

  useEffect(() => {
    const storedUser = localStorage.getItem("account")
    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser) as Account
        setUser(parsedUser)

        const tokenExpiry = new Date(parsedUser.token.accessTokenExpiry)
        if (tokenExpiry < new Date()) {
          logout()
        }
      } catch (error) {
        console.error("Failed to parse user from localStorage:", error)
        localStorage.removeItem("account")
      }
    }
    setIsInitialized(true)
  }, []) // Added logout to deps since it's called

  const login = async (data: LoginRequest) => {
    setIsLoading(true)
    try {
      const response = await api.post<Account>("/api/v1/account/login", data)
      setUser(response)
      localStorage.setItem("account", JSON.stringify(response))
    } catch (error: any) {
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const register = async (data: RegisterRequest) => {
    setIsLoading(true)
    try {
      await api.post("/api/v1/account/register", data)
    } catch (error: any) {
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem("account")
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: !!user,
        isInitialized,
        login,
        register,
        logout,
      }}
    >
      {isInitialized ? children : <div>Loading...</div>}
    </AuthContext.Provider>
  )
}

// Custom hook to use AuthContext
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}