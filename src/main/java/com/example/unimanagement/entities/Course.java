package com.example.unimanagement.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A university course.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne
    private Teacher teacher; // the teacher who holds the course

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Enrollment> enrollmentList;

    public Course() {
    }

    public Course(String name) {
        this.name = name;
        this.teacher = null;
        this.enrollmentList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getTeacherName() {
        if (teacher == null)
            return "Unassigned";
        else
            return teacher.getFirstName() + " " + teacher.getLastName();
    }

    public List<Enrollment> getEnrollmentList() {
        return enrollmentList;
    }

    public void setEnrollmentList(List<Enrollment> enrollmentList) {
        this.enrollmentList = enrollmentList;
    }

    @Override
    public String toString() {
        return "Course{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
