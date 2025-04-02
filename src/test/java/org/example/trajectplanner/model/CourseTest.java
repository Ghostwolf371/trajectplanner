package org.example.trajectplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CourseTest {

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
    }

    @Test
    void testSetAndGetId() {
        String id = "CS101";
        course.setId(id);
        assertEquals(id, course.getId(), "Course ID should match the set value");
    }

    @Test
    void testSetAndGetCode() {
        String code = "SE1234";
        course.setCode(code);
        assertEquals(code, course.getCode(), "Course code should match the set value");
    }

    @Test
    void testSetAndGetName() {
        String name = "Introduction to Programming";
        course.setName(name);
        assertEquals(name, course.getName(), "Course name should match the set value");
    }

    @Test
    void testSetAndGetEc() {
        Integer ec = 5;
        course.setEc(ec);
        assertEquals(ec, course.getEc(), "EC value should match the set value");
    }

    @Test
    void testSetAndGetDescription() {
        String description = "This course introduces basic programming concepts";
        course.setDescription(description);
        assertEquals(description, course.getDescription(), "Description should match the set value");
    }

    @Test
    void testSetAndGetSemester() {
        Integer semester = 1;
        course.setSemester(semester);
        assertEquals(semester, course.getSemester(), "Semester should match the set value");
    }

    @Test
    void testSetAndGetBlock() {
        Integer block = 2;
        course.setBlock(block);
        assertEquals(block, course.getBlock(), "Block should match the set value");
    }

    @Test
    void testSetAndGetSemesterName() {
        String semesterName = "Fall 2023";
        course.setSemesterName(semesterName);
        assertEquals(semesterName, course.getSemesterName(), "Semester name should match the set value");
    }

    @Test
    void testInitialState() {
        Course newCourse = new Course();
        assertNull(newCourse.getId(), "Initial ID should be null");
        assertNull(newCourse.getCode(), "Initial code should be null");
        assertNull(newCourse.getName(), "Initial name should be null");
        assertNull(newCourse.getEc(), "Initial EC should be null");
        assertNull(newCourse.getDescription(), "Initial description should be null");
        assertNull(newCourse.getSemester(), "Initial semester should be null");
        assertNull(newCourse.getBlock(), "Initial block should be null");
        assertNull(newCourse.getSemesterName(), "Initial semester name should be null");
    }
} 