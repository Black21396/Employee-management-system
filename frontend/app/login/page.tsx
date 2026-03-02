"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { authService } from "../services/AuthService";
import { useAuth } from "../context/AuthContext";
import Link from "next/link";

export default function LoginPage() {
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const response = await authService.login(form);
      login(response.token, response.role);
      router.push("/employees");
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card">
            <div className="card-body">
              <h2 className="card-title text-center mb-4">Login</h2>
              {error && <div className="alert alert-danger">{error}</div>}
              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label className="form-label">Username oder Email</label>
                  <input
                    type="text"
                    className="form-control"
                    value={form.username}
                    onChange={(e) =>
                      setForm({ ...form, username: e.target.value })
                    }
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Passwort</label>
                  <input
                    type="password"
                    className="form-control"
                    value={form.password}
                    onChange={(e) =>
                      setForm({ ...form, password: e.target.value })
                    }
                    required
                  />
                </div>
                <button
                  type="submit"
                  className="btn btn-primary w-100"
                  disabled={loading}
                >
                  {loading ? "Logging..." : "Login"}
                </button>
              </form>
              <div className="text-center mt-3">
                <Link href="/register">
                  You don't have an account? Register
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
