import { Employee } from "../types/Employee";
import { authService } from "./AuthService";

const BASE_URL =
  process.env.BACKEND_EMPLOYEE_API_URL || "http://localhost:8080/api/employees";

const getHeaders = () => {
  const token = authService.getToken();
  return {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }),
  };
};

export const employeeService = {
  getAll: async (): Promise<Employee[]> => {
    const res = await fetch(BASE_URL, { headers: getHeaders() });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  getById: async (id: number): Promise<Employee> => {
    const res = await fetch(`${BASE_URL}/${id}`, { headers: getHeaders() });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  create: async (data: Employee): Promise<Employee> => {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  update: async (id: number, data: Employee): Promise<Employee> => {
    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "PATCH",
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  delete: async (id: number): Promise<void> => {
    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "DELETE",
      headers: getHeaders(),
    });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
  },
};
