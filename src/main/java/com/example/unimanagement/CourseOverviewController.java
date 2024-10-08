package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Enrollment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Course overview controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
public class CourseOverviewController {

    @FXML private Label courseTitleLabel;
    @FXML private Label teacherLabel;
    @FXML private Label avgGradeLabel;
    @FXML private Label nStudentsLabel;
    @FXML private Label courseCodeLabel;

    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, String> enrollmentStudentSerialColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentStudentFirstNameColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentStudentLastNameColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentStudentResidenceColumn;
    @FXML private TableColumn<Enrollment, LocalDate> enrollmentStudentBirthdayColumn;
    @FXML private TableColumn<Enrollment, Optional<LocalDate>> enrollmentExaminationDateColumn;
    @FXML private TableColumn<Enrollment, Optional<Integer>> enrollmentGradeColumn;

    Course course;
    ObservableList<Enrollment> enrollmentObservableList = FXCollections.observableArrayList();
    EntityManagerFactory emf;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        enrollmentTable.setItems(enrollmentObservableList);
        enrollmentStudentSerialColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getStudent().getSerial());
        });
        enrollmentStudentFirstNameColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getStudent().getFirstName());
        });
        enrollmentStudentLastNameColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getStudent().getLastName());
        });
        enrollmentStudentResidenceColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getStudent().getResidence());
        });
        enrollmentStudentBirthdayColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleObjectProperty<>(enrollment.getStudent().getBirthday());
        });
        enrollmentExaminationDateColumn.setCellValueFactory(new PropertyValueFactory<>("examinationDate"));
        enrollmentGradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void setCourse(Course course) {
        this.course = course;
        uploadData();
    }

    /**
     * Upload the course info from the DB and updates the view.
     */
    @FXML
    private void uploadData() {
        try (EntityManager em = emf.createEntityManager()) { // Session necessary because of the lazy fetching of enrollments
            em.getTransaction().begin();

            Course mergedCourse = em.merge(course);
            enrollmentObservableList.addAll(mergedCourse.getEnrollmentList());

            em.getTransaction().commit();

            courseCodeLabel.setText(course.getId() + ")");
            courseTitleLabel.setText(course.getName());
            teacherLabel.setText(course.getTeacherName());
            avgGradeLabel.setText(getAvgGradeQuery());
            nStudentsLabel.setText(String.valueOf(enrollmentObservableList.size()));
        }
    }

    /**
     * Returns the average grade of the course exams.
     * @return the average grade of the course exams
     */
    private String getAvgGradeQuery() {
        String avgGrade;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = """
                    SELECT AVG(e.grade)
                    FROM Enrollment e
                    WHERE e.grade IS NOT NULL AND e.course = :course
                    """;

            TypedQuery<Double> q = em.createQuery(jpql, Double.class);
            q.setParameter("course", course);
            Double avg = q.getSingleResult();

            em.getTransaction().commit();
            if (avg == null)
                avgGrade = "No Data";
            else
                avgGrade = avg.toString();
        }
        return avgGrade;
    }

    /**
     * Switches the scene to the general overview.
     */
    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("general-overview-view.fxml"));
            Parent root = loader.load();

            GeneralOverviewController generalOverviewController = loader.getController();
            generalOverviewController.setEmf(emf);

            Stage stage = (Stage) enrollmentTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
