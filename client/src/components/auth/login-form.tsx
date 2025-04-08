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

const loginSchema = z.object({
  phoneNumber: z.string().min(1, "Số điện thoại là bắt buộc"),
  password: z.string().min(1, "Mật khẩu là bắt buộc"),
})

type LoginFormValues = z.infer<typeof loginSchema>

export default function LoginForm() {
  const { login, isLoading } = useAuth()
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  const form = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      phoneNumber: "",
      password: "",
    },
  })

  const onSubmit = async (data: LoginFormValues) => {
    setError(null)
    try {
      await login(data)
      router.push("/dashboard")
    } catch (err: any) {
      if (err.code === 404) {
        setError("Tài khoản không tồn tại. Vui lòng kiểm tra lại số điện thoại.")
      } else if (err.code === 403) {
        setError("Nhập sai tài khoản hoặc mật khẩu. Vui lòng thử lại.")
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
                  <FormLabel>Số điện thoại</FormLabel>
                  <FormControl>
                    <Input placeholder="Nhập số điện thoại" {...field} />
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
                  <FormLabel>Mật khẩu</FormLabel>
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
                  Đang đăng nhập...
                </>
              ) : (
                "Đăng nhập"
              )}
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  )
}
