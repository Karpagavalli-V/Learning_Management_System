package com.burak.studentmanagement.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burak.studentmanagement.dao.RoleDao;
import com.burak.studentmanagement.dao.StudentDao;
import com.burak.studentmanagement.entity.Role;
import com.burak.studentmanagement.entity.Student;
import com.burak.studentmanagement.user.UserDto;

@Service
public class StudentServiceImpl implements StudentService {
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired 
	private RoleDao roleDao;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;  // Inject password encoder

	@Override
	@Transactional
	public Student findByStudentName(String studentName) {
		// Add debugging
		System.out.println("StudentServiceImpl: Finding student by name: " + studentName);
		Student student = studentDao.findByStudentName(studentName);
		System.out.println("StudentServiceImpl: Student found: " + (student != null ? "Yes" : "No"));
		return student;
	}
	
	@Override
	@Transactional
	public Student findByStudentId(int id) {
		return studentDao.findByStudentId(id);
	}

	@Override
	@Transactional
	public void save(UserDto userDto) {
		System.out.println("StudentServiceImpl: Saving new student with username: " + userDto.getUserName());
		
		Student student = new Student();
		student.setUserName(userDto.getUserName());
		// Use the injected passwordEncoder instead of creating a new one each time
		student.setPassword(passwordEncoder.encode(userDto.getPassword()));
		student.setFirstName(userDto.getFirstName());
		student.setLastName(userDto.getLastName());
		student.setEmail(userDto.getEmail());		
		student.setRole(userDto.getRole());	
		
		studentDao.save(student);
		
		System.out.println("StudentServiceImpl: Student saved successfully");
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("StudentServiceImpl.loadUserByUsername: Called with username: " + username);
		
		try {
			Student student = studentDao.findByStudentName(username);
			
			if (student == null) {
				System.out.println("StudentServiceImpl.loadUserByUsername: No student found with username: " + username);
				throw new UsernameNotFoundException("Invalid username or password.");
			}
			
			System.out.println("StudentServiceImpl.loadUserByUsername: Student found: " + student.getUserName());
			System.out.println("StudentServiceImpl.loadUserByUsername: Role: " + (student.getRole() != null ? student.getRole().getName() : "null"));
			
			Collection<Role> roles = new ArrayList<>();
			roles.add(student.getRole());
			
			return new org.springframework.security.core.userdetails.User(
					student.getUserName(), 
					student.getPassword(),
					mapRolesToAuthorities(roles));
		} catch (Exception e) {
			System.out.println("StudentServiceImpl.loadUserByUsername: Exception: " + e.getMessage());
			e.printStackTrace();
			throw new UsernameNotFoundException("Error during authentication", e);
		}
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		Collection<SimpleGrantedAuthority> authorities = roles.stream()
				.map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList());
		
		// Debug
		for (SimpleGrantedAuthority authority : authorities) {
			System.out.println("StudentServiceImpl.mapRolesToAuthorities: Authority: " + authority.getAuthority());
		}
		
		return authorities;
	}

	@Override
	@Transactional
	public List<Student> findAllStudents() {
		return studentDao.findAllStudents();
	}

	@Override
	@Transactional
	public void save(Student student) {
		// If password is changed, make sure to encode it
		if (student.getPassword() != null && !student.getPassword().startsWith("$2a$")) {
			student.setPassword(passwordEncoder.encode(student.getPassword()));
		}
		
		studentDao.save(student);
	}

	@Override
	@Transactional
	public void deleteById(int id) {
		studentDao.deleteById(id);
	}
}