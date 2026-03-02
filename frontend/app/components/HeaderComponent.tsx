"use client";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "../context/AuthContext";

export default function HeaderComponent() {
  const router = useRouter();
  const { isAuthenticated, logout, user } = useAuth();

  const handleLogout = () => {
    logout();
    router.push("/");
  };

  return (
    <nav className="navbar navbar-dark bg-dark">
      <div className="container">
        <Link className="navbar-brand" href="/">
          Employee Management System
        </Link>
        {isAuthenticated && (
          <div className="d-flex align-items-center">
            {user?.role && (
              <span className="text-light me-3">
                Role: <span className="badge bg-primary">{user.role}</span>
              </span>
            )}
            <button
              className="btn btn-outline-light btn-sm"
              onClick={handleLogout}
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}
