package com.example.unimanagement.entities;

import jakarta.persistence.*;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    private String serial;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE)
    private List<Enrollment> enrollmentList;

    private String firstName;

    private String lastName;

    private String residence;

    private LocalDate birthday;

    public Student() {
    }

    public Student(String firstName, String lastName, String residence, LocalDate birthday) {
        this.serial = "000000";
        this.enrollmentList = new ArrayList<>();
        this.firstName = firstName;
        this.lastName = lastName;
        this.residence = residence;
        this.birthday = birthday;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) throws InvalidAttributeValueException {
        if (serial.length() != 6 || !isNumeric(serial))
            throw new InvalidAttributeValueException();
        else
            this.serial = serial;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null)
            return false;
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public List<Enrollment> getEnrollmentList() {
        return enrollmentList;
    }

    public void setEnrollmentList(List<Enrollment> enrollmentList) {
        this.enrollmentList = enrollmentList;
    }
}
