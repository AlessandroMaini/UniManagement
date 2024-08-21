package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Student;
import com.example.unimanagement.entities.Teacher;
import com.example.unimanagement.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class GeneralOverviewController {

    @FXML private TabPane tabPane;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> studentSerialColumn;
    @FXML private TableColumn<Student, String> studentFirstNameColumn;
    @FXML private TableColumn<Student, String> studentLastNameColumn;
    @FXML private TableColumn<Student, String> studentResidenceColumn;
    @FXML private TableColumn<Student, LocalDate> studentBirthdayColumn;

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseTeacherColumn;

    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> teacherFirstNameColumn;
    @FXML private TableColumn<Teacher, String> teacherLastNameColumn;
    @FXML private TableColumn<Teacher, String> teacherResidenceColumn;
    @FXML private TableColumn<Teacher, LocalDate> teacherBirthdayColumn;

    /**
     * Creates a persistence context
     */
    static Map<String, String> props = new HashMap<>();
    static {
        props.put("hibernate.show_sql", "true");
    }
    EntityManagerFactory emf = new HibernatePersistenceProvider()
            .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), props);
    EntityManager em = emf.createEntityManager();

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {

        initializeStudents();
        initializeTeachers();
        initializeCourses();
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

        studentTable.setItems(getStudentData());
    }

    private ObservableList<Student> getStudentData() {
        em.getTransaction().begin();

        String jpql = "SELECT s FROM Student s";
        TypedQuery<Student> q = em.createQuery(jpql, Student.class);
        ObservableList<Student> studentObservableList = FXCollections.observableList(q.getResultList());

        em.getTransaction().commit(); // ends the transaction

        return studentObservableList;
    }

    /**
     * Initializes the courses TableView
     */
    public void initializeCourses() {

        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseTeacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        courseTable.setItems(getCourseData());
    }

    private ObservableList<Course> getCourseData() {
        em.getTransaction().begin();

        String jpql = "SELECT c FROM Course c";
        TypedQuery<Course> q = em.createQuery(jpql, Course.class);
        ObservableList<Course> courseObservableList = FXCollections.observableList(q.getResultList());

        em.getTransaction().commit();

        return courseObservableList;
    }

    /**
     * Initializes the teachers TableView
     */
    public void initializeTeachers() {

        teacherFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        teacherLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        teacherResidenceColumn.setCellValueFactory(new PropertyValueFactory<>("residence"));
        teacherBirthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        teacherTable.setItems(getTeacherData());
    }

    private ObservableList<Teacher> getTeacherData() {
        em.getTransaction().begin();

        String jpql = "SELECT t FROM Teacher t";
        TypedQuery<Teacher> q = em.createQuery(jpql, Teacher.class);
        ObservableList<Teacher> teacherObservableList = FXCollections.observableList(q.getResultList());

        em.getTransaction().commit();

        return teacherObservableList;
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
        try {
            int selectedIndex = selectedIndex(teacherTable);
            em.getTransaction().begin();

            Teacher t = teacherTable.getItems().get(selectedIndex);
            for (Course c : t.getCourseList())
                c.setTeacher(null);
            em.remove(t);

            em.getTransaction().commit();
            courseTable.refresh();
            teacherTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        }
    }

    /**
     * Deletes the selected course from the DB
     */
    private void handleDeleteCourse() {
        try {
            int selectedIndex = selectedIndex(courseTable);
            em.getTransaction().begin();

            Course c = courseTable.getItems().get(selectedIndex);
            em.remove(c);

            em.getTransaction().commit();
            courseTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        }
    }

    /**
     * Deletes the selected student from the DB
     */
    private void handleDeleteStudent() {
        try {
            int selectedIndex = selectedIndex(studentTable);
            em.getTransaction().begin();

            Student s = studentTable.getItems().get(selectedIndex);
            em.remove(s);

            em.getTransaction().commit();
            studentTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
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
        try {
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

                Teacher t = teacherTable.getItems().get(selectedIndex);
                controller.updateTeacher(t);

                em.getTransaction().commit();
                courseTable.refresh();
                teacherTable.refresh();
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the information about the selected course
     */
    private void handleEditCourse() {
        try {
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

                Course c = courseTable.getItems().get(selectedIndex);
                controller.updateCourse(c);

                em.getTransaction().commit();
                courseTable.refresh();
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the information about the selected student
     */
    private void handleEditStudent() {
        try {
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

                Student s = studentTable.getItems().get(selectedIndex);
                controller.updateStudent(s);

                em.getTransaction().commit();
                studentTable.refresh();
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (InvalidAttributeValueException e) {
            showInvalidAttributeValueAlert();
        } catch (IOException e) {
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
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new course to the DB
     */
    private void handleNewCourse() {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new student to the DB
     */
    private void handleNewStudent() {
        try {
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
        } catch (InvalidAttributeValueException | EntityExistsException e) {
            showInvalidAttributeValueAlert();
            em.getTransaction().commit();
        } catch (IOException e) {
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
