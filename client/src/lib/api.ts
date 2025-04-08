import axios from "axios"

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
})

// Add a request interceptor to include the auth token
api.interceptors.request.use(
  (config) => {
    const userJson = localStorage.getItem("user")
    if (userJson) {
      try {
        const user = JSON.parse(userJson)
        if (user && user.token && user.token.accessToken) {
          config.headers.Authorization = `Bearer ${user.token.accessToken}`
        }
      } catch (error) {
        console.error("Failed to parse user from localStorage:", error)
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// Add a response interceptor to handle common errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const { response } = error

    if (response && response.status === 401) {
      // Unauthorized - clear local storage and redirect to login
      localStorage.removeItem("user")
      window.location.href = "/"
    }

    return Promise.reject(error)
  },
)
