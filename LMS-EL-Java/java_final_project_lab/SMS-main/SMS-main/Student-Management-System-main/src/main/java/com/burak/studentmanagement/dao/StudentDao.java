package com.burak.studentmanagement.dao;

import java.util.List;

import com.burak.studentmanagement.entity.Student;

public interface StudentDao {
	
	public Student findByStudentName(String theStudentName);
	
	public void save(Student student);
	
	public Student findByStudentId(int id);
	
	public List<Student> findAllStudents();
	
	public void deleteById(int id);
	
	/**
	 * Finds a student by their username for Spring Security authentication
	 * @param username The username to search for
	 * @return The student with the matching username or null if not found
	 */
	public Student findByUsername(String username);
}