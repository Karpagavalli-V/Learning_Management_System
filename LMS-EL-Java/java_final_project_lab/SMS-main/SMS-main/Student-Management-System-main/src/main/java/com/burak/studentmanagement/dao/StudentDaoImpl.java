package com.burak.studentmanagement.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.burak.studentmanagement.entity.Student;

@Repository
public class StudentDaoImpl implements StudentDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Student findByStudentName(String studentName) {
        System.out.println("StudentDaoImpl: Finding student by name: " + studentName);
        
        try {
            // Original query - may be case-sensitive
            TypedQuery<Student> query = entityManager.createQuery(
                    "FROM Student WHERE userName = :userName", Student.class);
            
            query.setParameter("userName", studentName);
            
            Student student = query.getSingleResult();
            System.out.println("StudentDaoImpl: Found student: " + student.getUserName());
            return student;
        } catch (NoResultException nre) {
            System.out.println("StudentDaoImpl: No student found with username: " + studentName);
            
            // Try case-insensitive search as fallback
            try {
                TypedQuery<Student> query = entityManager.createQuery(
                        "FROM Student WHERE LOWER(userName) = LOWER(:userName)", Student.class);
                
                query.setParameter("userName", studentName);
                
                Student student = query.getSingleResult();
                System.out.println("StudentDaoImpl: Found student with case-insensitive search: " + student.getUserName());
                return student;
            } catch (NoResultException nre2) {
                System.out.println("StudentDaoImpl: No student found with case-insensitive search either");
                return null;
            }
        } catch (Exception e) {
            System.out.println("StudentDaoImpl: Error finding student: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Student findByStudentId(int id) {
        return entityManager.find(Student.class, id);
    }

    @Override
    public void save(Student student) {
        Student dbStudent = entityManager.merge(student);
        student.setId(dbStudent.getId());
    }

    @Override
    public List<Student> findAllStudents() {
        TypedQuery<Student> query = entityManager.createQuery("FROM Student", Student.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(int id) {
        Student student = entityManager.find(Student.class, id);
        entityManager.remove(student);
    }
    
    // Add a method to find by email (if needed)
    public Student findByEmail(String email) {
        System.out.println("StudentDaoImpl: Finding student by email: " + email);
        
        try {
            TypedQuery<Student> query = entityManager.createQuery(
                    "FROM Student WHERE email = :email", Student.class);
            
            query.setParameter("email", email);
            
            Student student = query.getSingleResult();
            System.out.println("StudentDaoImpl: Found student by email: " + student.getEmail());
            return student;
        } catch (NoResultException nre) {
            System.out.println("StudentDaoImpl: No student found with email: " + email);
            return null;
        } catch (Exception e) {
            System.out.println("StudentDaoImpl: Error finding student by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Student findByUsername(String username) {
        System.out.println("StudentDaoImpl: Finding student by username for authentication: " + username);
        
        try {
            // First try to find by userName field
            TypedQuery<Student> query = entityManager.createQuery(
                    "FROM Student WHERE userName = :username", Student.class);
            
            query.setParameter("username", username);
            
            Student student = query.getSingleResult();
            System.out.println("StudentDaoImpl: Found student by username: " + student.getUserName());
            return student;
        } catch (NoResultException nre) {
            // If not found by userName, try by email as fallback
            System.out.println("StudentDaoImpl: No student found with userName, trying email...");
            
            try {
                TypedQuery<Student> query = entityManager.createQuery(
                        "FROM Student WHERE email = :email", Student.class);
                
                query.setParameter("email", username);
                
                Student student = query.getSingleResult();
                System.out.println("StudentDaoImpl: Found student by email as username: " + student.getEmail());
                return student;
            } catch (NoResultException nre2) {
                System.out.println("StudentDaoImpl: No student found for authentication with username: " + username);
                return null;
            }
        } catch (Exception e) {
            System.out.println("StudentDaoImpl: Error finding student for authentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}