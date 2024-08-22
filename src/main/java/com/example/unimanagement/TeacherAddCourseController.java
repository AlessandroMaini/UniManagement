package com.example.unimanagement;

import com.example.unimanagement.entities.Course;
import com.example.unimanagement.entities.Teacher;
import javafx.fxml.FXML;

import javax.swing.text.html.ListView;
import java.util.List;

public class TeacherAddCourseController {

    @FXML private ListView courseList;

    @FXML
    public void initialize() {

    }

    public void setCourseList(List<Course> courses) {
    }
}
