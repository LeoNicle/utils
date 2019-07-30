package com.leo.cache.service;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.leo.cache.entity.Department;
import com.leo.cache.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames="dept",cacheManager = "myCacheManager")//抽取缓存对的公共配置
public class DepartmentService {
    @Autowired
    DepartmentMapper departmentMapper;

    @Cacheable(key="#root.args[0]")
    public Department getDepById(Integer id) {
        System.out.println("查询dept"+id);
//        System.out.println(DruidStatManagerFacade.getInstance().getDataSourceStatDataList());
        return departmentMapper.getDepById(id);
    }

    public void insertDep(Department department) {
        departmentMapper.insertDep(department);
    }
}
