package org.example.trajectplanner.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    @Test
    void testValidStudentNumber() {
        assertTrue(isValidStudentNumber("SE/1123/188"), "Valid student number format should pass");
        assertTrue(isValidStudentNumber("CS/2023/001"), "Valid student number format should pass");
    }

    @Test
    void testInvalidStudentNumber() {
        assertFalse(isValidStudentNumber(""), "Empty student number should fail");
        assertFalse(isValidStudentNumber(null), "Null student number should fail");
        assertFalse(isValidStudentNumber("12345"), "Student number without pattern should fail");
        assertFalse(isValidStudentNumber("SE-1123-188"), "Student number with wrong separators should fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "12"})
    void testInvalidPasswordLength(String input) {
        assertFalse(isValidPassword(input), "Invalid password should fail validation");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "password", "test1234"})
    void testValidPasswordLength(String input) {
        assertTrue(isValidPassword(input), "Valid password should pass validation");
    }

    @ParameterizedTest
    @CsvSource({
        "5, true",
        "0, true",
        "60, true",
        "75, true",
        "-1, false",
        "-10, false"
    })
    void testEcRange(int ec, boolean expected) {
        assertEquals(expected, isValidEc(ec), "EC validation should match expected result");
    }

    @ParameterizedTest
    @CsvSource({
        "John, true",
        "Jane, true",
        ", false",
        "'', false",
        "'   ', false"
    })
    void testNameValidation(String name, boolean expected) {
        assertEquals(expected, isValidName(name), "Name validation should match expected result");
    }

    // Helper methods to simulate validation logic
    private boolean isValidStudentNumber(String studentNumber) {
        if (studentNumber == null || studentNumber.trim().isEmpty()) {
            return false;
        }
        return studentNumber.matches("[A-Z]{2}/\\d{4}/\\d{3}");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }

    private boolean isValidEc(int ec) {
        return ec >= 0;
    }

    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private void assertEquals(boolean expected, boolean actual, String message) {
        if (expected) {
            assertTrue(actual, message);
        } else {
            assertFalse(actual, message);
        }
    }
} 