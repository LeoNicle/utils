package com.leo.cache.mapper;

import com.leo.cache.entity.Department;
import org.apache.ibatis.annotations.*;

public interface DepartmentMapper {
    @Select("select * from department where id=#{id}")
    Department getDepById(Integer id);

    @Delete("delete from department where id=#{id}")
    int deleteDepById(Integer id);

    @Insert("insert into department(department_name) values(#{departmentName})")
    int insertDep(Department department);

    @Update("update department set department_name=#{departmentName} where id=#{id}")
    int updateDep(Department department);
}
