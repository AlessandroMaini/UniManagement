package com.example.unimanagement;

import com.example.unimanagement.entities.Enrollment;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;

public class EnrollmentEvaluateDialogController {

    @FXML private TextField studentField;
    @FXML private TextField courseFiled;
    @FXML private TextField gradeField;
    @FXML private DatePicker examinationDatePicker;

    String grade;
    LocalDate examinationDate;

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

    public void updateEnrollment(Enrollment enrollment) throws InvalidAttributeValueException {
        enrollment.setGrade(Integer.valueOf(grade));
        enrollment.setExaminationDate(examinationDate);
    }
}
