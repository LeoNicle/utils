package com.leo.cache.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
	
	private Integer id;
	private String lastName;
	private String email;
	private Integer gender; //性别 1男  0女
	private Integer dId;
	
	
	public Employee() {
		super();
	}

	
	public Employee(Integer id, String lastName, String email, Integer gender, Integer dId) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.email = email;
		this.gender = gender;
		this.dId = dId;
	}
	
	@Override
	public String toString() {
		return "Employee [id=" + id + ", lastName=" + lastName + ", email=" + email + ", gender=" + gender + ", dId="
				+ dId + "]";
	}
	
	

}
