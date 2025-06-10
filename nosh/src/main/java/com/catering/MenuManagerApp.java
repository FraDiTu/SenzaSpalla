package com.catering;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller pattern - coordina l'avvio dell'applicazione
 */
public class MenuManagerApp extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MenuManagerApp.class.getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        
        stage.setTitle("Sistema di Gestione Menu - Catering");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}