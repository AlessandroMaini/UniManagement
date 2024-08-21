package com.example.unimanagement;

import com.example.unimanagement.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UniManagementApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("general-overview-view.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Uni Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
//        Map<String, String> props = new HashMap<>(); // for hibernate properties
//        props.put("hibernate.show_sql", "true");
//        props.put("hibernate.hbm2ddl.auto", "none"); // create recreate a new table for each exec -> NO REAL WORLD!
//        EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider()
//                .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), props);
//        try (EntityManager em = entityManagerFactory.createEntityManager()) {
//            em.getTransaction().begin();
//
//
//            em.getTransaction().commit();
//        }
    }
}