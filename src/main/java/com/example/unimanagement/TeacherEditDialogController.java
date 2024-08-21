package com.example.unimanagement;

import com.example.unimanagement.entities.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class TeacherEditDialogController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField residenceField;
    @FXML private DatePicker birthdayPicker;

    Teacher teacher = new Teacher();

    @FXML
    public void initialize() {
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> teacher.setFirstName(newValue));
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> teacher.setLastName(newValue));
        residenceField.textProperty().addListener((observable, oldValue, newValue) -> teacher.setResidence(newValue));
        birthdayPicker.valueProperty().addListener((observable, oldValue, newValue) -> teacher.setBirthday(newValue));
    }

    public void updateTeacher(Teacher old) {
        old.setFirstName(teacher.getFirstName());
        old.setLastName(teacher.getLastName());
        old.setResidence(teacher.getResidence());
        old.setBirthday(teacher.getBirthday());
    }

    public void setTeacher(Teacher teacher) {
        this.teacher.setFirstName(teacher.getFirstName());
        this.teacher.setLastName(teacher.getLastName());
        this.teacher.setResidence(teacher.getResidence());
        this.teacher.setBirthday(teacher.getBirthday());
        updateLabels();
    }

    private void updateLabels() {
        firstNameField.setText(teacher.getFirstName());
        lastNameField.setText(teacher.getLastName());
        residenceField.setText(teacher.getResidence());
        birthdayPicker.setValue(teacher.getBirthday());
    }
}
