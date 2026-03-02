import { RegisterRequest, LoginRequest, AuthResponse } from "../types/Auth";

const BASE_URL =
  process.env.BACKEND_AUTH_API_URL || "http://localhost:8080/api/auth";

export const authService = {
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const res = await fetch(`${BASE_URL}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const res = await fetch(`${BASE_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  // get Token from localStorage
  getToken: (): string | null => {
    if (typeof window !== "undefined") {
      return localStorage.getItem("token");
    }
    return null;
  },

  // Save Token to localStorage
  setToken: (token: string): void => {
    if (typeof window !== "undefined") {
      localStorage.setItem("token", token);
    }
  },

  // Save token and User-Roles in localStorage
  saveAuth: (token: string, role: string): void => {
    if (typeof window !== "undefined") {
      localStorage.setItem("token", token);
      localStorage.setItem("role", role);
    }
  },

  getRole: (): string | null => {
    if (typeof window !== "undefined") {
      return localStorage.getItem("role");
    }
    return null;
  },

  // Logout: remove token and role from localStorage
  logout: (): void => {
    if (typeof window !== "undefined") {
      localStorage.removeItem("token");
      localStorage.removeItem("role");
    }
  },

  isAuthenticated: (): boolean => {
    return !!authService.getToken();
  },

  // Prüfen ob Admin
  isAdmin: (): boolean => {
    return authService.getRole() === "ADMIN";
  },
};
