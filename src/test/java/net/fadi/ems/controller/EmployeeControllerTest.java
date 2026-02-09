package net.fadi.ems.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import net.fadi.ems.dto.EmployeeDto;
import net.fadi.ems.exception.ResourceNotFoundException;
import net.fadi.ems.service.EmployeeService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(EmployeeController.class)
@AutoConfigureRestTestClient
public class EmployeeControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createEmployee_validData_okResponse() {
        EmployeeDto employeeDto = createSampleEmployeeDto();
        EmployeeDto createdEmployeeDto = createSampleEmployeeDto();
        createdEmployeeDto.setId(1L);
        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(createdEmployeeDto);

        restTestClient.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .body(employeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .isEqualTo(objectMapper.writeValueAsString(createdEmployeeDto));
    }

    @Test
    public void getEmployeeById_validId_okResponse() {
        Long id = 1L;
        EmployeeDto employeeDto = createSampleEmployeeDto();
        when(employeeService.getEmployeeById(id)).thenReturn(employeeDto);
        restTestClient.get().uri("/api/employees/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(objectMapper.writeValueAsString(employeeDto));
    }

    @Test
    public void getEmployeeById_invalidId_notFoundResponse() {
        Long id = -1L;
        when(employeeService.getEmployeeById(id)).thenThrow(new ResourceNotFoundException(
                "Employee", "id", id));

        restTestClient.get().uri("/api/employees/" + id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class);

    }

    @Test
    public void getAllEmployees_okResponse() {
        EmployeeDto employeeDto = createSampleEmployeeDto();
        when(employeeService.getAllEmployees()).thenReturn(java.util.List.of(employeeDto));

        restTestClient.get().uri("/api/employees")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(objectMapper.writeValueAsString(java.util.List.of(employeeDto)));
    }

    @Test
    public void updateEmployee_validData_okResponse() {
        EmployeeDto employeeDto = createSampleEmployeeDto();
        when(employeeService.updateEmployee(any(Long.class), any(EmployeeDto.class))).thenReturn(employeeDto);

        restTestClient.put().uri("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(employeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(objectMapper.writeValueAsString(employeeDto));
    }

    @Test
    public void deleteEmployee_validId_okResponse() {
        restTestClient.delete().uri("/api/employees/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Employee deleted successfully");
    }

    private EmployeeDto createSampleEmployeeDto() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("JohnDto");
        employeeDto.setLastName("DoeDto");
        employeeDto.setEmail("johndto@gmail.com");

        return employeeDto;
    }
}
