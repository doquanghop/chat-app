"use client"

import { createContext, useEffect, useState, type ReactNode } from "react"
import type { LoginRequest, RegisterRequest, Account } from "@/types/auth"
import { api } from "@/lib/api"

interface AuthError extends Error {
  code?: string
  field?: string
}

interface AuthContextType {
  user: Account | null
  isLoading: boolean
  isAuthenticated: boolean
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoading: false,
  isAuthenticated: false,
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
    const storedUser = localStorage.getItem("user")
    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser) as Account
        setUser(parsedUser)

        // Check if token is expired
        const tokenExpiry = new Date(parsedUser.token.accessTokenExpiry)
        if (tokenExpiry < new Date()) {
          // Token expired, log out
          logout()
        }
      } catch (error) {
        console.error("Failed to parse stored user:", error)
        localStorage.removeItem("user")
      }
    }
    setIsInitialized(true)
  }, [])

  const login = async (data: LoginRequest) => {
    setIsLoading(true)
    try {
      const response = await api.post<Account>("/api/v1/account/login", data)
      setUser(response.data)
      localStorage.setItem("user", JSON.stringify(response.data))
    } catch (error: any) {
      console.error("Login failed:", error)

      const authError: AuthError = new Error("Authentication failed")

      if (error.response) {
        const { status, data } = error.response

        if (status === 404) {
          authError.code = "ENTITY_NOT_FOUND"
          authError.message = "Account not found"
        } else if (status === 400) {
          authError.code = "INVALID_PAYLOAD"
          authError.message = "Invalid credentials"
        } else if (data && data.code) {
          authError.code = data.code
          authError.message = data.message || "Authentication failed"
          if (data.field) {
            authError.field = data.field
          }
        }
      }

      throw authError
    } finally {
      setIsLoading(false)
    }
  }

  const register = async (data: RegisterRequest) => {
    setIsLoading(true)
    try {
      await api.post("/api/v1/account/register", data)
    } catch (error: any) {
      console.error("Registration failed:", error)

      const authError: AuthError = new Error("Registration failed")

      if (error.response) {
        const { status, data } = error.response

        if (status === 409 || (data && data.code === "ENTITY_ALREADY_EXISTS")) {
          authError.code = "ENTITY_ALREADY_EXISTS"
          authError.message = "Registration failed. This account already exists."
          if (data && data.field) {
            authError.field = data.field
          }
        } else if (data && data.code) {
          authError.code = data.code
          authError.message = data.message || "Registration failed"
          if (data.field) {
            authError.field = data.field
          }
        }
      }

      throw authError
    } finally {
      setIsLoading(false)
    }
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem("user")
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: !!user,
        login,
        register,
        logout,
      }}
    >
      {isInitialized ? children : null}
    </AuthContext.Provider>
  )
}
