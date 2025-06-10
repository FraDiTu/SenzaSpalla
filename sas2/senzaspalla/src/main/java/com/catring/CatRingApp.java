package com.catring;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * CLASSE PRINCIPALE DELL'APPLICAZIONE
 * Avvia l'applicazione JavaFX che dimostra l'uso dei pattern:
 * - GRASP: Controller, Creator, Information Expert, Low Coupling, High Cohesion
 * - GoF: Singleton, Observer
 */
public class CatRingApp extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        // Carica il file FXML con il controller che implementa i pattern
        FXMLLoader fxmlLoader = new FXMLLoader(CatRingApp.class.getResource("/fxml/menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        
        stage.setTitle("Cat & Ring - Dimostrazione Pattern GRASP e GoF");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        
        // Messaggio informativo sui pattern utilizzati
        System.out.println("=== CAT & RING - PATTERN UTILIZZATI ===");
        System.out.println("PATTERN GRASP:");
        System.out.println("  - Controller: MenuController gestisce l'interfaccia");
        System.out.println("  - Creator: MenuCreator crea tutti gli oggetti del dominio");
        System.out.println("  - Information Expert: IdGenerator e MenuValidator hanno le competenze specifiche");
        System.out.println("  - Low Coupling & High Cohesion: Separazione chiara dei ruoli");
        System.out.println();
        System.out.println("PATTERN GOF:");
        System.out.println("  - Singleton: MenuService ha una sola istanza globale");
        System.out.println("  - Observer: MenuObserver notifica le modifiche ai menu");
        System.out.println("==========================================");
    }

    public static void main(String[] args) {
        launch();
    }
}