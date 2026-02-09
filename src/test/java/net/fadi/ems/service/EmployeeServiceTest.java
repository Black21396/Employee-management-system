package net.fadi.ems.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.fadi.ems.dto.EmployeeDto;
import net.fadi.ems.entity.Employee;
import net.fadi.ems.mapper.EmployeeMapper;
import net.fadi.ems.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createEmployee_validData_returnEmployeeDto() {
        Employee employee = createSampleEmployee();
        EmployeeDto employeeDto = createSampleEmployeeDto();
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDto);
        when(employeeMapper.toEntity(any(EmployeeDto.class))).thenReturn(employee);

        EmployeeDto result = employeeService.createEmployee(employeeDto);
        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
    }

    @Test
    void getEmployeeById_validId_returnEmployeeDto() {
        Employee employee = createSampleEmployee();
        EmployeeDto employeeDto = createSampleEmployeeDto();
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDto);

        EmployeeDto result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
    }

    @Test
    void getEmployeeById_invalidId_throwsResourceNotFoundException() {
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        try {
            employeeService.getEmployeeById(1L);
        } catch (Exception ex) {
            assertEquals("Employee not found with id : '1'", ex.getMessage());
        }
    }

    @Test
    void updateEmployee_validData_returnUpdatedEmployeeDto() {
        Employee employee = createSampleEmployee();
        EmployeeDto employeeDto = EmployeeDto.builder()
                .id(1L)
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email("updatedemail@gmail.com")
                .build();
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDto);

        EmployeeDto result = employeeService.updateEmployee(1L, employeeDto);

        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
        assertEquals(result.getFirstName(), employeeDto.getFirstName());
        assertEquals(result.getLastName(), employeeDto.getLastName());
        assertEquals(result.getEmail(), employeeDto.getEmail());
    }

    private Employee createSampleEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        return employee;
    }

    private EmployeeDto createSampleEmployeeDto() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1L);
        employeeDto.setFirstName("JohnDto");
        employeeDto.setLastName("DoeDto");
        employeeDto.setEmail("johndto@gmail.com");

        return employeeDto;
    }
}
