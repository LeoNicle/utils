package com.leo.cache.rest;

import com.leo.cache.entity.Employee;
import com.leo.cache.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @GetMapping("/emp/{id}")
    public Employee getDept(@PathVariable("id")Integer id){
        return employeeService.getEmpById(id);
    }

    @GetMapping("/emp")
    public Employee insertDept(Employee employee){
        employeeService.insertEmp(employee);
        return employee;
    }

}
