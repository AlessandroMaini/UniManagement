package com.example.unimanagement;

import com.example.unimanagement.entities.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * Teacher edit controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
public class TeacherEditDialogController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField residenceField;
    @FXML private DatePicker birthdayPicker;

    Teacher teacher = new Teacher();

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> teacher.setFirstName(newValue));
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> teacher.setLastName(newValue));
        residenceField.textProperty().addListener((observable, oldValue, newValue) -> teacher.setResidence(newValue));
        birthdayPicker.valueProperty().addListener((observable, oldValue, newValue) -> teacher.setBirthday(newValue));
    }

    /**
     * Updates a teacher with the new values.
     * @param old the teacher to update
     */
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

    /**
     * Updates the view with the teacher infos.
     */
    @FXML
    private void updateLabels() {
        firstNameField.setText(teacher.getFirstName());
        lastNameField.setText(teacher.getLastName());
        residenceField.setText(teacher.getResidence());
        birthdayPicker.setValue(teacher.getBirthday());
    }
}
