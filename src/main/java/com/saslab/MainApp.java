package com.saslab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale dell'applicazione Cat & Ring
 */
public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica la schermata di login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Cat & Ring - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        
        // Gestisci chiusura applicazione
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}