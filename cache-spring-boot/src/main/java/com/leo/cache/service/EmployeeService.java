package com.leo.cache.service;

import com.leo.cache.entity.Department;
import com.leo.cache.entity.Employee;
import com.leo.cache.mapper.DepartmentMapper;
import com.leo.cache.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "emp",cacheManager = "myCacheManager")
public class EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    CacheManager myCacheManager;

    @Cacheable(key = "#root.args[0]" )
    public Employee getEmpById(Integer id) {
        System.out.println("查询emp"+id);
        Department dept = departmentMapper.getDepById(1);
        Department dept2 = departmentMapper.getDepById(2);
        //以编码方式操作缓存
        Cache cache = myCacheManager.getCache("dept");
        cache.put(1,dept);
        cache.put(2,dept2);
        return employeeMapper.getEmpById(id);
    }

    public void insertEmp(Employee employee) {
        employeeMapper.insertEmp(employee);
    }
}
