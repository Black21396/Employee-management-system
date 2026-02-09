package net.fadi.ems.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "id", "firstName", "lastName", "email" })
public class EmployeeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
