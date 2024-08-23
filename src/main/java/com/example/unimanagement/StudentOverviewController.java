package com.example.unimanagement;

import com.example.unimanagement.entities.Enrollment;
import com.example.unimanagement.entities.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

public class StudentOverviewController {

    @FXML private Label studentFirstNameLabel;
    @FXML private Label studentLastNameLabel;
    @FXML private Label studentSerialLabel;
    @FXML private Label avgGradeLabel;

    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, Integer> enrollmentCourseCodeColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentCourseNameColumn;
    @FXML private TableColumn<Enrollment, Optional<LocalDate>> enrollmentExaminationDateColumn;
    @FXML private TableColumn<Enrollment, Optional<Integer>> enrollmentGradeColumn;

    Student student;
    EntityManagerFactory emf;

    @FXML
    public void initialize() {

        enrollmentCourseCodeColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleObjectProperty<>(enrollment.getCourse().getId());
        });
        enrollmentCourseNameColumn.setCellValueFactory(cellData -> {
            Enrollment enrollment = cellData.getValue();
            return new SimpleStringProperty(enrollment.getCourse().getName());
        });
        enrollmentExaminationDateColumn.setCellValueFactory(new PropertyValueFactory<>("examinationDate"));
        enrollmentGradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void setStudent(Student student) {
        this.student = student;
        uploadData();
    }

    @FXML
    private void uploadData() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Student mergedStudent = em.merge(student);
            enrollmentTable.setItems(FXCollections.observableList(mergedStudent.getEnrollmentList()));

            em.getTransaction().commit();

            studentFirstNameLabel.setText(student.getFirstName());
            studentLastNameLabel.setText(student.getLastName());
            studentSerialLabel.setText(student.getSerial());
            avgGradeLabel.setText(getAvgGradeQuery());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAvgGradeQuery() {
        String avgGrade = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = """
                    SELECT AVG(e.grade)
                    FROM Enrollment e
                    WHERE e.grade IS NOT NULL AND e.student = :student
                    """;

            TypedQuery<Double> q = em.createQuery(jpql, Double.class);
            q.setParameter("student", student);
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
    public void handleDrop() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex();
            Enrollment enrollment = enrollmentTable.getItems().get(selectedIndex);
            if (enrollment.isEvaluated())
                throw new IllegalArgumentException();

            em.getTransaction().begin();

            Enrollment mergedEnrollment = em.merge(enrollment);
            em.remove(mergedEnrollment);

            em.getTransaction().commit();
            enrollmentTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoCourseSelectedAlert();
        } catch (IllegalArgumentException e) {
            showInvalidSelectionAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Returns the index of the selected item in the TableView component
     * @return the index of the selected item
     */
    int selectedIndex() {
        int selectedIndex = enrollmentTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            throw new NoSuchElementException();
        }
        return selectedIndex;
    }

    /**
     * Shows a simple warning dialog in case of no selection
     */
    void showNoCourseSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Course Selected");
        alert.setContentText("Please select a course in the table.");
        alert.showAndWait();
    }

    private void showInvalidSelectionAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Selection");
        alert.setHeaderText("Cannot Drop this Course");
        alert.setContentText("Please select a course with no evaluation.");
        alert.showAndWait();
    }
}
