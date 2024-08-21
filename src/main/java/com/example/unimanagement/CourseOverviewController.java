package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Enrollment;
import com.example.unimanagement.persistence.CustomPersistenceUnitInfo;
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
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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

    /**
     * Creates a persistence context
     */
//    static Map<String, String> props = new HashMap<>();
//    static {
//        props.put("hibernate.show_sql", "true");
//    }
//    EntityManagerFactory emf = new HibernatePersistenceProvider()
//            .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), props);
    EntityManagerFactory emf;

    public CourseOverviewController() {
        Map<String, String> props = new HashMap<>();
        props.put("hibernate.show_sql", "true");
        emf = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), props);
    }

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

    @FXML
    public void setCourse(Course course) {
        this.course = course;

        courseTitleLabel.setText(this.course.getName());
        teacherLabel.setText(this.course.getTeacherName());
        avgGradeLabel.setText(getAvgGradeQuery());
        nStudentsLabel.setText(getNStudentsQuery());

        enrollmentTable.setItems(getEnrollmentData());
    }

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

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("general-overview-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) enrollmentTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
