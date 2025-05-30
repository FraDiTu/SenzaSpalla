package com.saslab.ui;

import com.saslab.controller.UserController;
import com.saslab.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller per la creazione di nuovi utenti
 */
public class CreateUserController {
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private TextField specializationField;
    @FXML private TextField departmentField;
    @FXML private TextField serviceRoleField;
    @FXML private Spinner<Integer> experienceSpinner;
    @FXML private Label specializationLabel;
    @FXML private Label departmentLabel;
    @FXML private Label serviceRoleLabel;
    @FXML private Label experienceLabel;
    @FXML private Button createButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;
    
    private UserController userController;
    
    @FXML
    public void initialize() {
        setupUserTypeComboBox();
        setupExperienceSpinner();
        errorLabel.setVisible(false);
        
        // Inizialmente nascondi tutti i campi specifici
        hideAllSpecificFields();
        
        // Aggiungi listener per il cambio di tipo utente
        userTypeComboBox.setOnAction(this::handleUserTypeChange);
    }
    
    private void setupUserTypeComboBox() {
        userTypeComboBox.getItems().addAll(
            "Organizzatore",
            "Chef", 
            "Cuoco",
            "Personale di Servizio"
        );
        userTypeComboBox.getSelectionModel().selectFirst();
    }
    
    private void setupExperienceSpinner() {
        experienceSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0));
    }
    
    private void hideAllSpecificFields() {
        specializationField.setVisible(false);
        specializationLabel.setVisible(false);
        departmentField.setVisible(false);
        departmentLabel.setVisible(false);
        serviceRoleField.setVisible(false);
        serviceRoleLabel.setVisible(false);
        experienceSpinner.setVisible(false);
        experienceLabel.setVisible(false);
    }
    
    @FXML
    private void handleUserTypeChange(ActionEvent event) {
        hideAllSpecificFields();
        
        String selectedType = userTypeComboBox.getSelectionModel().getSelectedItem();
        
        switch (selectedType) {
            case "Organizzatore":
                departmentField.setVisible(true);
                departmentLabel.setVisible(true);
                departmentField.setPromptText("es. Amministrazione, Vendite");
                break;
                
            case "Chef":
                specializationField.setVisible(true);
                specializationLabel.setVisible(true);
                experienceSpinner.setVisible(true);
                experienceLabel.setVisible(true);
                specializationField.setPromptText("es. Cucina Italiana, Pasticceria");
                break;
                
            case "Cuoco":
                experienceSpinner.setVisible(true);
                experienceLabel.setVisible(true);
                break;
                
            case "Personale di Servizio":
                serviceRoleField.setVisible(true);
                serviceRoleLabel.setVisible(true);
                experienceSpinner.setVisible(true);
                experienceLabel.setVisible(true);
                serviceRoleField.setPromptText("es. Cameriere, Sommelier, Lavapiatti");
                break;
        }
    }
    
    @FXML
    private void handleCreateUser(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String userType = userTypeComboBox.getSelectionModel().getSelectedItem();
            
            String userId = null;
            
            switch (userType) {
                case "Organizzatore":
                    String department = departmentField.getText().trim();
                    userId = userController.createOrganizer(name, email, password, 
                                                          department.isEmpty() ? "Generale" : department);
                    break;
                    
                case "Chef":
                    String specialization = specializationField.getText().trim();
                    int chefExperience = experienceSpinner.getValue();
                    userId = userController.createChef(name, email, password, 
                                                     specialization.isEmpty() ? "Generale" : specialization, 
                                                     chefExperience);
                    break;
                    
                case "Cuoco":
                    int cookExperience = experienceSpinner.getValue();
                    userId = userController.createCook(name, email, password, cookExperience);
                    break;
                    
                case "Personale di Servizio":
                    String serviceRole = serviceRoleField.getText().trim();
                    int serviceExperience = experienceSpinner.getValue();
                    userId = userController.createServiceStaff(name, email, password, 
                                                             serviceRole.isEmpty() ? "Generico" : serviceRole, 
                                                             serviceExperience);
                    break;
            }
            
            if (userId != null) {
                showSuccess("Utente creato con successo! ID: " + userId);
                clearForm();
            } else {
                showError("Errore nella creazione dell'utente");
            }
            
        } catch (Exception e) {
            showError("Errore: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        if (nameField.getText().trim().isEmpty()) {
            errors.append("Il nome è obbligatorio\n");
        }
        
        if (emailField.getText().trim().isEmpty()) {
            errors.append("L'email è obbligatoria\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("Formato email non valido\n");
        }
        
        if (passwordField.getText().isEmpty()) {
            errors.append("La password è obbligatoria\n");
        } else if (passwordField.getText().length() < 6) {
            errors.append("La password deve essere di almeno 6 caratteri\n");
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errors.append("Le password non coincidono\n");
        }
        
        if (userTypeComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.append("Selezionare un tipo di utente\n");
        }
        
        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setVisible(true);
    }
    
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        specializationField.clear();
        departmentField.clear();
        serviceRoleField.clear();
        experienceSpinner.getValueFactory().setValue(0);
        userTypeComboBox.getSelectionModel().selectFirst();
        handleUserTypeChange(null);
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    public void setUserController(UserController userController) {
        this.userController = userController;
    }
}