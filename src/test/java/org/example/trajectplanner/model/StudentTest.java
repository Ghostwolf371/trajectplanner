package org.example.trajectplanner.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {
    @Test
    public void testValidStudentNumber() {
        Student student = new Student();
        student.setStudentNumber("SE/2324/01");
        
        assertTrue(student.getStudentNumber().startsWith("SE/"));
        assertTrue(student.getStudentNumber().length() > 5);
    }

    @Test
    public void testValidEcRange() {
        Student student = new Student();
        student.setTotalEc(30);
        
        assertTrue(student.getTotalEc() >= 0);
        assertTrue(student.getTotalEc() <= 60);
    }

    @Test
    public void testValidName() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        
        assertTrue(student.getFirstName().length() > 0);
        assertTrue(student.getLastName().length() > 0);
    }
}
