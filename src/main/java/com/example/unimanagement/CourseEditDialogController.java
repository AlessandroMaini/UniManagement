package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Course edit controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
public class CourseEditDialogController {

    @FXML private TextField nameField;

    Course course = new Course();

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> course.setName(newValue));
    }

    /**
     * Updates a course with the new values.
     * @param old the course to update
     */
    public void updateCourse(Course old) {
        old.setName(course.getName());
    }

    public void setCourse(Course course) {
        this.course.setName(course.getName());
        updateLabels();
    }

    /**
     * Updates the view with the course infos.
     */
    @FXML
    private void updateLabels() {
        nameField.setText(course.getName());
    }
}
