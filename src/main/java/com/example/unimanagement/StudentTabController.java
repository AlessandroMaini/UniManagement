package com.example.unimanagement;

import com.example.unimanagement.entities.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
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

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Students list controller.
 *
 * @author Alessandro Maini
 * @version 2024-08-24
 */
public class StudentTabController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> studentSerialColumn;
    @FXML private TableColumn<Student, String> studentFirstNameColumn;
    @FXML private TableColumn<Student, String> studentLastNameColumn;
    @FXML private TableColumn<Student, String> studentResidenceColumn;
    @FXML private TableColumn<Student, LocalDate> studentBirthdayColumn;

    ObservableList<Student> studentObservableList = FXCollections.observableArrayList();
    EntityManagerFactory emf;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        studentTable.setItems(studentObservableList);
        studentSerialColumn.setCellValueFactory(new PropertyValueFactory<>("serial"));
        studentFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        studentLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        studentResidenceColumn.setCellValueFactory(new PropertyValueFactory<>("residence"));
        studentBirthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    Student student = row.getItem();
                    switchToStudentOverview(student);
                }
            });
            return row ;
        });
    }

    /**
     * Switches the scene to the student overview.
     * @param student the student to overview
     */
    @FXML
    private void switchToStudentOverview(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("student-overview-view.fxml"));
            Parent root = loader.load();

            StudentOverviewController controller = loader.getController();
            controller.setEmf(emf);
            controller.setStudent(student);

            Stage stage = (Stage) studentTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        fillStudentTable();
    }

    /**
     * Fills the students table with the data from the DB.
     */
    @FXML
    private void fillStudentTable() {
        studentObservableList.addAll(getStudentData());
    }

    private List<Student> getStudentData() {
        List<Student> studentObservableList = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = "SELECT s FROM Student s";
            TypedQuery<Student> q = em.createQuery(jpql, Student.class);
            studentObservableList = q.getResultList();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentObservableList;
    }

    /**
     * Deletes the selected student from the DB.
     */
    @FXML
    void handleDelete() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex();
            em.getTransaction().begin();

            Student s = em.merge(studentObservableList.get(selectedIndex));
            em.remove(s);

            em.getTransaction().commit();
            studentObservableList.remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the information about the selected student.
     */
    @FXML
    void handleEdit() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("student-edit-view.fxml"));
            DialogPane view = loader.load();
            StudentEditDialogController controller = loader.getController();
            controller.initializeEdit();

            int selectedIndex = selectedIndex();
            controller.setStudent(studentObservableList.get(selectedIndex));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Student");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Student s = em.merge(studentObservableList.get(selectedIndex));
                controller.updateStudent(s);

                em.getTransaction().commit();
                studentObservableList.set(selectedIndex, s);
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (InvalidAttributeValueException e) {
            showInvalidAttributeValueAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new student to the DB.
     */
    @FXML
    void handleNew() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("student-edit-view.fxml"));
            DialogPane view = loader.load();
            StudentEditDialogController controller = loader.getController();
            controller.initializeNew();

            Student newStudent = new Student("First Name", "Last Name", "Residence", LocalDate.now());
            controller.setStudent(newStudent);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("New Student");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                controller.updateStudent(newStudent);
                em.persist(newStudent);

                em.getTransaction().commit();
                studentObservableList.add(newStudent);
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (InvalidAttributeValueException | PersistenceException e) {
            showInvalidAttributeValueAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the index of the selected student in the StudentTable.
     * @return the index of the selected student
     */
    int selectedIndex() {
        int selectedIndex = studentTable.getSelectionModel().getSelectedIndex();
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

    /**
     * Shows a simple warning dialog in case of wrong attributes.
     */
    void showInvalidAttributeValueAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Serial");
        alert.setHeaderText("Invalid Serial Number");
        alert.setContentText("Please select a new 6 digits serial number.");
        alert.showAndWait();
    }
}

