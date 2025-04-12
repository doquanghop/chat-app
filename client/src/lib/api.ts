import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";

export interface ApiResponse<T> {
  code: number;
  isSuccess: boolean;
  message: string;
  data: T;
  errors: Record<string, any>;
  timestamp: Date;
}
interface CustomAxiosInstance extends AxiosInstance {
  get<T = any, R = T>(url: string, config?: AxiosRequestConfig): Promise<R>;
  post<T = any, R = T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<R>;
  put<T = any, R = T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<R>;
  delete<T = any, R = T>(url: string, config?: AxiosRequestConfig): Promise<R>;
}
export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
}) as CustomAxiosInstance;


api.interceptors.request.use(
  (config) => {
    console.log("Request config:", config);
    const userJson = localStorage.getItem("account");
    if (userJson) {
      try {
        const user = JSON.parse(userJson);
        if (user && user.token && user.token.accessToken) {
          config.headers.Authorization = `Bearer ${user.token.accessToken}`;
        }
      } catch (error) {
        console.error("Failed to parse user from localStorage:", error);
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => {
    console.log("Response data:", response.data);
    const apiResponse = response.data as ApiResponse<any>;
    if (!apiResponse.isSuccess) {
      const error = new Error(apiResponse.message || "Request failed");
      error.name = "ApiError";
      // @ts-ignore
      error.code = apiResponse.code;
      // @ts-ignore
      error.errors = apiResponse.errors;

      return Promise.reject(error);
    }
    return Promise.resolve(apiResponse.data);
  },
  (error) => {
    const apiResponse = error.response?.data as ApiResponse<any>;

    return Promise.reject(apiResponse);
  }
);
