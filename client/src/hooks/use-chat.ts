"use client"

import { useContext } from "react"
import { ChatContext } from "@/contexts/chat-context"

export const useChat = () => useContext(ChatContext)
