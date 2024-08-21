package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class CourseEditDialogController {

    @FXML private TextField nameField;

    Course course = new Course();

    @FXML
    public void initialize() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> course.setName(newValue));
    }

    public void updateCourse(Course old) {
        old.setName(course.getName());
    }

    public void setCourse(Course course) {
        this.course.setName(course.getName());
        updateLabels();
    }

    private void updateLabels() {
        nameField.setText(course.getName());
    }
}
