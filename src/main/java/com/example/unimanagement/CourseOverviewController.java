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
import java.util.List;
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

        enrollmentSerialColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getStudent().getSerial());
        });
        enrollmentFirstNameColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getStudent().getFirstName());
        });
        enrollmentLastNameColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getStudent().getLastName());
        });
        enrollmentResidenceColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getStudent().getResidence());
        });
        enrollmentBirthdayColumn.setCellValueFactory(cellData -> {
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
     * Upload the course info from the DB and updates the view
     */
    @FXML
    private void uploadData() {
        try (EntityManager em = emf.createEntityManager()) { // Session necessary because of the lazy fetching of enrollments
            em.getTransaction().begin();

            Course mergedCourse = em.merge(course);
//            nStudentsLabel.setText(String.valueOf(mergedCourse.getEnrollmentList().size()));
//            nStudentsLabel.setText(getNStudentsQuery());
            enrollmentTable.setItems(FXCollections.observableList(mergedCourse.getEnrollmentList()));
//            enrollmentTable.setItems(getEnrollmentData());

            em.getTransaction().commit();

            courseTitleLabel.setText(course.getName());
            teacherLabel.setText(course.getTeacherName());
            avgGradeLabel.setText(getAvgGradeQuery());
            nStudentsLabel.setText(String.valueOf(enrollmentTable.getItems().size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                     FROM Enrollment e
                     WHERE e.course = :course
                    """;

            TypedQuery<Enrollment> q = em.createQuery(jpql, Enrollment.class);
            q.setParameter("course", course);
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
                    FROM Enrollment e
                    WHERE e.course = :course
                    """;

            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            q.setParameter("course", course);
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
