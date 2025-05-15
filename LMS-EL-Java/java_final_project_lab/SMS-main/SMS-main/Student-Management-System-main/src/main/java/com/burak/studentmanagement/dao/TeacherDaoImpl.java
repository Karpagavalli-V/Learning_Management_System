package com.burak.studentmanagement.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.burak.studentmanagement.entity.Teacher;

@Repository
public class TeacherDaoImpl implements TeacherDao {
    
    @Autowired
    private EntityManager entityManager;
    
    @Override
    public Teacher findByTeacherName(String teacherName) {
        System.out.println("TeacherDaoImpl: Finding teacher by name: " + teacherName);
        
        try {
            // Original query - may be case-sensitive
            TypedQuery<Teacher> query = entityManager.createQuery(
                    "FROM Teacher WHERE userName = :userName", Teacher.class);
            
            query.setParameter("userName", teacherName);
            
            Teacher teacher = query.getSingleResult();
            System.out.println("TeacherDaoImpl: Found teacher: " + teacher.getUserName());
            return teacher;
        } catch (NoResultException nre) {
            System.out.println("TeacherDaoImpl: No teacher found with username: " + teacherName);
            
            // Try case-insensitive search as fallback
            try {
                TypedQuery<Teacher> query = entityManager.createQuery(
                        "FROM Teacher WHERE LOWER(userName) = LOWER(:userName)", Teacher.class);
                
                query.setParameter("userName", teacherName);
                
                Teacher teacher = query.getSingleResult();
                System.out.println("TeacherDaoImpl: Found teacher with case-insensitive search: " + teacher.getUserName());
                return teacher;
            } catch (NoResultException nre2) {
                System.out.println("TeacherDaoImpl: No teacher found with case-insensitive search either");
                return null;
            }
        } catch (Exception e) {
            System.out.println("TeacherDaoImpl: Error finding teacher: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Teacher findByTeacherId(int id) {
        System.out.println("TeacherDaoImpl: Finding teacher by ID: " + id);
        return entityManager.find(Teacher.class, id);
    }

    @Override
    public void save(Teacher teacher) {
        System.out.println("TeacherDaoImpl: Saving teacher: " + teacher.getUserName());
        Teacher dbTeacher = entityManager.merge(teacher);
        // Update the ID in case this was an insert
        teacher.setId(dbTeacher.getId());
    }

    @Override
    public List<Teacher> findAllTeachers() {
        System.out.println("TeacherDaoImpl: Finding all teachers");
        TypedQuery<Teacher> query = entityManager.createQuery("FROM Teacher", Teacher.class);
        return query.getResultList();
    }

    @Override
    public void deleteTeacherById(int id) {
        System.out.println("TeacherDaoImpl: Deleting teacher with ID: " + id);
        Teacher teacher = entityManager.find(Teacher.class, id);
        if (teacher != null) {
            entityManager.remove(teacher);
        }
    }
    
    // Add a method to find by email (if needed)
    public Teacher findByEmail(String email) {
        System.out.println("TeacherDaoImpl: Finding teacher by email: " + email);
        
        try {
            TypedQuery<Teacher> query = entityManager.createQuery(
                    "FROM Teacher WHERE email = :email", Teacher.class);
            
            query.setParameter("email", email);
            
            Teacher teacher = query.getSingleResult();
            System.out.println("TeacherDaoImpl: Found teacher by email: " + teacher.getEmail());
            return teacher;
        } catch (NoResultException nre) {
            System.out.println("TeacherDaoImpl: No teacher found with email: " + email);
            return null;
        } catch (Exception e) {
            System.out.println("TeacherDaoImpl: Error finding teacher by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Teacher findByUsername(String username) {
        System.out.println("TeacherDaoImpl: Finding teacher by username for authentication: " + username);
        
        try {
            // First try to find by userName field
            TypedQuery<Teacher> query = entityManager.createQuery(
                    "FROM Teacher WHERE userName = :username", Teacher.class);
            
            query.setParameter("username", username);
            
            Teacher teacher = query.getSingleResult();
            System.out.println("TeacherDaoImpl: Found teacher by username: " + teacher.getUserName());
            return teacher;
        } catch (NoResultException nre) {
            // If not found by userName, try by email as fallback
            System.out.println("TeacherDaoImpl: No teacher found with userName, trying email...");
            
            try {
                TypedQuery<Teacher> query = entityManager.createQuery(
                        "FROM Teacher WHERE email = :email", Teacher.class);
                
                query.setParameter("email", username);
                
                Teacher teacher = query.getSingleResult();
                System.out.println("TeacherDaoImpl: Found teacher by email as username: " + teacher.getEmail());
                return teacher;
            } catch (NoResultException nre2) {
                System.out.println("TeacherDaoImpl: No teacher found for authentication with username: " + username);
                return null;
            }
        } catch (Exception e) {
            System.out.println("TeacherDaoImpl: Error finding teacher for authentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}