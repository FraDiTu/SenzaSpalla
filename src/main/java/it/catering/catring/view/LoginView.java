package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.AuthController;
import it.catering.catring.model.entities.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {
    private Stage stage;
    private AuthController authController;
    
    public LoginView(Stage stage) {
        this.stage = stage;
        this.authController = AuthController.getInstance();
    }
    
    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f0f8ff;");
        
        // Title
        Label titleLabel = new Label("Cat & Ring");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label subtitleLabel = new Label("Sistema di Gestione Catering");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        // Login Form
        VBox loginForm = createLoginForm();
        
        root.getChildren().addAll(titleLabel, subtitleLabel, loginForm);
        
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox createLoginForm() {
        VBox form = new VBox(15);
        form.setMaxWidth(300);
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        Label loginLabel = new Label("Accesso al Sistema");
        loginLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        Button loginButton = new Button("Accedi");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5;");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");
        
        // Info per utenti demo
        Label infoLabel = new Label("Utenti Demo:\nchef1/password (Chef)\ncuoco1/password (Cuoco)\norg1/password (Organizzatore)");
        infoLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            
            if (authController.login(username, password)) {
                ApplicationController.getInstance().updateCurrentUser();
                openMainView();
            } else {
                statusLabel.setText("Credenziali non valide");
            }
        });
        
        // Enter key handling
        passwordField.setOnAction(e -> loginButton.fire());
        
        form.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton, statusLabel, infoLabel);
        
        return form;
    }
    
    private void openMainView() {
        User currentUser = authController.getCurrentUser();
        MainView mainView = new MainView(stage, currentUser);
        mainView.show();
    }
}