import { redirect } from "next/navigation"
import { getSession } from "@/lib/auth"
import RegisterForm from "@/components/auth/register-form"
import Link from "next/link"
import { Button } from "@/components/ui/button"

export default async function RegisterPage() {
  const session = await getSession()

  if (session) {
    redirect("/dashboard")
  }

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-4 bg-gray-50">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold">Register</h1>
          <p className="text-gray-500 mt-2">Create a new account</p>
        </div>
        <RegisterForm />
        <div className="text-center mt-6">
          <p className="text-sm text-gray-500">Already have an account?</p>
          <Link href="/login">
            <Button variant="link">Login here</Button>
          </Link>
        </div>
      </div>
    </main>
  )
}
