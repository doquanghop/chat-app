export interface UserResponse {
    id: string;
    username: string;
    // Add other fields as needed
}

export interface MessageResponse {
    id: string;
    sender: UserResponse;
    content: string;
    createdAt: string;
}

export interface ConversationResponse {
    id: string;
    name: string;
    // Add other fields as needed (e.g., participants)
}

export interface SendMessageRequest {
    content: string;
}

export interface PageResponse<T> {
    data: T[];
    currentPage: number;
    pageSize: number;
    totalPages: number;
    totalElements: number;
}

export interface ConversationEvent<T> {
    requestId: string;
    type: ConversationEventType;
    payload: T;
}

export enum ConversationEventType {
    MESSAGE = "MESSAGE",
    MEMBER_JOIN = "MEMBER_JOIN",
    MEMBER_LEAVE = "MEMBER_LEAVE",
    MEMBER_UPDATE = "MEMBER_UPDATE",
    CONVERSATION_UPDATE = "CONVERSATION_UPDATE",
}