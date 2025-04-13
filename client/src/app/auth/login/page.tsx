import { redirect } from "next/navigation"
import { getSession } from "@/lib/auth"
import LoginForm from "@/components/auth/login-form"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Metadata } from "next"


export const metadata: Metadata = {
  title: "Đăng nhập | Telegram",
  description: "Đăng nhập vào tài khoản của bạn",
  openGraph: {
    title: "Đăng nhập",
    description: "Đăng nhập vào tài khoản của bạn",
    url: "/auth/login",
    images: [
      {
        url: "/favicon.icon",
        width: 800,
        height: 600,
      },
    ],
  },
}
export default async function LoginPage() {
  const session = await getSession()

  if (session) {
    redirect("/chat")
  }

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-4 bg-gray-50">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold">Đăng nhập</h1>
          <p className="text-gray-500 mt-2">Đăng nhập vào tài khoản của bạn</p>
        </div>
        <LoginForm />
        <div className="text-center mt-6">
          <p className="text-sm text-gray-500">Bạn không có tài khoản?</p>
          <Link href="/register">
            <Button variant="link">Đăng ký tại đây</Button>
          </Link>
        </div>
      </div>
    </main>
  )
}
