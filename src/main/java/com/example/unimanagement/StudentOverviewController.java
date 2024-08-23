package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Student overview controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
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

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
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

    /**
     * Upload the student info from the DB and updates the view.
     */
    @FXML
    private void uploadData() {
        try (EntityManager em = emf.createEntityManager()) { // Session necessary because of the lazy fetching of enrollments
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

    /**
     * Returns the average grade of the student.
     * @return the average grade of the student
     */
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

    /**
     * Adds a valuation to the student's selected enrollment.
     */
    @FXML
    public void handleEvaluate() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("enrollment-evaluate-view.fxml"));
            DialogPane view = loader.load();
            EnrollmentEvaluateDialogController controller = loader.getController();

            int selectedIndex = selectedIndex();
            Enrollment enrollment = enrollmentTable.getItems().get(selectedIndex);
            if (enrollment.isEvaluated())
                throw new IllegalArgumentException();
            controller.setEnrollment(enrollment);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Evaluate");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Enrollment mergedEnrollment = em.merge(enrollment);
                controller.updateEnrollment(mergedEnrollment);

                Student mergedStudent = em.merge(student);
                enrollmentTable.setItems(FXCollections.observableList(mergedStudent.getEnrollmentList()));

                em.getTransaction().commit();
                avgGradeLabel.setText(getAvgGradeQuery());
            }
        } catch (NoSuchElementException e) {
            showNoCourseSelectedAlert();
        } catch (InvalidAttributeValueException | NumberFormatException e) {
            showInvalidAttributeValueAlert();
        } catch (IllegalArgumentException e) {
            showInvalidSelectionAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enrolls the student to a new course.
     */
    @FXML
    public void handleEnroll() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("course-add-view.fxml"));
            DialogPane view = loader.load();
            CourseAddDialogController controller = loader.getController();

            controller.setCourseList(getNotEnrolledCourses());

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Enroll");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Enrollment enrollment = new Enrollment(em.merge(student), em.merge(controller.getNewCourse()));
                em.persist(enrollment);

                em.getTransaction().commit();
                enrollmentTable.getItems().add(enrollment);
            }
        } catch (NoSuchElementException e) {
            showNoCourseSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the courses in which the student is not enrolled.
     * @return the list the courses in which the student is not enrolled
     */
    private List<Course> getNotEnrolledCourses() {
        List<Course> courseList = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = """
                     SELECT c
                     FROM Course c
                     WHERE c NOT IN (   SELECT e.course
                                        FROM Enrollment e
                                        WHERE e.student = :student
                                    )
                    """;

            TypedQuery<Course> q = em.createQuery(jpql, Course.class);
            q.setParameter("student", student);
            courseList = q.getResultList();

            em.getTransaction().commit(); // ends the transaction
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseList;
    }

    /**
     * Drops a student enrollment.
     */
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
            e.printStackTrace();
        }
    }

    /**
     * Returns the index of the selected item in the TableView component.
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
     * Shows a simple warning dialog in case of no selection.
     */
    void showNoCourseSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Course Selected");
        alert.setContentText("Please select a course in the table.");
        alert.showAndWait();
    }

    /**
     * Shows a simple warning dialog in case of wrong selection.
     */
    void showInvalidSelectionAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Selection");
        alert.setHeaderText("Cannot Drop nor Evaluate this Course");
        alert.setContentText("Please select a course with no evaluation.");
        alert.showAndWait();
    }

    /**
     * Shows a simple warning dialog in case of wrong attributes.
     */
    void showInvalidAttributeValueAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Grade");
        alert.setHeaderText("Invalid Grade");
        alert.setContentText("Please select a grade between 18 and 30.");
        alert.showAndWait();
    }
}
