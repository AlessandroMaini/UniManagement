package com.example.unimanagement.entities;

import jakarta.persistence.*;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true)
    private Integer grade;

    @Column(nullable = true)
    private LocalDate examinationDate;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Course course;

    public Enrollment() {
    }

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.examinationDate = null;
        this.grade = null;
    }

    public boolean isEvaluated() {
        return !(examinationDate == null || grade == null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) throws InvalidAttributeValueException {
        if (grade < 18 || grade > 30)
            throw new InvalidAttributeValueException();
        else
            this.grade = grade;
    }

    public LocalDate getExaminationDate() {
        return examinationDate;
    }

    public void setExaminationDate(LocalDate examinationDate) {
        this.examinationDate = examinationDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
