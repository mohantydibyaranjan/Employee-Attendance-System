package com.attendance.employeeservice.controller;

import com.attendance.common.dto.ApiResponse;
import com.attendance.employeeservice.dto.EmployeeDto;
import com.attendance.employeeservice.entity.Employee;
import com.attendance.employeeservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Create a new employee", description = "Creates a new employee with the given details.")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDto>> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        Employee employee = convertToEntity(employeeDto);
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(new ApiResponse<>(true, "Employee created successfully", convertToDto(createdEmployee)));
    }

    @Operation(summary = "Get an employee by ID", description = "Retrieves an employee by their ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Employee retrieved successfully", convertToDto(employee)));
    }

    @Operation(summary = "Update an employee", description = "Updates an existing employee with the given details.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDto employeeDto) {
        Employee employee = convertToEntity(employeeDto);
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(new ApiResponse<>(true, "Employee updated successfully", convertToDto(updatedEmployee)));
    }

    @Operation(summary = "Delete an employee", description = "Deletes an employee by their ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Employee deleted successfully", null));
    }

    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(new ApiResponse<>(true, "Employees retrieved successfully", employees));
    }

    private EmployeeDto convertToDto(Employee employee) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(employee.getId());
        employeeDto.setName(employee.getName());
        employeeDto.setDepartment(employee.getDepartment());
        employeeDto.setDesignation(employee.getDesignation());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setStatus(employee.getStatus());
        employeeDto.setJoinDate(employee.getJoinDate());
        return employeeDto;
    }

    private Employee convertToEntity(EmployeeDto employeeDto) {
        Employee employee = new Employee();
        employee.setId(employeeDto.getId());
        employee.setName(employeeDto.getName());
        employee.setDepartment(employeeDto.getDepartment());
        employee.setDesignation(employeeDto.getDesignation());
        employee.setEmail(employeeDto.getEmail());
        employee.setStatus(employeeDto.getStatus());
        employee.setJoinDate(employeeDto.getJoinDate());
        return employee;
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<Employee>>> filterEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String email,
            Pageable pageable) {
        Specification<Employee> spec = com.attendance.employeeservice.util.SpecificationBuilder.build(name, department, designation, email);
        Page<Employee> employees = employeeService.searchEmployees(spec, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Employees retrieved successfully", employees));
    }
}
