"use client"

import {
    createContext,
    useContext,
    useState,
    useEffect,
    type ReactNode,
} from "react"
import { useAuth } from "@/contexts/auth-context"
import { api } from "@/lib/api"
import { User } from "@/types/user"

const defaultUser: User = {
    id: "",
    name: "Guest",
    avatar: "https://example.com/default-avatar.jpg",
    email: undefined,
    phone: undefined,
    status: "offline",
    lastSeen: undefined,
}

interface UserContextType {
    user: User
    isLoading: boolean
    updateUser: (updates: Partial<User>) => Promise<void>
}

export const UserContext = createContext<UserContextType>({
    user: defaultUser,
    isLoading: false,
    updateUser: async () => { },
})

interface UserProviderProps {
    children: ReactNode
}

export function UserProvider({ children }: UserProviderProps) {
    const { user: authUser, isAuthenticated, isInitialized, logout } = useAuth()
    const [user, setUser] = useState<User>(defaultUser)
    const [isLoading, setIsLoading] = useState<boolean>(true)

    useEffect(() => {
        const loadUser = async () => {
            if (!isInitialized) {
                return
            }

            const storedUser = localStorage.getItem("user")
            if (storedUser && authUser?.token.accessToken) {
                try {
                    const parsedUser = JSON.parse(storedUser) as User
                    setUser(parsedUser)
                    setIsLoading(false)
                    await fetchUserProfile()
                    return
                } catch (error) {
                    console.error("Failed to parse user from localStorage:", error)
                    localStorage.removeItem("user")
                }
            }

            if (!isAuthenticated || !authUser?.token.accessToken) {
                setUser(defaultUser)
                localStorage.removeItem("user")
                setIsLoading(false)
                return
            }

            await fetchUserProfile()
        }

        const fetchUserProfile = async () => {
            if (!isInitialized || !authUser?.token.accessToken) {
                return
            }

            setIsLoading(true)
            try {
                const response = await api.get<User>("/api/v1/user/profile")
                setUser(response)
                localStorage.setItem("userProfile", JSON.stringify(response))
            } catch (error: any) {
                console.error("Failed to fetch user profile:", error)
                if (error.response?.status === 401) {
                    logout()
                    setUser(defaultUser)
                    localStorage.removeItem("userProfile")
                }
            } finally {
                setIsLoading(false)
            }
        }

        loadUser()
    }, [isInitialized, isAuthenticated, authUser, logout])

    const updateUser = async (updates: Partial<User>) => {
        if (!isAuthenticated || !authUser?.token.accessToken) {
            throw new Error("Not authenticated")
        }

        setIsLoading(true)
        try {
            const response = await api.patch<User>("/api/v1/user/profile", updates)
            setUser(response.data)
            localStorage.setItem("user", JSON.stringify(response))
        } catch (error: any) {
            console.error("Failed to update user profile:", error)
            throw error
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <UserContext.Provider
            value={{
                user,
                isLoading,
                updateUser,
            }}
        >
            {children}
        </UserContext.Provider>
    )
}

export function useUser() {
    const context = useContext(UserContext)
    if (!context) {
        throw new Error("useUser must be used within a UserProvider")
    }
    return context
}