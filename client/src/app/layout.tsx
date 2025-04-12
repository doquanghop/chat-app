import type React from "react"
import type { Metadata } from "next"
import "./globals.css"
import { AuthProvider } from "@/contexts/auth-context"

import { Roboto } from "next/font/google"
const roboto = Roboto({ subsets: ["latin"], weight: ["400", "700"] }) // tuỳ trọng số bạn cần


export const metadata: Metadata = {
  title: "Telegram",
  description: "Telegram Clone",
  generator: 'telegram.com',
  icons: {
    icon: "/favicon.icon",
  }

}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="vi">
      <body className={roboto.className}>
        <AuthProvider>
          {children}
        </AuthProvider>
      </body>
    </html>
  )
}


import './globals.css'
