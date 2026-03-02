"use client";
import {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
import { authService } from "../services/AuthService";
import { User } from "../types/Auth";

interface AuthContextType {
  user: User | null;
  login: (token: string, role: string) => void;
  logout: () => void;
  isAdmin: boolean;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // Token beim Laden prüfen
    const token = authService.getToken();
    const role = authService.getRole();
    if (token && role) {
      setUser({ username: "", role: role as "USER" | "ADMIN" });
    }
  }, []);

  const login = (token: string, role: string) => {
    authService.saveAuth(token, role);
    setUser({ username: "", role: role as "USER" | "ADMIN" });
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        logout,
        isAdmin: user?.role === "ADMIN",
        isAuthenticated: !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
}
