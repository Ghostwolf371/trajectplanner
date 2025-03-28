package org.example.trajectplanner.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {
    @Test
    public void testStudentNumberFormat() {
        String studentNumber = "SE/2324/01";
        assertTrue(studentNumber.matches("SE/\\d{4}/\\d{2}"));
    }

    @Test
    public void testScoreValueFormat() {
        String scoreValue = "8.5";
        assertTrue(scoreValue.matches("\\d+(\\.\\d+)?"));
        assertTrue(Double.parseDouble(scoreValue) >= 0.0);
        assertTrue(Double.parseDouble(scoreValue) <= 10.0);
    }

    @Test
    public void testDateFormat() {
        String date = "2024-03-20";
        assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}"));
    }
}