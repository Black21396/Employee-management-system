package net.fadi.ems.mapper;

import org.mapstruct.Mapper;

import net.fadi.ems.dto.EmployeeDto;
import net.fadi.ems.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    public EmployeeDto toDto(Employee employee);
    public Employee toEntity(EmployeeDto employeeDto);
}
