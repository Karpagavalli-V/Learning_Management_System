package com.burak.studentmanagement.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burak.studentmanagement.dao.StudentDao;
import com.burak.studentmanagement.dao.TeacherDao;
import com.burak.studentmanagement.entity.Student;
import com.burak.studentmanagement.entity.Teacher;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private TeacherDao teacherDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find the user in the student table
        Student student = studentDao.findByUsername(username);
        if (student != null) {
            System.out.println("Authentication: Found student with username: " + username);
            return new User(
                student.getUserName(), 
                student.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_STUDENT"))
            );
        }

        // Try to find the user in the teacher table
        Teacher teacher = teacherDao.findByUsername(username);
        if (teacher != null) {
            System.out.println("Authentication: Found teacher with username: " + username);
            return new User(
                teacher.getUserName(),
                teacher.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_TEACHER"))
            );
        }

        System.out.println("Authentication failed: User not found with username: " + username);
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}