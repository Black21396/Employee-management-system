package net.fadi.ems.mapper;

import org.mapstruct.Mapper;

import net.fadi.ems.dto.EmployeeDto;
import net.fadi.ems.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDto toDto(Employee employee);
    Employee toEntity(EmployeeDto employeeDto);
}
