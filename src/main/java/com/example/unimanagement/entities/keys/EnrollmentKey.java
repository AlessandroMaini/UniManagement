package com.example.unimanagement.entities.keys;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Student;

import java.io.Serializable;
import java.util.Objects;

/**
 * The primary key of enrollment.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
public class EnrollmentKey implements Serializable {

    private Course course;
    private Student student;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EnrollmentKey that = (EnrollmentKey) o;
        return Objects.equals(course, that.course) && Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, student);
    }
}
