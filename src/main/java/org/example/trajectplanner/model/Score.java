package org.example.trajectplanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Score {
    private String id;
    
    @JsonProperty("student_id")
    private Integer studentId;
    
    @JsonProperty("student_number")
    private String studentNumber;
    
    @JsonProperty("exam_id")
    private Integer examId;
    
    @JsonProperty("course_name")
    private String courseName;
    
    @JsonProperty("course_code")
    private String courseCode;
    
    @JsonProperty("score_value")
    private String scoreValue;
    
    @JsonProperty("score_datetime")
    private String scoreDateTime;

    // Constructor
    public Score() {}

    // Copy constructor
    public Score(Score other) {
        this.id = other.id;
        this.studentId = other.studentId;
        this.studentNumber = other.studentNumber;
        this.examId = other.examId;
        this.courseName = other.courseName;
        this.courseCode = other.courseCode;
        this.scoreValue = other.scoreValue;
        this.scoreDateTime = other.scoreDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(String scoreValue) {
        this.scoreValue = scoreValue;
    }

    public String getScoreDateTime() {
        return scoreDateTime;
    }

    public void setScoreDateTime(String scoreDateTime) {
        this.scoreDateTime = scoreDateTime;
    }

    // Convenience methods
    public Double getScore() {
        return scoreValue != null ? Double.parseDouble(scoreValue) : null;
    }

    public void setScore(double score) {
        this.scoreValue = String.valueOf(score);
    }

    public String getDate() {
        return scoreDateTime;
    }

    public void setDate(String date) {
        this.scoreDateTime = date;
    }
}
