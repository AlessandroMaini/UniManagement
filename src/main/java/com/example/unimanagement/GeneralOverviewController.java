package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Student;
import com.example.unimanagement.entities.Teacher;
import jakarta.persistence.*;
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
import java.util.*;

public class GeneralOverviewController {

    @FXML private TabPane tabPane;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> studentSerialColumn;
    @FXML private TableColumn<Student, String> studentFirstNameColumn;
    @FXML private TableColumn<Student, String> studentLastNameColumn;
    @FXML private TableColumn<Student, String> studentResidenceColumn;
    @FXML private TableColumn<Student, LocalDate> studentBirthdayColumn;

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseTeacherColumn;

    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> teacherFirstNameColumn;
    @FXML private TableColumn<Teacher, String> teacherLastNameColumn;
    @FXML private TableColumn<Teacher, String> teacherResidenceColumn;
    @FXML private TableColumn<Teacher, LocalDate> teacherBirthdayColumn;

    EntityManagerFactory emf;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {

        initializeStudents();
        initializeTeachers();
        initializeCourses();
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        fillTables();
    }

    /**
     * Fills the students, courses and teachers tables with the data from the DB
     */
    @FXML
    private void fillTables() {
        studentTable.setItems(getStudentData());
        courseTable.setItems(getCourseData());
        teacherTable.setItems(getTeacherData());
    }

    private ObservableList<Student> getStudentData() {
        ObservableList<Student> studentObservableList = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = "SELECT s FROM Student s";
            TypedQuery<Student> q = em.createQuery(jpql, Student.class);
            studentObservableList = FXCollections.observableList(q.getResultList());

            em.getTransaction().commit(); // ends the transaction
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentObservableList;
    }

    private ObservableList<Course> getCourseData() {
        ObservableList<Course> courseObservableList = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = "SELECT c FROM Course c";
            TypedQuery<Course> q = em.createQuery(jpql, Course.class);
            courseObservableList = FXCollections.observableList(q.getResultList());

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseObservableList;
    }

    private ObservableList<Teacher> getTeacherData() {
        ObservableList<Teacher> teacherObservableList = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            String jpql = "SELECT t FROM Teacher t";
            TypedQuery<Teacher> q = em.createQuery(jpql, Teacher.class);
            teacherObservableList = FXCollections.observableList(q.getResultList());

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teacherObservableList;
    }

    /**
     * Initializes the students TableView
     */
    public void initializeStudents() {

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

    /**
     * Initializes the courses TableView
     */
    public void initializeCourses() {

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
     * Switches the scene to the course overview
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

    /**
     * Initializes the teachers TableView
     */
    public void initializeTeachers() {

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
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDelete() {
        switch (Objects.requireNonNull(getSelectedTab())) {
            case "Students":
                handleDeleteStudent();
                break;
            case "Courses":
                handleDeleteCourse();
                break;
            case "Teachers":
                handleDeleteTeacher();
                break;
        }
    }

    /**
     * Deletes the selected teacher from the DB
     */
    private void handleDeleteTeacher() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex(teacherTable);
            em.getTransaction().begin();

            Teacher t = em.merge(teacherTable.getItems().get(selectedIndex));
            for (Course c : t.getCourseList())
                c.setTeacher(null);
            em.remove(t);

            em.getTransaction().commit();
            courseTable.setItems(getCourseData());
            teacherTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the selected course from the DB
     */
    private void handleDeleteCourse() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex(courseTable);
            em.getTransaction().begin();

            Course c = em.merge(courseTable.getItems().get(selectedIndex));
            em.remove(c);

            em.getTransaction().commit();
            courseTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the selected student from the DB
     */
    private void handleDeleteStudent() {
        try (EntityManager em = emf.createEntityManager()) {
            int selectedIndex = selectedIndex(studentTable);
            em.getTransaction().begin();

            Student s = em.merge(studentTable.getItems().get(selectedIndex));
            em.remove(s);

            em.getTransaction().commit();
            studentTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEdit() {
        switch (Objects.requireNonNull(getSelectedTab())) {
            case "Students":
                handleEditStudent();
                break;
            case "Courses":
                handleEditCourse();
                break;
            case "Teachers":
                handleEditTeacher();
                break;
        }
    }

    /**
     * Updates the information about the selected teacher
     */
    private void handleEditTeacher() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("teacher-edit-view.fxml"));
            DialogPane view = loader.load();
            TeacherEditDialogController controller = loader.getController();

            int selectedIndex = selectedIndex(teacherTable);
            controller.setTeacher(teacherTable.getItems().get(selectedIndex));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Teacher");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Teacher t = em.merge(teacherTable.getItems().get(selectedIndex));
                controller.updateTeacher(t);

                em.getTransaction().commit();
                courseTable.setItems(getCourseData());
                teacherTable.setItems(getTeacherData());
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the information about the selected course
     */
    private void handleEditCourse() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("course-edit-view.fxml"));
            DialogPane view = loader.load();
            CourseEditDialogController controller = loader.getController();

            int selectedIndex = selectedIndex(courseTable);
            controller.setCourse(courseTable.getItems().get(selectedIndex));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Course");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Course c = em.merge(courseTable.getItems().get(selectedIndex));
                controller.updateCourse(c);

                em.getTransaction().commit();
                courseTable.setItems(getCourseData());
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the information about the selected student
     */
    private void handleEditStudent() {
        try (EntityManager em = emf.createEntityManager()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("student-edit-view.fxml"));
            DialogPane view = loader.load();
            StudentEditDialogController controller = loader.getController();
            controller.initializeEdit();

            int selectedIndex = selectedIndex(studentTable);
            controller.setStudent(studentTable.getItems().get(selectedIndex));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Student");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                em.getTransaction().begin();

                Student s = em.merge(studentTable.getItems().get(selectedIndex));
                controller.updateStudent(s);

                em.getTransaction().commit();
                studentTable.setItems(getStudentData());
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (InvalidAttributeValueException e) {
            showInvalidAttributeValueAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNew() {
        switch (Objects.requireNonNull(getSelectedTab())) {
            case "Students":
                handleNewStudent();
                break;
            case "Courses":
                handleNewCourse();
                break;
            case "Teachers":
                handleNewTeacher();
                break;
        }
    }

    /**
     * Adds a new teacher to the DB
     */
    private void handleNewTeacher() {
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
                teacherTable.getItems().add(newTeacher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new course to the DB
     */
    private void handleNewCourse() {
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
                courseTable.getItems().add(newCourse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new student to the DB
     */
    private void handleNewStudent() {
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
                studentTable.getItems().add(newStudent);
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
     * Returns the title of the selected tab
     * @return the title of the selected tab
     */
    private String getSelectedTab() {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            if (tab.isSelected())
                return tab.getText();
        }
        return null;
    }

    /**
     * Returns the index of the selected item in the TableView component
     * @param tableView the Table of the selection
     * @return the index of the selected item
     */
    int selectedIndex(TableView<?> tableView) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            throw new NoSuchElementException();
        }
        return selectedIndex;
    }

    /**
     * Shows a simple warning dialog in case of no selection
     */
    void showNoPersonSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("Nothing Selected");
        alert.setContentText("Please select something in the table.");
        alert.showAndWait();
    }

    /**
     * Shows a simple warning dialog in case of wrong attributes
     */
    void showInvalidAttributeValueAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Serial");
        alert.setHeaderText("Invalid Serial Number");
        alert.setContentText("Please select a new 6 digits serial number.");
        alert.showAndWait();
    }
}
