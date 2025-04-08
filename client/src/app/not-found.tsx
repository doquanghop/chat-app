"use client";

import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import TelegramIcon from "@/components/icon";

export default function NotFound() {
    const router = useRouter();

    return (
        <div className="min-h-screen flex items-center justify-center bg-background">
            <div className="flex flex-col items-center justify-center text-center space-y-6">
                <div className=" h-[50px] w-[50px]">
                    <TelegramIcon />

                </div>
                <h1 className="text-5xl font-bold text-telegram-primary">404</h1>
                <p className="text-lg text-muted-foreground">
                    Không tìm thấy trang bạn đang tìm kiếm. Có thể đã bị xóa hoặc không còn tồn tại.
                </p>

                <Button
                    onClick={() => router.push("/")}
                    className="bg-telegram-primary hover:bg-telegram-dark text-white font-medium px-6 py-2 rounded-md"
                >
                    Trở về trang chủ
                </Button>
            </div>
        </div>
    );
}