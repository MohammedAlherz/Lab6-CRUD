package com.example.employeemanagementsystem.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class EmployeeModel {

    @NotNull(message = "ID cannot be null")
    @Min(value = 100, message = "ID must has 3 digits")
    private int id;
    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 5, message = "Name must be at least 5 characters long")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name Must contain only characters (no numbers)")
    private String name;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Phone cannot be empty")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone must start with '05' and consist of exactly 10 digits")
    private String phone;

    @NotNull(message = "Age cannot be null")
    @Positive(message = "Age must be a positive number")
    @Min(value = 26, message = "Age must be at least 26")
    private int age;

    @NotEmpty(message = "Position cannot be empty")
    @Pattern(regexp = "^(supervisor|coordinator)$", message = "Position must be either 'supervisor' or 'coordinator'")
    private String position;

    private boolean onLeave = false;

    @NotNull(message = "Hire date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "Hire date must be today or in the future")
    private LocalDate hireDate;

    @NotNull(message = "annualLeave cannot be null")
    @Positive(message = "annualLeave must be a positive number")
    private int annualLeave;
}
