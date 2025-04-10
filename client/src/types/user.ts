
export interface User {
    id: string;
    name: string;
    avatar: string;
    email?: string;
    phone?: string;
    status?: "online" | "offline" | "away";
    lastSeen?: string;
}

export interface UserProfileProps {
    user: User;
    onLogout: () => void;
}
