import { redirect } from "next/navigation"
import { getSession } from "@/lib/auth"
import Link from "next/link"
import { Button } from "@/components/ui/button"

export default async function Home() {
  const session = await getSession()

  if (session) {
    redirect("/dashboard")
  }

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-4 bg-gray-50">
      <div className="w-full max-w-md text-center">
        <h1 className="text-3xl font-bold">Telegram</h1>
        <p className="text-gray-500 mt-2 mb-8">Đăng nhập tài khoản của bạn hoặc tạo mới</p>

        <div className="flex flex-col gap-4">
          <Link href="/auth/login" className="w-full">
            <Button className="w-full" size="lg">
              Đăng nhập
            </Button>
          </Link>
          <Link href="/auth/register" className="w-full">
            <Button variant="outline" className="w-full hover:bg-slate-50 hover:text-black" size="lg">
              Đăng ký
            </Button>
          </Link>
        </div>
      </div>
    </main>
  )
}
