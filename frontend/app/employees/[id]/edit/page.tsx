"use client";
import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { Employee } from "@/app/types/Employee";
import { employeeService } from "@/app/services/EmployeeService";
import EmployeeForm from "@/app/components/EmployeeForm";

export default function EditEmployeePage() {
  const { id } = useParams();
  const router = useRouter();
  const [employee, setEmployee] = useState<Employee | null>(null);

  useEffect(() => {
    employeeService.getById(Number(id)).then(setEmployee);
  }, [id]);

  const handleUpdate = async (data: Employee) => {
    await employeeService.update(Number(id), data);
    router.push("/employees");
  };

  if (!employee) return <p>Loading...</p>;
  return (
    <div className="container mt-4">
      <h2>Edit Employee</h2>
      <EmployeeForm
        initial={employee}
        onSubmit={handleUpdate}
        submitLabel="Update"
      />
    </div>
  );
}
