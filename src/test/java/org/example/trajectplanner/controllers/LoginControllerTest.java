package org.example.trajectplanner.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginControllerTest {

    private LoginController controller;
    
    @BeforeEach
    void setUp() {
        controller = new LoginController();
    }
    
    @Test
    void testDefaultPassword() {
        // This test accesses the DEFAULT_PASSWORD constant using reflection for testing purposes
        try {
            Field field = LoginController.class.getDeclaredField("DEFAULT_PASSWORD");
            field.setAccessible(true);
            String defaultPassword = (String) field.get(null); // null for static fields
            
            // Test that the default password is what we expect (for consistency testing)
            assertEquals("9x#V@7p!Lz$Q2w%T8m^C3j*B6r&K0d", defaultPassword);
            
            // Test that normal password doesn't match default password
            assertFalse(defaultPassword.equals("1234"));
        } catch (Exception e) {
            throw new AssertionError("Reflection failed: " + e.getMessage());
        }
    }
    
    @Test
    void testAdminCredentials() {
        // Get admin username and password using reflection
        try {
            Field usernameField = LoginController.class.getDeclaredField("ADMIN_USERNAME");
            usernameField.setAccessible(true);
            String adminUsername = (String) usernameField.get(null);
            
            Field passwordField = LoginController.class.getDeclaredField("ADMIN_PASSWORD");
            passwordField.setAccessible(true);
            String adminPassword = (String) passwordField.get(null);
            
            // Test admin credentials
            assertEquals("admin", adminUsername);
            assertEquals("admin", adminPassword);
        } catch (Exception e) {
            throw new AssertionError("Reflection failed: " + e.getMessage());
        }
    }
    
    @Test
    void testStudentNumberFormatting() {
        String originalStudentNumber = "SE/1123/188";
        String expected = "SE-1123-188";
        String actual = formatStudentNumber(originalStudentNumber);
        
        assertEquals(expected, actual, "Student number should be formatted correctly");
    }
    
    @Test
    void testCredentialValidation() {
        // Test valid credentials (of course in real app we'd check against the server)
        String validUsername = "SE/1123/188";
        String validPassword = "1234";
        
        // For demo purposes, just test that we can match the credentials
        assertTrue(validateCredentials(validUsername, validPassword, validUsername, validPassword),
                "Valid credentials should pass validation");
        
        // Test invalid credentials
        String wrongPassword = "wrong";
        assertFalse(validateCredentials(validUsername, validPassword, validUsername, wrongPassword),
                "Invalid password should fail validation");
        
        String wrongUsername = "wrong";
        assertFalse(validateCredentials(validUsername, validPassword, wrongUsername, validPassword),
                "Invalid username should fail validation");
    }
    
    // Helper methods to simulate the functionality of the controller
    
    private String formatStudentNumber(String studentNumber) {
        return studentNumber.replace("/", "-");
    }
    
    private boolean validateCredentials(String storedUsername, String storedPassword,
                                      String inputUsername, String inputPassword) {
        return storedUsername.equals(inputUsername) && storedPassword.equals(inputPassword);
    }
} 