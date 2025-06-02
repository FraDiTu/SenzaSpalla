package com.saslab.ui;

import com.saslab.controller.UserController;
import com.saslab.model.User;
import com.saslab.service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller per la schermata di login
 */
public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private Label infoLabel;
    
    private final UserController userController = new UserController();
    private final SessionManager sessionManager = SessionManager.getInstance();
    
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        setupInfoLabel();
        
        // Imposta il comportamento di default per il tasto Enter
        passwordField.setOnAction(this::handleLogin);
    }
    
    private void setupInfoLabel() {
        StringBuilder info = new StringBuilder();
        info.append("Utenti di default:\n");
        info.append("Organizzatore: admin@catring.com / admin123\n");
        info.append("Chef: mario@catring.com / chef123\n");
        info.append("Cuoco: giuseppe@catring.com / cook123\n");
        info.append("Servizio: anna@catring.com / service123");
        
        infoLabel.setText(info.toString());
        infoLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showError("Inserire email e password");
            return;
        }
        
        try {
            User user = userController.authenticateUser(email, password);
            
            if (user != null) {
                // Crea sessione
                String sessionId = sessionManager.createSession(user);
                
                // Apri schermata principale
                openMainWindow(user, sessionId);
                
                // Chiudi finestra di login
                Stage loginStage = (Stage) loginButton.getScene().getWindow();
                loginStage.close();
                
            } else {
                showError("Email o password non corretti");
            }
            
        } catch (Exception e) {
            showError("Errore durante il login: " + e.getMessage());
        }
    }
    
    private void openMainWindow(User user, String sessionId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            MainController mainController = loader.getController();
            mainController.initializeWithUser(user, sessionId);
            
            Stage mainStage = new Stage();
            mainStage.setTitle("Cat & Ring - " + user.getName() + " (" + user.getRole().getDisplayName() + ")");
            mainStage.setScene(new Scene(root, 1000, 700));
            mainStage.setMaximized(true);
            mainStage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura dell'applicazione: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
    
    @FXML
    private void handleCreateUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateUserView.fxml"));
            Parent root = loader.load();
            
            CreateUserController controller = loader.getController();
            controller.setUserController(userController);
            
            Stage stage = new Stage();
            stage.setTitle("Crea Nuovo Utente");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura della finestra: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
}