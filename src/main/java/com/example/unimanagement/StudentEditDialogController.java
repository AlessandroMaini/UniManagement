package com.example.unimanagement;

import com.example.unimanagement.entities.Student;
import com.example.unimanagement.entities.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import javax.management.InvalidAttributeValueException;

public class StudentEditDialogController {

    @FXML private TextField serialField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField residenceField;
    @FXML private DatePicker birthdayPicker;

    Student student = new Student();
    String serial;

    @FXML
    public void initialize() {
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> student.setFirstName(newValue));
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> student.setLastName(newValue));
        residenceField.textProperty().addListener((observable, oldValue, newValue) -> student.setResidence(newValue));
        birthdayPicker.valueProperty().addListener((observable, oldValue, newValue) -> student.setBirthday(newValue));
    }

    @FXML
    public void initializeEdit(){
        serialField.editableProperty().set(false);
    }

    @FXML
    public void initializeNew() {
        serialField.textProperty().addListener((observable, oldValue, newValue) -> serial = newValue);
    }

    public void updateStudent(Student old) throws InvalidAttributeValueException {
        old.setSerial(serial);
        old.setFirstName(student.getFirstName());
        old.setLastName(student.getLastName());
        old.setResidence(student.getResidence());
        old.setBirthday(student.getBirthday());
    }

    public void setStudent(Student student) {
        serial = student.getSerial();
        this.student.setFirstName(student.getFirstName());
        this.student.setLastName(student.getLastName());
        this.student.setResidence(student.getResidence());
        this.student.setBirthday(student.getBirthday());
        updateLabels();
    }

    private void updateLabels() {
        serialField.setText(serial);
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        residenceField.setText(student.getResidence());
        birthdayPicker.setValue(student.getBirthday());
    }
}
