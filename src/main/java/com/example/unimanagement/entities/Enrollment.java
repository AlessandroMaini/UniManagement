package com.example.unimanagement.entities;

import com.example.unimanagement.entities.keys.EnrollmentKey;
import jakarta.persistence.*;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;

/**
 * The enrollment of a student in a course (join table).
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
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

    private Integer grade;

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

    public void setGrade(Integer grade) throws InvalidAttributeValueException {
        if (grade < 18 || grade > 30)
            throw new InvalidAttributeValueException();
        else
            this.grade = grade;
    }

    public void setExaminationDate(LocalDate examinationDate) {
        this.examinationDate = examinationDate;
    }

    public Student getStudent() {
        return student;
    }

    public Integer getGrade() {
        return grade;
    }

    public LocalDate getExaminationDate() {
        return examinationDate;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Enrollment{" + "student=" + student + ", course=" + course + ", grade=" + grade + ", examinationDate=" + examinationDate + '}';
    }
}
