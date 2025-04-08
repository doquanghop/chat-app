"use client"

import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import * as z from "zod"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import { useAuth } from "@/hooks/use-auth"
import { Loader2 } from "lucide-react"
import { useRouter } from "next/navigation"

const registerSchema = z.object({
  phoneNumber: z.string().min(1, "Phone number is required"),
  password: z.string().min(6, "Password must be at least 6 characters"),
  userName: z.string().min(1, "Username is required"),
  fullName: z.string().min(1, "Full name is required"),
})

type RegisterFormValues = z.infer<typeof registerSchema>

export default function RegisterForm() {
  const { register, isLoading } = useAuth()
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      phoneNumber: "",
      password: "",
      userName: "",
      fullName: "",
    },
  })

  const onSubmit = async (data: RegisterFormValues) => {
    setError(null)
    try {
      await register(data)
      router.push("/login?registered=true")
    } catch (err: any) {
      if (err.code === "ENTITY_ALREADY_EXISTS") {
        if (err.field === "PHONE_NUMBER") {
          setError("This phone number is already registered. Please use a different one.")
        } else if (err.field === "USERNAME") {
          setError("This username is already taken. Please choose a different one.")
        } else {
          setError("An account with these details already exists.")
        }
      } else {
        setError(err.message || "An unexpected error occurred")
      }
    }
  }

  return (
    <Card>
      <CardContent className="pt-6">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            {error && <div className="p-3 bg-red-50 text-red-500 text-sm rounded-md">{error}</div>}
            <FormField
              control={form.control}
              name="phoneNumber"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Phone Number</FormLabel>
                  <FormControl>
                    <Input placeholder="Enter your phone number" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="userName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input placeholder="Choose a username" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="fullName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Full Name</FormLabel>
                  <FormControl>
                    <Input placeholder="Enter your full name" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Password</FormLabel>
                  <FormControl>
                    <Input type="password" placeholder="Create a password" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Registering...
                </>
              ) : (
                "Register"
              )}
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  )
}
