package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Courses list controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-24
 */
public class CourseTabController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseTeacherColumn;

    ObservableList<Course> courseObservableList = FXCollections.observableArrayList();
    EntityManagerFactory emf;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        courseTable.setItems(courseObservableList);
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseTeacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        courseTable.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    Course course = row.getItem();
                    switchToCourseOverview(course);
                }
            });
            return row ;
        });
    }

    /**
     * Switches the scene to the course overview.
     * @param course the course to overview
     */
    @FXML
    private void switchToCourseOverview(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("course-overview-view.fxml"));
            Parent root = loader.load();

            CourseOverviewController controller = loader.getController();
            controller.setEmf(emf);
            controller.setCourse(course);

            Stage stage = (Stage) courseTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        fillCourseTable();
    }

    /**
     * Updates the course-teacher column after a modification of teachers DB.
     * @param coursesToUpdate the list of courses to update
     */
    public void updateCourseTable(List<Course> coursesToUpdate) {
        for (Course c : courseObservableList)
            for (Course u : coursesToUpdate)
                if (c.getId() == u.getId())
                    courseObservableList.set(courseObservableList.indexOf(c), u);
    }

    /**
     * Fills the courses table with the data from the DB.
     */
    @FXML
    private void fillCourseTable() {
        courseObservableList.addAll(getCourseData());
    }

    private List<Course> getCourseData() {
        List<Course> courseObservableList = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = "SELECT c FROM Course c";
            TypedQuery<Course> q = em.createQuery(jpql, Course.class);
            courseObservableList = q.getResultList();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseObservableList;
    }

    /**
     * Deletes the selected course from the DB.
     */
    @FXML
    void handleDelete() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex();
            em.getTransaction().begin();

            Course c = em.merge(courseObservableList.get(selectedIndex));
            em.remove(c);

            em.getTransaction().commit();
            courseObservableList.remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the information about the selected course.
     */
    @FXML
    void handleEdit() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("course-edit-view.fxml"));
            DialogPane view = loader.load();
            CourseEditDialogController controller = loader.getController();

            int selectedIndex = selectedIndex();
            controller.setCourse(courseObservableList.get(selectedIndex));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Course");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Course c = em.merge(courseObservableList.get(selectedIndex));
                controller.updateCourse(c);

                em.getTransaction().commit();
                courseObservableList.set(selectedIndex, c);
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new course to the DB.
     */
    @FXML
    void handleNew() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("course-edit-view.fxml"));
            DialogPane view = loader.load();
            CourseEditDialogController controller = loader.getController();

            Course newCourse = new Course("Course Name");
            controller.setCourse(newCourse);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("New Course");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                controller.updateCourse(newCourse);
                em.persist(newCourse);

                em.getTransaction().commit();
                courseObservableList.add(newCourse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the index of the selected course in the CourseTable.
     * @return the index of the selected course
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
    void showNoPersonSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("Nothing Selected");
        alert.setContentText("Please select something in the table.");
        alert.showAndWait();
    }
}
