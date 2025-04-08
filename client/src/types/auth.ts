export interface LoginRequest {
  phoneNumber: string
  password: string
}

export interface RegisterRequest {
  phoneNumber: string
  password: string
  userName: string
  fullName: string
}

export interface Token {
  accountId: string
  accessToken: string
  accessTokenExpiry: Date
  refreshToken: string
  refreshTokenExpiry: Date
}

export interface Account {
  id: string
  phoneNumber: string
  token: Token
}
