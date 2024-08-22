package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Teacher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class TeacherAddCourseDialogController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseNameColumn;

    @FXML
    public void initialize() {
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    @FXML
    public void setCourseList(List<Course> courses) {
        courseTable.setItems(FXCollections.observableList(courses));
    }

    public Course getNewCourse() {
        int selectedIndex = courseTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0)
            throw new NoSuchElementException();
        else
            return courseTable.getItems().get(selectedIndex);
    }
}
