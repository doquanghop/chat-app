
export type MessageSender = "me" | "other";

export interface Message {
    id: string;
    text: string;
    sender: MessageSender;
    time: string;
    name?: string;
    isRead?: boolean;
    attachments?: Attachment[];
}

export interface Attachment {
    id: string;
    type: "image" | "file" | "audio" | "video";
    url: string;
    name?: string;
    size?: number;
    thumbnail?: string;
}

export interface MessageBubbleProps {
    message: Message;
    isGroup: boolean;
}

export interface MessageInputProps {
    onSendMessage: (text: string) => void;
    disabled?: boolean;
}
