package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Student;
import com.example.unimanagement.entities.Teacher;
import com.example.unimanagement.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

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
            System.out.println(selectedIndex);
            em.getTransaction().begin();

            Teacher t = em.find(Teacher.class, teacherTable.getItems().get(selectedIndex).getId());
            for (Course c : t.getCourseList()) {
                c.setTeacher(null);
                courseTable.refresh();
            }
            em.remove(t);

            em.getTransaction().commit();
            teacherTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        }
    }

    private void handleDeleteCourse() {
    }

    private void handleDeleteStudent() {
    }

    @FXML
    public void handleEdit() {

    }

    @FXML
    public void handleNew() {

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
     * Shows a simple warning dialog
     */
    void showNoPersonSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("Nothing Selected");
        alert.setContentText("Please select something in the table.");
        alert.showAndWait();
    }
}
