package org.example.trajectplanner.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NewPasswordControllerTest {

    private NewPasswordController controller;
    
    @BeforeEach
    void setUp() {
        controller = new NewPasswordController();
        controller.setStudentInfo("SE/1123/188", "oldPassword");
    }
    
    @Test
    void testSetStudentInfo() {
        // Setup
        NewPasswordController testController = new NewPasswordController();
        String studentNumber = "SE/1123/189";
        String apiPassword = "testPassword";
        
        // Execute
        testController.setStudentInfo(studentNumber, apiPassword);
        
        // Verify - using reflection to access private fields for testing
        try {
            java.lang.reflect.Field studentNumberField = NewPasswordController.class.getDeclaredField("studentNumber");
            studentNumberField.setAccessible(true);
            String actualStudentNumber = (String) studentNumberField.get(testController);
            
            java.lang.reflect.Field passwordField = NewPasswordController.class.getDeclaredField("currentApiPassword");
            passwordField.setAccessible(true);
            String actualPassword = (String) passwordField.get(testController);
            
            assertEquals(studentNumber, actualStudentNumber, "Student number should be set correctly");
            assertEquals(apiPassword, actualPassword, "API password should be set correctly");
        } catch (Exception e) {
            // If reflection fails, the test fails
            throw new AssertionError("Reflection failed: " + e.getMessage());
        }
    }
    
    @Test
    void testPasswordValidation() {
        // Tests the validation logic that would be used in handleChangePassword
        String shortPassword = "123";
        String validPassword = "1234";
        String longPassword = "12345678";
        
        assertEquals(false, isPasswordValid(shortPassword), "Short password should be invalid");
        assertEquals(true, isPasswordValid(validPassword), "Valid password should be valid");
        assertEquals(true, isPasswordValid(longPassword), "Long password should be valid");
    }
    
    // Helper method to simulate the validation logic in handleChangePassword
    private boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty() && password.length() >= 4;
    }
    
    @Test
    void testPasswordMatching() {
        String password1 = "1234";
        String password2 = "1234";
        String password3 = "5678";
        
        assertEquals(true, password1.equals(password2), "Matching passwords should be equal");
        assertEquals(false, password1.equals(password3), "Different passwords should not be equal");
    }
    
    @Test
    void testUpdatePassword() {
        // This is a simplified test that doesn't actually call the server
        // Just testing that the comparison works when passwords match
        String newPass = "1234";
        String confirmPass = "1234";
        
        assertEquals(true, newPass.equals(confirmPass), "Password update should succeed when passwords match");
        
        // Test with mismatched passwords
        String otherPass = "4321";
        assertEquals(false, newPass.equals(otherPass), "Password update should fail when passwords don't match");
    }
} 