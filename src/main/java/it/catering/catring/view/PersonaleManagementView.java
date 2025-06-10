package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.PersonaleController;
import it.catering.catring.model.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class PersonaleManagementView {
    private PersonaleController personaleController;
    private VBox mainContent;
    private TableView<User> personaleTable;
    
    public PersonaleManagementView() {
        this.personaleController = new PersonaleController();
        this.personaleController.setCurrentUser(
            ApplicationController.getInstance().getAuthController().getCurrentUser());
        createView();
        loadPersonale();
    }
    
    public ScrollPane getView() {
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }
    
    private void createView() {
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: white;");
        
        // Header
        HBox header = createHeader();
        
        // Table
        personaleTable = createPersonaleTable();
        
        // Action buttons
        HBox actionButtons = createActionButtons();
        
        mainContent.getChildren().addAll(header, new Separator(), personaleTable, actionButtons);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Gestione Personale");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> loadPersonale());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, refreshButton);
        return header;
    }
    
    private TableView<User> createPersonaleTable() {
        TableView<User> table = new TableView<>();
        table.setPrefHeight(400);
        
        TableColumn<User, String> tipoCol = new TableColumn<>("Ruolo");
        tipoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getClass().getSimpleName()));
        tipoCol.setPrefWidth(120);
        
        TableColumn<User, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));
        nomeCol.setPrefWidth(150);
        
        TableColumn<User, String> cognomeCol = new TableColumn<>("Cognome");
        cognomeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getCognome()));
        cognomeCol.setPrefWidth(150);
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        usernameCol.setPrefWidth(120);
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        emailCol.setPrefWidth(200);
        
        TableColumn<User, String> dettagliCol = new TableColumn<>("Dettagli");
        dettagliCol.setCellValueFactory(data -> {
            User user = data.getValue();
            String dettagli = "";
            if (user instanceof Chef chef) {
                dettagli = "Specializzazione: " + chef.getSpecializzazione();
            } else if (user instanceof Cuoco cuoco) {
                dettagli = "Esperienza: " + cuoco.getAnniEsperienza() + " anni";
            } else if (user instanceof PersonaleServizio personale) {
                dettagli = "Ruolo: " + personale.getRuoloSpecifico();
            }
            return new javafx.beans.property.SimpleStringProperty(dettagli);
        });
        dettagliCol.setPrefWidth(200);
        
        table.getColumns().addAll(tipoCol, nomeCol, cognomeCol, usernameCol, emailCol, dettagliCol);
        
        return table;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button addButton = new Button("Aggiungi Personale");
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        addButton.setOnAction(e -> showAddPersonaleDialog());
        
        buttonBox.getChildren().add(addButton);
        return buttonBox;
    }
    
    private void showAddPersonaleDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Personale");
        dialog.setHeaderText("Crea un nuovo membro del personale");
        
        ButtonType createButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.setItems(FXCollections.observableArrayList("chef", "cuoco", "organizzatore", "personaleServizio"));
        tipoCombo.setValue("cuoco");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome");
        
        TextField cognomeField = new TextField();
        cognomeField.setPromptText("Cognome");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        TextField extraField = new TextField();
        extraField.setPromptText("Info aggiuntive");
        
        Label extraLabel = new Label("Specializzazione/Esperienza/Ruolo:");
        
        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(tipoCombo, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Nome:"), 0, 3);
        grid.add(nomeField, 1, 3);
        grid.add(new Label("Cognome:"), 0, 4);
        grid.add(cognomeField, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);
        grid.add(extraLabel, 0, 6);
        grid.add(extraField, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String tipo = tipoCombo.getValue();
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    String nome = nomeField.getText();
                    String cognome = cognomeField.getText();
                    String email = emailField.getText();
                    String extra = extraField.getText();
                    
                    if (username.isEmpty() || password.isEmpty() || nome.isEmpty() || cognome.isEmpty()) {
                        showAlert("Errore", "Compilare tutti i campi obbligatori");
                        return null;
                    }
                    
                    return personaleController.createPersonale(tipo, username, password, 
                                                             nome, cognome, email, extra);
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(user -> {
            if (user != null) {
                loadPersonale();
                showAlert("Successo", "Personale aggiunto con successo!");
            }
        });
    }
    
    private void loadPersonale() {
        try {
            List<User> personale = personaleController.getAllPersonale();
            ObservableList<User> personaleList = FXCollections.observableArrayList(personale);
            personaleTable.setItems(personaleList);
        } catch (Exception e) {
            showAlert("Errore", "Impossibile caricare il personale: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (title.equals("Errore")) {
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}