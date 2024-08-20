package com.example.unimanagement.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne
    private Teacher teacher;

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
            return "";
        else
            return teacher.getFirstName() + " " + teacher.getLastName();
    }
}
