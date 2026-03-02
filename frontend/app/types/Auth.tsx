export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  role?: "USER" | "ADMIN";
}

export interface LoginRequest {
  email?: string;
  username?: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  role: string;
}

export interface User {
  username: string;
  role: "USER" | "ADMIN";
}
