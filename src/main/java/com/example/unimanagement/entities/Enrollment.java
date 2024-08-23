package com.example.unimanagement.entities;

import com.example.unimanagement.entities.keys.EnrollmentKey;
import jakarta.persistence.*;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
@IdClass(EnrollmentKey.class)
public class Enrollment {

    @Id
    @ManyToOne
    private Student student;

    @Id
    @ManyToOne
    private Course course;

    @Column(nullable = true)
    private Integer grade;

    @Column(nullable = true)
    private LocalDate examinationDate;

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
