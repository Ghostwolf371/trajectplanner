package org.example.trajectplanner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Exam {
    @JsonProperty("exam_id")
    private String id;
    
    @JsonProperty("course_name")
    private String courseName;
    
    @JsonProperty("exam_date")
    private String examDate;
    
    @JsonProperty("exam_time")
    private String examTime;
    
    @JsonProperty("location")
    private String location;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getExamDate() { return examDate; }
    public void setExamDate(String examDate) { this.examDate = examDate; }

    public String getExamTime() { return examTime; }
    public void setExamTime(String examTime) { this.examTime = examTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}