package net.fadi.ems.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.fadi.ems.dto.EmployeeDto;
import net.fadi.ems.entity.Employee;
import net.fadi.ems.exception.DatabaseException;
import net.fadi.ems.exception.ResourceNotFoundException;
import net.fadi.ems.mapper.EmployeeMapper;
import net.fadi.ems.repository.EmployeeRepository;
import net.fadi.ems.service.interfaces.EmployeeServiceInterface;

@Service
public class EmployeeService implements EmployeeServiceInterface {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        Employee entity = employeeMapper.toEntity(employeeDto);
        try {
            entity = employeeRepository.save(entity);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return employeeMapper.toDto(entity);
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        return employeeMapper.toDto(entity);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> entities = employeeRepository.findAll();

        return entities.stream().map(employeeMapper::toDto).toList();
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        entity.setFirstName(employeeDto.getFirstName());
        entity.setLastName(employeeDto.getLastName());
        entity.setEmail(employeeDto.getEmail());

        try {
            entity = employeeRepository.save(entity);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return employeeMapper.toDto(entity);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        try {
            employeeRepository.delete(entity);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
