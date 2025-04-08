import { cookies } from "next/headers"
import type { AccountResponse } from "@/types/auth"

export async function getSession(): Promise<AccountResponse | null> {
  const cookieStore = cookies()
  const userCookie = cookieStore.get("user")

  if (!userCookie?.value) {
    return null
  }

  try {
    const user = JSON.parse(userCookie.value) as AccountResponse

    // Check if token is expired
    const tokenExpiry = new Date(user.token.accessTokenExpiry)
    if (tokenExpiry < new Date()) {
      return null
    }

    return user
  } catch (error) {
    console.error("Failed to parse user cookie:", error)
    return null
  }
}
