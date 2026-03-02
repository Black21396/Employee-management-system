"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Employee } from "../types/Employee";
import { employeeService } from "../services/EmployeeService";
import { useAuth } from "../context/AuthContext";

export default function EmployeeListPage() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();
  const { isAuthenticated, isAdmin, logout } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/");
      return;
    }

    employeeService
      .getAll()
      .then(setEmployees)
      .catch((err) => {
        if (err.message.includes("401")) {
          logout();
          router.push("/");
        }
      })
      .finally(() => setLoading(false));
  }, [isAuthenticated, router, logout]);

  const handleDelete = async (id: number) => {
    if (!confirm("Delete this employee?")) return;
    await employeeService.delete(id);
    setEmployees((prev) => prev.filter((e) => e.id !== id));
  };

  if (loading) return <p className="text-center mt-5">Loading...</p>;

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Employees {isAdmin ? "(Admin View)" : "(User View)"}</h2>
        <div>
          {isAdmin && (
            <button
              className="btn btn-primary me-2"
              onClick={() => router.push("/employees/new")}
            >
              + New Employee
            </button>
          )}
        </div>
      </div>
      <table className="table table-bordered table-hover">
        <thead className="table-dark">
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            {isAdmin && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {employees.map((emp) => (
            <tr key={emp.id}>
              <td>{emp.id}</td>
              <td>{emp.firstName}</td>
              <td>{emp.lastName}</td>
              <td>{emp.email}</td>
              {isAdmin && (
                <td>
                  <button
                    className="btn btn-sm btn-warning me-2"
                    onClick={() => router.push(`/employees/${emp.id}/edit`)}
                  >
                    Edit
                  </button>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDelete(emp.id!)}
                  >
                    Delete
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
