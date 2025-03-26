package org.example.trajectplanner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Tentamen {
    private SimpleIntegerProperty id;
    private SimpleIntegerProperty courseId;
    private SimpleStringProperty courseName;
    private SimpleIntegerProperty semester;
    private SimpleStringProperty type;
    private SimpleStringProperty date;

    public Tentamen() {
        this.id = new SimpleIntegerProperty();
        this.courseId = new SimpleIntegerProperty();
        this.courseName = new SimpleStringProperty();
        this.semester = new SimpleIntegerProperty();
        this.type = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
    }

    @JsonProperty("id")
    public String getId() { return String.valueOf(id.get()); }
    @JsonProperty("id")
    public void setId(int id) { this.id.set(id); }

    @JsonProperty("course_id")
    public int getCourseId() { return courseId.get(); }
    @JsonProperty("course_id")
    public void setCourseId(int courseId) { this.courseId.set(courseId); }

    @JsonProperty("course_name")
    public String getCourseName() { return courseName.get(); }
    @JsonProperty("course_name")
    public void setCourseName(String courseName) { this.courseName.set(courseName); }

    @JsonProperty("semester")
    public int getSemester() { return semester.get(); }
    @JsonProperty("semester")
    public void setSemester(int semester) { this.semester.set(semester); }

    @JsonProperty("type")
    public String getType() { return type.get(); }
    @JsonProperty("type")
    public void setType(String type) { this.type.set(type); }

    @JsonProperty("date")
    public String getDate() { return date.get(); }
    @JsonProperty("date")
    public void setDate(String date) { this.date.set(date); }

    // Property getters
    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleIntegerProperty courseIdProperty() { return courseId; }
    public SimpleStringProperty courseNameProperty() { return courseName; }
    public SimpleIntegerProperty semesterProperty() { return semester; }
    public SimpleStringProperty typeProperty() { return type; }
    public SimpleStringProperty dateProperty() { return date; }
}
