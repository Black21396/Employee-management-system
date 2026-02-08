package net.fadi.ems.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({ "id", "firstName", "lastName", "email" })
public class EmployeeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
