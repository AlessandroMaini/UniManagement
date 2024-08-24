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

import java.util.HashMap;
import java.util.Map;

public class UniManagementApplication extends Application {

    private static EntityManagerFactory emf; // unique instance of EntityManagerFactory

    @Override
    public void start(Stage stage) throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put("hibernate.show_sql", "true");
        emf = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), props);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("general-overview-view.fxml"));
        Parent root = loader.load();

        GeneralOverviewController generalOverviewController = loader.getController();
        generalOverviewController.setEmf(emf);

        Scene scene = new Scene(root);
        stage.setTitle("Uni Management");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Closes all connections to the DB
     */
    @Override
    public void stop() {
        if (emf != null && emf.isOpen())
            emf.close();
    }

    public static void main(String[] args) {
        launch(args);
//        Map<String, String> props = new HashMap<>(); // for hibernate properties
//        props.put("hibernate.show_sql", "true");
//        props.put("hibernate.hbm2ddl.auto", "create");
//        emf = new HibernatePersistenceProvider()
//                .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), props);
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//
//
//            em.getTransaction().commit();
//        }
    }
}