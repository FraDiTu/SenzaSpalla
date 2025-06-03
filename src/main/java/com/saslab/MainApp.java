package com.saslab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Classe principale dell'applicazione Cat & Ring
 * CORRETTA: Aggiunge gestione errori, verifica inizializzazione e fallback
 */
public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // NUOVO: Verifica inizializzazione sistema
            initializeSystem();
            
            // Carica la schermata di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("Cat & Ring - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            
            // NUOVO: Gestione migliorata chiusura applicazione
            primaryStage.setOnCloseRequest(event -> {
                performCleanup();
                Platform.exit();
                System.exit(0);
            });
            
            primaryStage.show();
            
        } catch (Exception e) {
            showErrorAndExit("Errore durante l'avvio dell'applicazione", e);
        }
    }
    
    /**
     * NUOVO: Inizializza i componenti del sistema
     */
    private void initializeSystem() {
        try {
            // Inizializza RecipeBook (Singleton)
            RecipeBook recipeBook = RecipeBook.getInstance();
            System.out.println("Sistema inizializzato - RecipeBook versione: " + recipeBook.getVersion());
            
            // Verifica presenza risorse critiche
            verifyResources();
            
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'inizializzazione del sistema: " + e.getMessage(), e);
        }
    }
    
    /**
     * NUOVO: Verifica presenza delle risorse necessarie
     */
    private void verifyResources() {
        // Verifica presenza file FXML critici
        String[] requiredFxml = {
            "/fxml/LoginView.fxml",
            "/fxml/MainView.fxml"
        };
        
        for (String fxmlPath : requiredFxml) {
            if (getClass().getResource(fxmlPath) == null) {
                throw new RuntimeException("File FXML mancante: " + fxmlPath);
            }
        }
        
        System.out.println("Verifica risorse completata con successo");
    }
    
    /**
     * NUOVO: Gestisce la pulizia dei dati prima della chiusura
     */
    private void performCleanup() {
        try {
            // Pulizia sessioni attive
            com.saslab.service.SessionManager.getInstance().cleanupExpiredSessions();
            
            // Pulizia notifiche vecchie
            com.saslab.service.NotificationService.getInstance().cleanOldNotifications();
            
            System.out.println("Pulizia sistema completata");
            
        } catch (Exception e) {
            System.err.println("Errore durante la pulizia: " + e.getMessage());
        }
    }
    
    /**
     * NUOVO: Mostra errore critico e termina l'applicazione
     */
    private void showErrorAndExit(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore Critico");
            alert.setHeaderText(message);
            alert.setContentText("L'applicazione verrà chiusa.\n\nDettagli: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception alertException) {
            // Se anche l'alert fallisce, stampa solo su console
            System.err.println("Impossibile mostrare alert di errore: " + alertException.getMessage());
        }
        
        Platform.exit();
        System.exit(1);
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Errore fatale durante l'avvio: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}