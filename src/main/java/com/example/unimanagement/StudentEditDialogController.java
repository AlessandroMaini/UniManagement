package com.example.unimanagement;

import com.example.unimanagement.entities.Student;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import javax.management.InvalidAttributeValueException;

/**
 * Student edit controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
public class StudentEditDialogController {

    @FXML private TextField serialField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField residenceField;
    @FXML private DatePicker birthdayPicker;

    Student student = new Student();
    String serial;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> student.setFirstName(newValue));
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> student.setLastName(newValue));
        residenceField.textProperty().addListener((observable, oldValue, newValue) -> student.setResidence(newValue));
        birthdayPicker.valueProperty().addListener((observable, oldValue, newValue) -> student.setBirthday(newValue));
    }

    /**
     * Initializes the controller in case of edit.
     */
    @FXML
    public void initializeEdit(){
        serialField.editableProperty().set(false);
    }

    /**
     * Initializes the controller in case of new.
     */
    @FXML
    public void initializeNew() {
        serialField.textProperty().addListener((observable, oldValue, newValue) -> serial = newValue);
    }

    /**
     * Updates a student with the new values.
     * @param old the student to update
     * @throws InvalidAttributeValueException in case of wrong serial
     */
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

    /**
     * Updates the view with the student infos.
     */
    @FXML
    private void updateLabels() {
        serialField.setText(serial);
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        residenceField.setText(student.getResidence());
        birthdayPicker.setValue(student.getBirthday());
    }
}
