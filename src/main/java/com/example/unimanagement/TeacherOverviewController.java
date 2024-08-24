package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Teacher overview controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-23
 */
public class TeacherOverviewController {

    @FXML private Label teacherFirstNameLabel;
    @FXML private Label teacherLastNameLabel;
    @FXML private Label nCoursesLabel;

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, Long> courseNStudentsColumn;

    Teacher teacher;
    ObservableList<Course> courseObservableList = FXCollections.observableArrayList();
    Map<Course, Long> courseNStudents = new HashMap<>(); // maps: course -> number of students
    EntityManagerFactory emf;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        courseTable.setItems(courseObservableList);
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseNStudentsColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleObjectProperty<>(courseNStudents.get(course));
        });
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        uploadData();
        setCourseNStudents();
    }

    /**
     * Upload the teacher info from the DB and updates the view.
     */
    @FXML
    private void uploadData() {
        try (EntityManager em = emf.createEntityManager()) { // Session necessary because of the lazy fetching of courses
            em.getTransaction().begin();

            Teacher mergedTeacher = em.merge(teacher);
            courseObservableList.addAll(mergedTeacher.getCourseList());

            em.getTransaction().commit();

            teacherFirstNameLabel.setText(teacher.getFirstName());
            teacherLastNameLabel.setText(teacher.getLastName());
            nCoursesLabel.setText(String.valueOf(courseObservableList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the (course -> number of students) map.
     */
    private void setCourseNStudents() {
        courseObservableList.forEach(course -> courseNStudents.put(course, getNStudentsFromCourseQuery(course)));
    }

    /**
     * Returns the number of students in a specified course.
     * @param course the course we want the number of students
     * @return the number of students in the course
     */
    private Long getNStudentsFromCourseQuery(Course course) {
        Long nStudents = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = """
                    SELECT COUNT(e)
                    FROM Enrollment e
                    WHERE e.course = :course
                    """;

            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            q.setParameter("course", course);
            nStudents = q.getSingleResult();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nStudents;
    }

    /**
     * Adds a course to the teacher courseList.
     */
    @FXML
    public void handleAdd() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("course-add-view.fxml"));
            DialogPane view = loader.load();
            CourseAddDialogController controller = loader.getController();

            controller.setCourseList(getUnassignedCourses());

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Course");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Course course = em.merge(controller.getNewCourse());
                course.setTeacher(teacher);

                em.getTransaction().commit();
                courseObservableList.add(course);
                nCoursesLabel.setText(String.valueOf(courseObservableList.size()));
                setCourseNStudents();
            }
        } catch (NoSuchElementException e) {
            showNoCourseSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the unassigned courses.
     * @return the list of unassigned courses
     */
    private List<Course> getUnassignedCourses() {
        List<Course> courseList = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = """
                     SELECT c
                     FROM Course c
                     WHERE c.teacher IS NULL
                    """;

            TypedQuery<Course> q = em.createQuery(jpql, Course.class);
            courseList = q.getResultList();

            em.getTransaction().commit(); // ends the transaction
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseList;
    }

    /**
     * Removes a course from the teacher courseList.
     */
    @FXML
    public void handleLeave() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex();
            em.getTransaction().begin();

            Course course = em.merge(courseObservableList.get(selectedIndex));
            course.setTeacher(null);

            em.getTransaction().commit();
            courseObservableList.remove(selectedIndex);
            nCoursesLabel.setText(String.valueOf(courseObservableList.size()));
        } catch (NoSuchElementException e) {
            showNoCourseSelectedAlert();
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

            Stage stage = (Stage) courseTable.getScene().getWindow();
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
        int selectedIndex = courseTable.getSelectionModel().getSelectedIndex();
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
}
