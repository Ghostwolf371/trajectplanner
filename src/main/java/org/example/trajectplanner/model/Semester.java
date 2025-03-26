package org.example.trajectplanner.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Semester {
    private int id;
    private int semester;
    
    @JsonProperty("semester_name")
    private String semesterName;
    
    private List<SemesterCourse> courses;

    // Inner class to represent the course structure in the semester API
    public static class SemesterCourse {
        @JsonProperty("course_id")
        private int courseId;
        
        @JsonProperty("course_name")
        private String courseName;
        
        @JsonProperty("course_code")
        private String courseCode;
        
        @JsonProperty("course_description")
        private String courseDescription;
        
        private Integer ec;
        private Integer block;

        // Getters and Setters
        public int getCourseId() { return courseId; }
        public void setCourseId(int courseId) { this.courseId = courseId; }

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }

        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

        public String getCourseDescription() { return courseDescription; }
        public void setCourseDescription(String courseDescription) { this.courseDescription = courseDescription; }

        public Integer getEc() { return ec; }
        public void setEc(Integer ec) { this.ec = ec; }

        public Integer getBlock() { return block; }
        public void setBlock(Integer block) { this.block = block; }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    
    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }
    
    public List<SemesterCourse> getCourses() { return courses; }
    public void setCourses(List<SemesterCourse> courses) { this.courses = courses; }
    
}
