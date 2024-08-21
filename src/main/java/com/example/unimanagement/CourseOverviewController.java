package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Enrollment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
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

public class CourseOverviewController {

    @FXML private Label courseTitleLabel;
    @FXML private Label teacherLabel;
    @FXML private Label avgGradeLabel;
    @FXML private Label nStudentsLabel;

    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, String> enrollmentSerialColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentFirstNameColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentLastNameColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentResidenceColumn;
    @FXML private TableColumn<Enrollment, LocalDate> enrollmentBirthdayColumn;
    @FXML private TableColumn<Enrollment, Optional<LocalDate>> enrollmentExaminationDateColumn;
    @FXML private TableColumn<Enrollment, Optional<Integer>> enrollmentGradeColumn;

    Course course;
    EntityManagerFactory emf;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {

        enrollmentSerialColumn.setCellValueFactory(new PropertyValueFactory<>("studentSerial"));
        enrollmentFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentFirstName"));
        enrollmentLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentLastName"));
        enrollmentResidenceColumn.setCellValueFactory(new PropertyValueFactory<>("studentResidence"));
        enrollmentBirthdayColumn.setCellValueFactory(new PropertyValueFactory<>("studentBirthday"));
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
     * Upload the course info from the DB and updates the view
     */
    @FXML
    private void uploadData() {
        courseTitleLabel.setText(this.course.getName());
        teacherLabel.setText(this.course.getTeacherName());
        avgGradeLabel.setText(getAvgGradeQuery());
        nStudentsLabel.setText(getNStudentsQuery());

        enrollmentTable.setItems(getEnrollmentData());
    }

    /**
     * Returns the list of enrollments for the course
     * @return the list of enrollments for the course
     */
    private ObservableList<Enrollment> getEnrollmentData() {
        ObservableList<Enrollment> enrollmentObservableList = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = """
                     SELECT e
                     FROM Course c JOIN c.enrollmentList e
                     WHERE c.id = :courseId
                    """;

            TypedQuery<Enrollment> q = em.createQuery(jpql, Enrollment.class);
            q.setParameter("courseId", course.getId());
            enrollmentObservableList = FXCollections.observableList(q.getResultList());

            em.getTransaction().commit(); // ends the transaction
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrollmentObservableList;
    }

    /**
     * Returns the number of students in the course
     * @return the number of students in the course
     */
    private String getNStudentsQuery() {
        String nStudents = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = """
                    SELECT COUNT(e)
                    FROM Course c JOIN c.enrollmentList e
                    WHERE c.id = :courseId
                    """;

            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            q.setParameter("courseId", course.getId());
            Long count = q.getSingleResult();

            em.getTransaction().commit();
            nStudents = count.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nStudents;
    }

    /**
     * Returns the average grade of the course exams
     * @return the average grade of the course exams
     */
    private String getAvgGradeQuery() {
        String avgGrade = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = """
                    SELECT AVG(e.grade)
                    FROM Course c JOIN c.enrollmentList e
                    WHERE e.grade IS NOT NULL AND c.id = :courseId
                    """;

            TypedQuery<Double> q = em.createQuery(jpql, Double.class);
            q.setParameter("courseId", course.getId());
            Double avg = q.getSingleResult();

            em.getTransaction().commit();
            if (avg == null)
                avgGrade = "No Data";
            else
                avgGrade = avg.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avgGrade;
    }

    /**
     * Switches the scene to the general overview
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
            e.printStackTrace();
        }
    }
}
