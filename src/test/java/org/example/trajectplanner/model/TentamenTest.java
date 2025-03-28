package org.example.trajectplanner.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TentamenTest {
    @Test
    public void testTentamenCreation() {
        Tentamen tentamen = new Tentamen();
        tentamen.setId(1);
        tentamen.setCourseId(100);
        tentamen.setCourseName("Java Programming");
        tentamen.setSemester(1);
        tentamen.setType("Written");
        tentamen.setDate("2024-03-20");

        assertEquals("1", tentamen.getId());
        assertEquals(100, tentamen.getCourseId());
        assertEquals("Java Programming", tentamen.getCourseName());
        assertEquals(1, tentamen.getSemester());
        assertEquals("Written", tentamen.getType());
        assertEquals("2024-03-20", tentamen.getDate());
    }

    @Test
    public void testPropertyBindings() {
        Tentamen tentamen = new Tentamen();
        tentamen.setCourseName("Java Programming");
        
        assertEquals("Java Programming", tentamen.courseNameProperty().get());
    }

    @Test
    public void testValidSemester() {
        Tentamen tentamen = new Tentamen();
        tentamen.setSemester(1);
        
        assertTrue(tentamen.getSemester() >= 1);
        assertTrue(tentamen.getSemester() <= 8);
    }

    @Test
    public void testValidExamType() {
        Tentamen tentamen = new Tentamen();
        tentamen.setType("Written");
        
        assertTrue(tentamen.getType().equals("Written") || 
                  tentamen.getType().equals("Oral") || 
                  tentamen.getType().equals("Project"));
    }

    @Test
    public void testValidDate() {
        Tentamen tentamen = new Tentamen();
        tentamen.setDate("2024-03-20");
        
        assertTrue(tentamen.getDate().matches("\\d{4}-\\d{2}-\\d{2}"));
    }
}
