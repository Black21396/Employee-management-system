"use client";
import EmployeeForm from "@/app/components/EmployeeForm";
import { employeeService } from "@/app/services/EmployeeService";
import { Employee } from "@/app/types/Employee";
import { useRouter } from "next/navigation";

export default function NewEmployeePage() {
  const router = useRouter();
  const handleCreate = async (data: Employee) => {
    await employeeService.create(data);
    router.push("/employees");
  };
  return (
    <div className="container mt-4">
      <h2>New Employee</h2>
      <EmployeeForm onSubmit={handleCreate} submitLabel="Create" />
    </div>
  );
}
