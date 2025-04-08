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
  phoneNumber: z.string().min(1, "Số điện thoại là bắt buộc"),
  password: z.string().min(6, "Mật khẩu phải có ít nhất 6 ký tự"),
  userName: z.string().min(1, "Username là bắt buộc"),
  fullName: z.string().min(1, "Họ và tên là bắt buộc"),
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
          setError("Số điện thoại này đã được đăng ký. Vui lòng chọn số khác.")
        } else if (err.field === "USERNAME") {
          setError("Tên tài khoản này đã được sử dụng. Vui lòng chọn tên khác.")
        } else {
          setError("Tài khoản này đã được đăng ký. Vui lòng chọn số khác.")
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
                    <Input placeholder="Nhập số điện thoại" {...field} />
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
                    <Input placeholder="Nhập username" {...field} />
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
                    <Input placeholder="Nhập họ và tên" {...field} />
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
                    <Input type="password" placeholder="Nhập mật khẩu" {...field} />
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
