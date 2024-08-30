package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Teacher;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Teachers list controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-24
 */
public class TeacherTabController {

    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> teacherFirstNameColumn;
    @FXML private TableColumn<Teacher, String> teacherLastNameColumn;
    @FXML private TableColumn<Teacher, String> teacherResidenceColumn;
    @FXML private TableColumn<Teacher, LocalDate> teacherBirthdayColumn;

    ObservableList<Teacher> teacherObservableList = FXCollections.observableArrayList();
    CourseTabController courseController; // reference necessary to update the course-teacher column
    EntityManagerFactory emf;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        teacherTable.setItems(teacherObservableList);
        teacherFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        teacherLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        teacherResidenceColumn.setCellValueFactory(new PropertyValueFactory<>("residence"));
        teacherBirthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        teacherTable.setRowFactory(tv -> {
            TableRow<Teacher> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    Teacher teacher = row.getItem();
                    switchToTeacherOverview(teacher);
                }
            });
            return row ;
        });
    }

    /**
     * Switches the scene to the teacher overview.
     * @param teacher the teacher to overview
     */
    @FXML
    private void switchToTeacherOverview(Teacher teacher) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("teacher-overview-view.fxml"));
            Parent root = loader.load();

            TeacherOverviewController controller = loader.getController();
            controller.setEmf(emf);
            controller.setTeacher(teacher);

            Stage stage = (Stage) teacherTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        fillTeacherTable();
    }

    public void setCourseController(CourseTabController courseController) {
        this.courseController = courseController;
    }

    /**
     * Fills the teachers table with the data from the DB.
     */
    @FXML
    private void fillTeacherTable() {
        teacherObservableList.addAll(getTeacherData());
    }

    private List<Teacher> getTeacherData() {
        List<Teacher> teacherObservableList;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = "SELECT t FROM Teacher t";
            TypedQuery<Teacher> q = em.createQuery(jpql, Teacher.class);
            teacherObservableList = q.getResultList();

            em.getTransaction().commit();
        }
        return teacherObservableList;
    }

    /**
     * Deletes the selected teacher from the DB.
     */
    @FXML
    void handleDelete() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex();
            em.getTransaction().begin();

            Teacher t = em.merge(teacherObservableList.get(selectedIndex));
            for (Course c : t.getCourseList())
                c.setTeacher(null);
            List<Course> coursesToUpdate = new ArrayList<>(t.getCourseList());

            em.remove(t);

            em.getTransaction().commit();
            courseController.updateCourseTable(coursesToUpdate);
            teacherObservableList.remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        }
    }

    /**
     * Updates the information about the selected teacher.
     */
    @FXML
    void handleEdit() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("teacher-edit-view.fxml"));
            DialogPane view = loader.load();
            TeacherEditDialogController controller = loader.getController();

            int selectedIndex = selectedIndex();
            controller.setTeacher(teacherObservableList.get(selectedIndex));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Teacher");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Teacher t = em.merge(teacherObservableList.get(selectedIndex));
                controller.updateTeacher(t);
                List<Course> coursesToUpdate = new ArrayList<>(t.getCourseList());

                em.getTransaction().commit();
                courseController.updateCourseTable(coursesToUpdate);
                teacherObservableList.set(selectedIndex, t);
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new teacher to the DB.
     */
    @FXML
    void handleNew() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("teacher-edit-view.fxml"));
            DialogPane view = loader.load();
            TeacherEditDialogController controller = loader.getController();

            Teacher newTeacher = new Teacher("First Name", "Last Name", "Residence", LocalDate.now());
            controller.setTeacher(newTeacher);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("New Teacher");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                controller.updateTeacher(newTeacher);
                em.persist(newTeacher);

                em.getTransaction().commit();
                teacherObservableList.add(newTeacher);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the index of the selected teacher in the TeacherTable.
     * @return the index of the selected teacher
     */
    int selectedIndex() {
        int selectedIndex = teacherTable.getSelectionModel().getSelectedIndex();
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
