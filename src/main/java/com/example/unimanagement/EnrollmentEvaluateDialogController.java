package com.example.unimanagement;

import com.example.unimanagement.entities.Enrollment;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;

/**
 * Enrollment evaluation controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
public class EnrollmentEvaluateDialogController {

    @FXML private TextField studentField;
    @FXML private TextField courseFiled;
    @FXML private TextField gradeField;
    @FXML private DatePicker examinationDatePicker;

    String grade;
    LocalDate examinationDate;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        gradeField.textProperty().addListener((observable, oldValue, newValue) -> grade = newValue);
        examinationDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> examinationDate = newValue);
    }

    public void setEnrollment(Enrollment enrollment) {
        studentField.setText(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());
        courseFiled.setText(enrollment.getCourse().getName());
        examinationDatePicker.setValue(LocalDate.now());
    }

    /**
     * Updates an enrollment with the evaluation.
     * @param enrollment the enrollment to evaluate
     * @throws InvalidAttributeValueException in case of wrong grade
     */
    public void updateEnrollment(Enrollment enrollment) throws InvalidAttributeValueException {
        enrollment.setGrade(Integer.valueOf(grade));
        enrollment.setExaminationDate(examinationDate);
    }
}
