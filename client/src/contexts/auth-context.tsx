"use client"

import { createContext, useEffect, useState, type ReactNode } from "react"
import type { LoginRequest, RegisterRequest, Account } from "@/types/auth"
import { api } from "@/lib/api"

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

        const tokenExpiry = new Date(parsedUser.token.accessTokenExpiry)
        if (tokenExpiry < new Date()) {
          logout()
        }
      } catch (error) {
        localStorage.removeItem("user")
      }
    }
    setIsInitialized(true)
  }, [])

  const login = async (data: LoginRequest) => {
    setIsLoading(true)
    try {
      const response = await api.post<Account>("/api/v1/account/login", data)
      console.log("Login response:", response)
      setUser(response)
      localStorage.setItem("user", JSON.stringify(response))
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
