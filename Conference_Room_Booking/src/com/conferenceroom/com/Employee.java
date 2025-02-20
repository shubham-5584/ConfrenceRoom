package com.conferenceroom.com;

public class Employee {
     int Employee_Id;
     String User_Name;
	public Employee(int employee_Id, String user_Name) {
		super();
		Employee_Id = employee_Id;
		User_Name = user_Name;
	}
	public int getEmployee_Id() {
		return Employee_Id;
	}
	public void setEmployee_Id(int employee_Id) {
		Employee_Id = employee_Id;
	}
	public String getUser_Name() {
		return User_Name;
	}
	public void setUser_Name(String user_Name) {
		User_Name = user_Name;
	}
     
     
}
