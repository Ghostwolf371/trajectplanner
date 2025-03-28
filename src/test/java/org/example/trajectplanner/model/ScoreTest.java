package org.example.trajectplanner.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {
    @Test
    public void testScoreCreation() {
        Score score = new Score();
        score.setId("1");
        score.setStudentId(1);
        score.setExamId(100);
        score.setScoreValue("8.5");

        assertEquals("1", score.getId());
        assertEquals(Integer.valueOf(1), score.getStudentId());
        assertEquals(Integer.valueOf(100), score.getExamId());
        assertEquals("8.5", score.getScoreValue());
    }

    @Test
    public void testScoreCopyConstructor() {
        Score original = new Score();
        original.setId("1");
        original.setStudentId(1);
        original.setExamId(100);
        original.setScoreValue("8.5");

        Score copy = new Score(original);
        
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getStudentId(), copy.getStudentId());
        assertEquals(original.getExamId(), copy.getExamId());
        assertEquals(original.getScoreValue(), copy.getScoreValue());
    }

    @Test
    public void testGetScore() {
        Score score = new Score();
        score.setScoreValue("8.5");
        
        assertEquals(8.5, score.getScore(), 0.001);
    }
}