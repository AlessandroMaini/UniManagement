package com.example.unimanagement.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "teacher")
    private List<Course> courseList;

    private String firstName;

    private String lastName;

    private String residence;

    private LocalDate birthday;

    public Teacher() {
    }

    public Teacher(Teacher other) {
        this.id = other.getId();
        this.courseList = new ArrayList<>();
        this.courseList.addAll(other.getCourseList());
        this.firstName = other.getFirstName();
        this.lastName = other.getLastName();
        this.residence = other.getResidence();
        this.birthday = other.getBirthday();
    }

    public Teacher(String firstName, String lastName, String residence, LocalDate birthday) {
        this.courseList = new ArrayList<>();
        this.firstName = firstName;
        this.lastName = lastName;
        this.residence = residence;
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    @Override
    public String toString() {
        return "Teacher{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", residence='" + residence + '\'' + ", birthday=" + birthday + '}';
    }
}
