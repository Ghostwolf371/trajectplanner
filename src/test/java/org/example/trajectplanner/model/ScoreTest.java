package org.example.trajectplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    void testSetAndGetId() {
        String id = "score-123";
        score.setId(id);
        assertEquals(id, score.getId(), "Score ID should match the set value");
    }

    @Test
    void testSetAndGetStudentId() {
        Integer studentId = 456;
        score.setStudentId(studentId);
        assertEquals(studentId, score.getStudentId(), "Student ID should match the set value");
    }

    @Test
    void testSetAndGetStudentNumber() {
        String studentNumber = "SE/1123/188";
        score.setStudentNumber(studentNumber);
        assertEquals(studentNumber, score.getStudentNumber(), "Student number should match the set value");
    }

    @Test
    void testSetAndGetExamId() {
        Integer examId = 789;
        score.setExamId(examId);
        assertEquals(examId, score.getExamId(), "Exam ID should match the set value");
    }

    @Test
    void testSetAndGetCourseName() {
        String courseName = "Java Programming";
        score.setCourseName(courseName);
        assertEquals(courseName, score.getCourseName(), "Course name should match the set value");
    }

    @Test
    void testSetAndGetCourseCode() {
        String courseCode = "JAV101";
        score.setCourseCode(courseCode);
        assertEquals(courseCode, score.getCourseCode(), "Course code should match the set value");
    }

    @Test
    void testSetAndGetScoreValue() {
        String scoreValue = "8.5";
        score.setScoreValue(scoreValue);
        assertEquals(scoreValue, score.getScoreValue(), "Score value should match the set value");
    }

    @Test
    void testSetAndGetScoreDateTime() {
        String scoreDateTime = "2023-05-15T14:30:00";
        score.setScoreDateTime(scoreDateTime);
        assertEquals(scoreDateTime, score.getScoreDateTime(), "Score date/time should match the set value");
    }

    @Test
    void testGetScore() {
        String scoreValue = "8.5";
        score.setScoreValue(scoreValue);
        assertEquals(8.5, score.getScore(), "getScore should return the correct double value");
    }

    @Test
    void testSetScore() {
        double numericScore = 7.8;
        score.setScore(numericScore);
        assertEquals("7.8", score.getScoreValue(), "setScore should set the string representation");
        assertEquals(numericScore, score.getScore(), "getScore should return the correct numeric value");
    }

    @Test
    void testGetDate() {
        String date = "2023-05-15";
        score.setScoreDateTime(date);
        assertEquals(date, score.getDate(), "getDate should return the score date time");
    }

    @Test
    void testSetDate() {
        String date = "2023-06-20";
        score.setDate(date);
        assertEquals(date, score.getScoreDateTime(), "setDate should set the score date time");
    }

    @Test
    void testNullScoreValue() {
        score.setScoreValue(null);
        assertNull(score.getScore(), "getScore should return null for null score value");
    }

    @Test
    void testCopyConstructor() {
        // Setup original score
        Score originalScore = new Score();
        originalScore.setId("score-123");
        originalScore.setStudentId(456);
        originalScore.setStudentNumber("SE/1123/188");
        originalScore.setExamId(789);
        originalScore.setCourseName("Java Programming");
        originalScore.setCourseCode("JAV101");
        originalScore.setScoreValue("8.5");
        originalScore.setScoreDateTime("2023-05-15T14:30:00");
        
        // Create copy using copy constructor
        Score copiedScore = new Score(originalScore);
        
        // Verify all properties are copied correctly
        assertEquals(originalScore.getId(), copiedScore.getId(), "Copied ID should match original");
        assertEquals(originalScore.getStudentId(), copiedScore.getStudentId(), "Copied student ID should match original");
        assertEquals(originalScore.getStudentNumber(), copiedScore.getStudentNumber(), "Copied student number should match original");
        assertEquals(originalScore.getExamId(), copiedScore.getExamId(), "Copied exam ID should match original");
        assertEquals(originalScore.getCourseName(), copiedScore.getCourseName(), "Copied course name should match original");
        assertEquals(originalScore.getCourseCode(), copiedScore.getCourseCode(), "Copied course code should match original");
        assertEquals(originalScore.getScoreValue(), copiedScore.getScoreValue(), "Copied score value should match original");
        assertEquals(originalScore.getScoreDateTime(), copiedScore.getScoreDateTime(), "Copied score date/time should match original");
        
        // Change original, verify copy is unaffected
        originalScore.setScoreValue("9.0");
        assertEquals("8.5", copiedScore.getScoreValue(), "Changing original should not affect copy");
    }
}