package org.example.trajectplanner.Modal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tentamen {
    private String id;
    @JsonProperty("course_id")
    private String courseId;
    @JsonProperty("course_name")
    private String cursesNaam;
    private String semester;
    @JsonProperty("date")
    private String datum;
    private String type;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    public String getCursesNaam() {
        return cursesNaam;
    }
    public void setCursesNaam(String cursesNaam) {
        this.cursesNaam = cursesNaam;
    }
    public String getSemester() {
        return semester;
    }
    public void setSemester(String semester) {
        this.semester = semester;
    }
    public String getDatum() {
        return datum;
    }
    public void setDatum(String datum) {
        this.datum = datum;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    
}
