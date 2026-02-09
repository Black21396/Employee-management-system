package net.fadi.ems.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import net.fadi.ems.entity.Employee;

@DataJpaTest
public class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        employeeRepository.saveAll(
                List.of(
                        Employee.builder()
                                .firstName("Fadi")
                                .lastName("Salameh")
                                .email("fadi@gmail.com")
                                .build(),
                        Employee.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .email("john.doe@gmail.com")
                                .build(),
                        Employee.builder()
                                .firstName("Jane")
                                .lastName("Smith")
                                .email("jane.smith@gmail.com")
                                .build()));

        employeeRepository.flush();
    }

    @Test
    public void saveEmployee_validData_returnEmployeeWithId() {
        Employee employee = Employee.builder()
                .firstName("Fadi")
                .lastName("Salameh")
                .email("fadi@gmaildd.com")
                .build();

        employeeRepository.save(employee);

        assertNotNull(employee.getId());
    }

    @Test
    public void findAllEmployees_whenCalled_returnListOfEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        assertNotNull(employees);
        assert (employees.size() > 0);
    }

    @Test
    public void findEmployeeById_EmployeeExists_returnEmployee() {
        Optional<Employee> employee = employeeRepository.findAll().stream().findFirst();
        Long employeeId = employee.get().getId();

        Optional<Employee> foundEmployee = employeeRepository.findById(employeeId);

        assertTrue(foundEmployee.isPresent());
    }

    @Test
    public void findEmployeeById_EmployeeDoesntExist_emptyResult() {
        Optional<Employee> employee = employeeRepository.findById(-1L);

        assertTrue(employee.isEmpty());
    }

    @Test
    public void deleteEmployee_EmployeeExists_employeeDeleted() {
        Optional<Employee> employee = employeeRepository.findAll().stream().findFirst();
        assertTrue(employee.isPresent());
        Long employeeId = employee.get().getId();

        employeeRepository.deleteById(employeeId);

        assertTrue(employeeRepository.findById(employeeId).isEmpty());
    }
}