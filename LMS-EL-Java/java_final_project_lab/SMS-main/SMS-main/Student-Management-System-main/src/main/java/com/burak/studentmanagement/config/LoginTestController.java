package com.burak.studentmanagement.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.burak.studentmanagement.service.StudentService;
import com.burak.studentmanagement.service.TeacherService;

/**
 * Test controller for debugging login issues.
 * Access at /test-login?username=xxx&password=yyy
 */
@RestController
@RequestMapping("/test-login")
public class LoginTestController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginTestController(StudentService studentService, 
                               TeacherService teacherService,
                               BCryptPasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String testLogin(@RequestParam String username, @RequestParam String password) {
        StringBuilder result = new StringBuilder();
        result.append("Testing authentication for username: ").append(username).append("\n\n");

        // Try student authentication
        try {
            result.append("Trying student authentication:\n");
            UserDetails userDetails = studentService.loadUserByUsername(username);
            result.append("- Student found: ").append(userDetails.getUsername()).append("\n");
            
            boolean passwordMatches = passwordEncoder.matches(password, userDetails.getPassword());
            result.append("- Password matches: ").append(passwordMatches).append("\n");
            
            result.append("- Authorities: ");
            userDetails.getAuthorities().forEach(auth -> 
                result.append(auth.getAuthority()).append(" "));
            result.append("\n\n");
            
            if (passwordMatches) {
                result.append("Student authentication successful!");
                return result.toString();
            } else {
                result.append("Student found but password doesn't match.\n\n");
            }
        } catch (UsernameNotFoundException e) {
            result.append("- No student found with username: ").append(username).append("\n\n");
        } catch (Exception e) {
            result.append("- Error during student authentication: ").append(e.getMessage()).append("\n\n");
        }

        // Try teacher authentication
        try {
            result.append("Trying teacher authentication:\n");
            UserDetails userDetails = teacherService.loadUserByUsername(username);
            result.append("- Teacher found: ").append(userDetails.getUsername()).append("\n");
            
            boolean passwordMatches = passwordEncoder.matches(password, userDetails.getPassword());
            result.append("- Password matches: ").append(passwordMatches).append("\n");
            
            result.append("- Authorities: ");
            userDetails.getAuthorities().forEach(auth -> 
                result.append(auth.getAuthority()).append(" "));
            result.append("\n\n");
            
            if (passwordMatches) {
                result.append("Teacher authentication successful!");
                return result.toString();
            } else {
                result.append("Teacher found but password doesn't match.\n\n");
            }
        } catch (UsernameNotFoundException e) {
            result.append("- No teacher found with username: ").append(username).append("\n\n");
        } catch (Exception e) {
            result.append("- Error during teacher authentication: ").append(e.getMessage()).append("\n\n");
        }

        // If we get here, authentication failed
        result.append("Authentication failed. No matching user found or password incorrect.");
        return result.toString();
    }
}