package com.example.employeemanagementsystem.Controller;

import com.example.employeemanagementsystem.Api.ApiResponse;
import com.example.employeemanagementsystem.Model.EmployeeModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee-management-system")
public class EmployeeController {
    ArrayList<EmployeeModel> employeeList = new ArrayList<>();

    // Endpoint to get all employees
    @GetMapping("/get")
    public ResponseEntity<?> getAllEmployees() {
        if (employeeList.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("No employees found"));
        }
        return ResponseEntity.status(200).body(employeeList);
    }

    // Endpoint to add a new employee
    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@Valid @RequestBody EmployeeModel employee, Errors errors) {
        if(errors.hasErrors()) {
            String errorMessage = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(errorMessage));
        }
        if (employeeList.stream().anyMatch(e -> e.getId() == (employee.getId()))) {
            return ResponseEntity.status(400).body(new ApiResponse("Employee with this ID already exists"));
        }
        employeeList.add(employee);
        return ResponseEntity.status(200).body(new ApiResponse("Employee added successfully"));
    }

    // Endpoint to update an employee
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable @Positive int id, @Valid @RequestBody EmployeeModel updatedEmployee, Errors errors) {
        if(errors.hasErrors()) {
            String errorMessage = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(errorMessage));
        }
        for (EmployeeModel employee : employeeList) {
            if (employee.getId() == id) {
                if(!(employee.getId() == updatedEmployee.getId())) {
                    return ResponseEntity.status(400).body(new ApiResponse("Cannot change employee ID"));
                }
                employee.setName(updatedEmployee.getName());
                employee.setEmail(updatedEmployee.getEmail());
                employee.setPhone(updatedEmployee.getPhone());
                employee.setAge(updatedEmployee.getAge());
                employee.setPosition(updatedEmployee.getPosition());
                employee.setOnLeave(updatedEmployee.isOnLeave());
                employee.setHireDate(updatedEmployee.getHireDate());
                employee.setAnnualLeave(updatedEmployee.getAnnualLeave());
                return ResponseEntity.status(200).body(new ApiResponse("Employee updated successfully"));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("Employee not found"));
    }

    // Endpoint to delete an employee
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int id) {
        if(id<0) {
            return ResponseEntity.status(400).body(new ApiResponse("ID must be a positive number"));
        }
        for (EmployeeModel employee : employeeList) {
            if (employee.getId() == id) {
                employeeList.remove(employee);
                return ResponseEntity.status(200).body(new ApiResponse("Employee deleted successfully"));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("Employee not found"));
    }

    @GetMapping("/search/{position}")
    public ResponseEntity<?> searchEmployeePosition(@PathVariable String position){
        ArrayList<EmployeeModel> superVisorList = new ArrayList<>();
        ArrayList<EmployeeModel> coordinatorList = new ArrayList<>();
        switch (position){
            case "supervisor":
                for (EmployeeModel employee : employeeList) {
                    if (employee.getPosition().equalsIgnoreCase("supervisor")) {
                        superVisorList.add(employee);
                    }
                }
                if (superVisorList.isEmpty()) {
                    return ResponseEntity.status(400).body(new ApiResponse("No supervisors found"));
                }
                return ResponseEntity.status(200).body(superVisorList);
            case "coordinator":
                for (EmployeeModel employee : employeeList) {
                    if (employee.getPosition().equalsIgnoreCase("coordinator")) {
                        coordinatorList.add(employee);
                    }
                }
                if (coordinatorList.isEmpty()) {
                    return ResponseEntity.status(400).body(new ApiResponse("No coordinators found"));
                }
                return ResponseEntity.status(200).body(coordinatorList);
            default:
                return ResponseEntity.status(400).body(new ApiResponse("Invalid position specified"));
        }
    }

    @GetMapping("/get-by-age/{minAge}/{maxAge}")
    public ResponseEntity<?> getEmployeeByAge(@PathVariable int minAge,@PathVariable int maxAge) {
        ArrayList<EmployeeModel> ageListEmployee = new ArrayList<>();

        if(minAge<0 || maxAge<0) {
            return ResponseEntity.status(400).body(new ApiResponse("Age must be a positive number"));
        }
        if(minAge > maxAge) {
            return ResponseEntity.status(400).body(new ApiResponse("Minimum age cannot be greater than maximum age"));
        }
        for (EmployeeModel employee : employeeList) {
            if (employee.getAge() >= minAge && employee.getAge() <= maxAge) {
                ageListEmployee.add(employee);
            }
        }
        if (ageListEmployee.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("No employees found in this age range"));
        }
        return ResponseEntity.status(200).body(ageListEmployee);
    }
    @PutMapping("/apply-annual-leave/{id}")
    public ResponseEntity<?> applyAnnualLeave(@PathVariable int id){
        if(id<0) {
            return ResponseEntity.status(400).body(new ApiResponse("ID must be a positive number"));
        }
        for (EmployeeModel employee : employeeList) {
            if (employee.getId() == id) {
                if (employee.isOnLeave()) {
                    return ResponseEntity.status(400).body(new ApiResponse("Employee is already on leave"));
                }
                if (employee.getAnnualLeave() <= 0) {
                    return ResponseEntity.status(400).body(new ApiResponse("No annual leave available"));
                }
                employee.setOnLeave(true);
                employee.setAnnualLeave(employee.getAnnualLeave() - 1);
                return ResponseEntity.status(200).body(new ApiResponse("Annual leave applied successfully"));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("Employee not found"));
    }

    @GetMapping("/get-all-employees-with-no-annual-leave")
    public ResponseEntity<?> getAllEmployeesWithNoAnnualLeave(){
        ArrayList<EmployeeModel> noAnnualLeaveList = new ArrayList<>();
        for (EmployeeModel employee : employeeList) {
            if (employee.getAnnualLeave() <= 0) {
                noAnnualLeaveList.add(employee);
            }
        }
        if (noAnnualLeaveList.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("No employees found with no annual leave"));
        }
        return ResponseEntity.status(200).body(noAnnualLeaveList);
    }
    @PutMapping("/promote/{superVisorId}/{coordinatorId}")
    public ResponseEntity<?> promoteEmployee(@PathVariable int superVisorId,@PathVariable int coordinatorId){
        if(superVisorId<0 || coordinatorId<0) {
            return ResponseEntity.status(400).body(new ApiResponse("ID must be a positive number"));
        }
        EmployeeModel supervisor = null;
        EmployeeModel coordinator = null;

        for (EmployeeModel employee : employeeList) {
            if (employee.getId() == superVisorId && employee.getPosition().equalsIgnoreCase("supervisor")) {
                supervisor = employee;
            }
            if (employee.getId() == coordinatorId && employee.getPosition().equalsIgnoreCase("coordinator")) {
                coordinator = employee;
            }
        }

        if (supervisor == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Supervisor not found"));
        }
        if (coordinator == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Coordinator not found"));
        }
        if(coordinator.getAge() >=30 && !(coordinator.isOnLeave())) {
            coordinator.setPosition("supervisor");
        } else {
            return ResponseEntity.status(400).body(new ApiResponse("Coordinator must be at least 30 years old and not on leave to be promoted"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Promotion successful"));
    }
}
