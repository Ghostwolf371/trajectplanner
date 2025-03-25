package org.example.trajectplanner.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private int id;
    
    @JsonProperty("student_number")
    private String studentNumber;
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    private String major;
    
    private Integer cohort;
    
    private String gender;
    
    @JsonProperty("birthdate")
    @JsonAlias("birth_date")
    private LocalDate birthdate;
    
    private String password;

    @JsonProperty("total_ec")
    private Integer totalEc;

    public Student() {
    }

    public Student(int id, String studentNumber, String firstName, String lastName, String major,
                  Integer cohort, String gender, LocalDate birthdate, String password, Integer totalEc) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.cohort = cohort;
        this.gender = gender;
        this.birthdate = birthdate;
        this.password = password;
        this.totalEc = totalEc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getCohort() {
        return cohort;
    }

    public void setCohort(Integer cohort) {
        this.cohort = cohort;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTotalEc() {
        return totalEc;
    }

    public void setTotalEc(Integer totalEc) {
        this.totalEc = totalEc;
    }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 