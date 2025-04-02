package org.example.trajectplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StudentTest {

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
    }

    @Test
    void testSetAndGetId() {
        Long id = 123L;
        student.setId(id);
        assertEquals(id, student.getId(), "Student ID should match the set value");
    }

    @Test
    void testSetAndGetFirstName() {
        String firstName = "John";
        student.setFirstName(firstName);
        assertEquals(firstName, student.getFirstName(), "First name should match the set value");
    }

    @Test
    void testSetAndGetLastName() {
        String lastName = "Doe";
        student.setLastName(lastName);
        assertEquals(lastName, student.getLastName(), "Last name should match the set value");
    }

    @Test
    void testSetAndGetStudentNumber() {
        String studentNumber = "SE/1123/188";
        student.setStudentNumber(studentNumber);
        assertEquals(studentNumber, student.getStudentNumber(), "Student number should match the set value");
    }

    @Test
    void testSetAndGetPassword() {
        String password = "$2y$10$h.fpN.DG3Cqsd.6TMOPH.OCpxjTCp1VCoFQg3gx4DK1G.2tzohiKC";
        student.setPassword(password);
        assertEquals(password, student.getPassword(), "Password should match the set value");
    }

    @Test
    void testSetAndGetTotalEc() {
        Integer totalEc = 75;
        student.setTotalEc(totalEc);
        assertEquals(totalEc, student.getTotalEc(), "Total EC should match the set value");
    }

    @Test
    void testSetAndGetGender() {
        String gender = "M";
        student.setGender(gender);
        assertEquals(gender, student.getGender(), "Gender should match the set value");
    }

    @Test
    void testSetAndGetBirthdate() {
        String birthdate = "2005-09-10";
        student.setBirthdate(birthdate);
        assertEquals(birthdate, student.getBirthdate(), "Birthdate should match the set value");
    }

    @Test
    void testSetAndGetMajor() {
        String major = "Software Engineering";
        student.setMajor(major);
        assertEquals(major, student.getMajor(), "Major should match the set value");
    }

    @Test
    void testSetAndGetCohort() {
        Integer cohort = 2023;
        student.setCohort(cohort);
        assertEquals(cohort, student.getCohort(), "Cohort should match the set value");
    }

    @Test
    void testInitialState() {
        Student newStudent = new Student();
        assertNull(newStudent.getId(), "Initial ID should be null");
        assertNull(newStudent.getFirstName(), "Initial first name should be null");
        assertNull(newStudent.getLastName(), "Initial last name should be null");
        assertNull(newStudent.getStudentNumber(), "Initial student number should be null");
        assertNull(newStudent.getPassword(), "Initial password should be null");
        assertNull(newStudent.getTotalEc(), "Initial total EC should be null");
        assertNull(newStudent.getGender(), "Initial gender should be null");
        assertNull(newStudent.getBirthdate(), "Initial birthdate should be null");
        assertNull(newStudent.getMajor(), "Initial major should be null");
        assertNull(newStudent.getCohort(), "Initial cohort should be null");
    }

    @Test
    void testToString() {
        Long id = 123L;
        String firstName = "John";
        String lastName = "Doe";
        String studentNumber = "SE/1123/188";
        Integer totalEc = 75;
        
        student.setId(id);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentNumber(studentNumber);
        student.setTotalEc(totalEc);
        
        String expected = String.format("Student{id=%d, firstName='%s', lastName='%s', studentNumber='%s', totalEc=%d}",
                id, firstName, lastName, studentNumber, totalEc);
        
        assertEquals(expected, student.toString(), "toString should return the expected string format");
    }
}
