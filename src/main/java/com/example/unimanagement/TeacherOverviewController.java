package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import javafx.beans.property.SimpleObjectProperty;
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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TeacherOverviewController {

    @FXML private Label teacherFirstNameLabel;
    @FXML private Label teacherLastNameLabel;
    @FXML private Label nCoursesLabel;

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, Long> courseNStudentsColumn;

    Teacher teacher;
    EntityManagerFactory emf;
    Map<Course, Long> courseNStudents = new HashMap<>();

    @FXML
    public void initialize() {

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

    @FXML
    private void uploadData() {
        teacherFirstNameLabel.setText(teacher.getFirstName());
        teacherLastNameLabel.setText(teacher.getLastName());

        try (EntityManager em = emf.createEntityManager()) { // Session necessary because of the lazy fetching of courses
            em.getTransaction().begin();

            Teacher mergedTeacher = em.merge(teacher);
            nCoursesLabel.setText(String.valueOf(mergedTeacher.getCourseList().size()));
            courseTable.setItems(FXCollections.observableList(mergedTeacher.getCourseList()));

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCourseNStudents() {
        courseTable.getItems().forEach(course -> courseNStudents.put(course, getNStudentsFromCourseQuery(course)));
    }

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

    @FXML
    public void handleAdd() {

    }

    @FXML
    public void handleLeave() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex();
            em.getTransaction().begin();

            Course course = em.merge(courseTable.getItems().get(selectedIndex));
            course.setTeacher(null);

            em.getTransaction().commit();
            courseTable.getItems().remove(selectedIndex);
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
     * Returns the index of the selected item in the TableView component
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
     * Shows a simple warning dialog in case of no selection
     */
    void showNoCourseSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Course Selected");
        alert.setContentText("Please select a course in the table.");
        alert.showAndWait();
    }
}
