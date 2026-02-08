package net.fadi.ems.service.interfaces;

import java.util.List;

import net.fadi.ems.dto.EmployeeDto;

public interface EmployeeServiceInterface {
    public EmployeeDto createEmployee(EmployeeDto employeeDto);

    public EmployeeDto getEmployeeById(Long id);

    public List<EmployeeDto> getAllEmployees();
}
