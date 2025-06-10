package it.catering.catring;

import it.catering.catring.model.DataInitializer;
import it.catering.catring.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class CatRingApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Inizializza i dati di demo
        DataInitializer.initializeData();
        
        primaryStage.setTitle("Cat & Ring - Sistema Catering");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setResizable(true);
        
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}