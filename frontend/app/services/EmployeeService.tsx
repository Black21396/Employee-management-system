import { Employee } from "../types/Employee";

const BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/employees";

export const employeeService = {
  getAll: async (): Promise<Employee[]> => {
    const res = await fetch(BASE_URL);
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  getById: async (id: number): Promise<Employee> => {
    const res = await fetch(`${BASE_URL}/${id}`);
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  create: async (data: Employee): Promise<Employee> => {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  update: async (id: number, data: Employee): Promise<Employee> => {
    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
    return res.json();
  },

  delete: async (id: number): Promise<void> => {
    const res = await fetch(`${BASE_URL}/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error(JSON.stringify(await res.json()));
  },
};
