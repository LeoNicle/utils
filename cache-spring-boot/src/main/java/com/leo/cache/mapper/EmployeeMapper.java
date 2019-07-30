package com.leo.cache.mapper;

import com.leo.cache.entity.Employee;
import org.apache.ibatis.annotations.*;

public interface EmployeeMapper {
@Select("select * from employee where id=#{id}")
     Employee getEmpById(Integer id);

@Update("update employee set lastName=#{lastName},emial=#{email},gender=#{gender},d_id=#{dId} where id=#{id}")
void update(Employee employee);

@Delete("delete from employee where id=#{id}")
void delete(Employee employee);
//    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into employee(lastName,email,gender,d_id) values(#{lastName},#{email},#{gender},#{dId})")
     void insertEmp(Employee employee);
}
