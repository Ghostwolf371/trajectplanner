package org.example.trajectplanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private Long id;
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    @JsonProperty("student_number")
    private String studentNumber;
    
    private String password;
    
    @JsonProperty("total_ec")
    private Integer totalEc;
    
    private String gender;
    private String birthdate;
    private String major;
    private Integer cohort;

    // Getters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getStudentNumber() { return studentNumber; }
    public String getPassword() { return password; }
    public Integer getTotalEc() { return totalEc; }
    public String getGender() { return gender; }
    public String getBirthdate() { return birthdate; }
    public String getMajor() { return major; }
    public Integer getCohort() { return cohort; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public void setPassword(String password) { this.password = password; }
    public void setTotalEc(Integer totalEc) { this.totalEc = totalEc; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
    public void setMajor(String major) { this.major = major; }
    public void setCohort(Integer cohort) { this.cohort = cohort; }

    // Optional: Add a toString method for debugging
    @Override
    public String toString() {
        return String.format("Student{id=%d, firstName='%s', lastName='%s', studentNumber='%s', totalEc=%d}",
                id, firstName, lastName, studentNumber, totalEc);
    }
}
