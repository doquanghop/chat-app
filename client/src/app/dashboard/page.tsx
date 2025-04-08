import { redirect } from "next/navigation"
import { getSession } from "@/lib/auth"

export default async function DashboardPage() {
  const session = await getSession()

  if (!session) {
    redirect("/")
  }

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold mb-6">Dashboard</h1>
      <div className="bg-white p-6 rounded-lg shadow">
        <h2 className="text-xl font-semibold mb-4">Welcome back!</h2>
        <p>You are logged in as: {session.phoneNumber}</p>
      </div>
    </div>
  )
}
