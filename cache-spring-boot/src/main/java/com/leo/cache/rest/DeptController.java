package com.leo.cache.rest;

import com.leo.cache.entity.Department;
import com.leo.cache.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeptController {
    @Autowired
    DepartmentService departmentService;

    @GetMapping("/dept/{id}")
    public Department getDept(@PathVariable("id")Integer id){
        return departmentService.getDepById(id);
    }

    @GetMapping("/dept")
    public Department insertDept(Department department){
        departmentService.insertDep(department);
        return department;
    }

}
