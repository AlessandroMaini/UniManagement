package com.example.unimanagement;

import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;

public class GeneralOverviewController {

    @FXML private Parent students;
    @FXML private Parent courses;
    @FXML private Parent teachers;

    @FXML private StudentTabController studentsController;
    @FXML private CourseTabController coursesController;
    @FXML private TeacherTabController teachersController;

    EntityManagerFactory emf;

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        initializeTabs();
    }

    @FXML
    private void initializeTabs() {
        studentsController.setEmf(emf);
        coursesController.setEmf(emf);
        teachersController.setEmf(emf);
        teachersController.setCourseController(coursesController);
    }
}
