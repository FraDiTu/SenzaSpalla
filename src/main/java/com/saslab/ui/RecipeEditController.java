package com.saslab.ui;

import com.saslab.controller.RecipeController;
import com.saslab.Recipe;
import com.saslab.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller per la creazione e modifica delle ricette
 * Implementa pattern Controller (GRASP) per gestire l'interfaccia utente
 */
public class RecipeEditController {
    
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private Spinner<Integer> preparationTimeSpinner;
    @FXML private Spinner<Integer> basePortionsSpinner;
    @FXML private TextField authorField;
    
    // Ingredienti
    @FXML private TextField ingredientNameField;
    @FXML private TextField ingredientQuantityField;
    @FXML private TextField ingredientUnitField;
    @FXML private TableView<Recipe.Ingredient> ingredientsTable;
    @FXML private TableColumn<Recipe.Ingredient, String> ingredientNameColumn;
    @FXML private TableColumn<Recipe.Ingredient, Double> ingredientQuantityColumn;
    @FXML private TableColumn<Recipe.Ingredient, String> ingredientUnitColumn;
    @FXML private Button addIngredientButton;
    @FXML private Button removeIngredientButton;
    
    // Tags
    @FXML private TextField tagField;
    @FXML private ListView<String> tagsList;
    @FXML private Button addTagButton;
    @FXML private Button removeTagButton;
    
    // Istruzioni
    @FXML private TextArea advanceInstructionsArea;
    @FXML private TextArea lastMinuteInstructionsArea;
    @FXML private TextField advanceInstructionField;
    @FXML private TextField lastMinuteInstructionField;
    @FXML private ListView<String> advanceInstructionsList;
    @FXML private ListView<String> lastMinuteInstructionsList;
    @FXML private Button addAdvanceInstructionButton;
    @FXML private Button addLastMinuteInstructionButton;
    @FXML private Button removeAdvanceInstructionButton;
    @FXML private Button removeLastMinuteInstructionButton;
    
    // Controlli
    @FXML private Button saveButton;
    @FXML private Button publishButton;
    @FXML private Button cancelButton;
    @FXML private Label statusLabel;
    @FXML private Label errorLabel;
    
    private RecipeController recipeController;
    private User currentUser;
    private MainController mainController;
    private Recipe currentRecipe;
    private boolean isEditMode = false;
    
    @FXML
    public void initialize() {
        setupSpinners();
        setupTables();
        setupLists();
        errorLabel.setVisible(false);
        statusLabel.setVisible(false);
    }
    
    private void setupSpinners() {
        preparationTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 600, 30));
        basePortionsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 4));
    }
    
    private void setupTables() {
        ingredientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingredientQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        ingredientUnitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        
        // Abilita selezione multipla per rimozione ingredienti
        ingredientsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    private void setupLists() {
        // Abilita selezione multipla per liste
        tagsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        advanceInstructionsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        lastMinuteInstructionsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    public void setRecipeController(RecipeController recipeController) {
        this.recipeController = recipeController;
    }
    
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void setRecipeForEditing(Recipe recipe) {
        this.currentRecipe = recipe;
        this.isEditMode = true;
        loadRecipeData();
        updateUIForEditMode();
    }
    
    private void loadRecipeData() {
        if (currentRecipe == null) return;
        
        nameField.setText(currentRecipe.getName());
        descriptionArea.setText(currentRecipe.getDescription());
        preparationTimeSpinner.getValueFactory().setValue(currentRecipe.getPreparationTime());
        basePortionsSpinner.getValueFactory().setValue(currentRecipe.getBasePortions());
        authorField.setText(currentRecipe.getAuthorId());
        
        // Carica ingredienti
        ObservableList<Recipe.Ingredient> ingredients = FXCollections.observableArrayList(currentRecipe.getIngredients());
        ingredientsTable.setItems(ingredients);
        
        // Carica tags
        ObservableList<String> tags = FXCollections.observableArrayList(currentRecipe.getTags());
        tagsList.setItems(tags);
        
        // Carica istruzioni
        ObservableList<String> advanceInstructions = FXCollections.observableArrayList(currentRecipe.getAdvanceInstructions());
        advanceInstructionsList.setItems(advanceInstructions);
        
        ObservableList<String> lastMinuteInstructions = FXCollections.observableArrayList(currentRecipe.getLastMinuteInstructions());
        lastMinuteInstructionsList.setItems(lastMinuteInstructions);
        
        updateStatusLabel();
    }
    
    private void updateUIForEditMode() {
        if (currentRecipe != null && !currentRecipe.canBeModified(currentUser.getId())) {
            // Disabilita tutti i controlli se la ricetta non può essere modificata
            setAllControlsEnabled(false);
            showError("La ricetta non può essere modificata nel suo stato attuale o non hai i permessi necessari");
        }
    }
    
    private void setAllControlsEnabled(boolean enabled) {
        nameField.setDisable(!enabled);
        descriptionArea.setDisable(!enabled);
        preparationTimeSpinner.setDisable(!enabled);
        basePortionsSpinner.setDisable(!enabled);
        authorField.setDisable(!enabled);
        
        ingredientNameField.setDisable(!enabled);
        ingredientQuantityField.setDisable(!enabled);
        ingredientUnitField.setDisable(!enabled);
        addIngredientButton.setDisable(!enabled);
        removeIngredientButton.setDisable(!enabled);
        
        tagField.setDisable(!enabled);
        addTagButton.setDisable(!enabled);
        removeTagButton.setDisable(!enabled);
        
        advanceInstructionField.setDisable(!enabled);
        lastMinuteInstructionField.setDisable(!enabled);
        addAdvanceInstructionButton.setDisable(!enabled);
        addLastMinuteInstructionButton.setDisable(!enabled);
        removeAdvanceInstructionButton.setDisable(!enabled);
        removeLastMinuteInstructionButton.setDisable(!enabled);
        
        saveButton.setDisable(!enabled);
        publishButton.setDisable(!enabled);
    }
    
    @FXML
    private void handleAddIngredient(ActionEvent event) {
        String name = ingredientNameField.getText().trim();
        String quantityText = ingredientQuantityField.getText().trim();
        String unit = ingredientUnitField.getText().trim();
        
        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            showError("Tutti i campi ingrediente sono obbligatori");
            return;
        }
        
        try {
            double quantity = Double.parseDouble(quantityText);
            if (quantity <= 0) {
                showError("La quantità deve essere positiva");
                return;
            }
            
            if (isEditMode && currentRecipe != null) {
                currentRecipe.addIngredient(name, quantity, unit);
                loadRecipeData(); // Ricarica per aggiornare la vista
            } else {
                // Modalità creazione - aggiungi alla tabella locale
                Recipe.Ingredient ingredient = new Recipe.Ingredient(name, quantity, unit);
                ingredientsTable.getItems().add(ingredient);
            }
            
            // Pulisci i campi
            ingredientNameField.clear();
            ingredientQuantityField.clear();
            ingredientUnitField.clear();
            
            clearError();
            
        } catch (NumberFormatException e) {
            showError("Inserire un numero valido per la quantità");
        } catch (Exception e) {
            showError("Errore nell'aggiunta dell'ingrediente: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRemoveIngredient(ActionEvent event) {
        ObservableList<Recipe.Ingredient> selectedItems = ingredientsTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            showError("Seleziona almeno un ingrediente da rimuovere");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setHeaderText("Rimuovere gli ingredienti selezionati?");
        alert.setContentText("Questa operazione non può essere annullata.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // In modalità creazione, rimuovi solo dalla tabella
            if (!isEditMode) {
                ingredientsTable.getItems().removeAll(selectedItems);
            } else {
                // In modalità modifica, qui dovremmo implementare la rimozione dalla ricetta
                // Per ora mostriamo un messaggio
                showInfo("Funzionalità di rimozione ingredienti da implementare per ricette esistenti");
            }
        }
    }
    
    @FXML
    private void handleAddTag(ActionEvent event) {
        String tag = tagField.getText().trim();
        if (tag.isEmpty()) {
            showError("Inserire un tag");
            return;
        }
        
        try {
            if (isEditMode && currentRecipe != null) {
                currentRecipe.addTag(tag);
                loadRecipeData();
            } else {
                // Modalità creazione
                if (!tagsList.getItems().contains(tag)) {
                    tagsList.getItems().add(tag);
                }
            }
            
            tagField.clear();
            clearError();
            
        } catch (Exception e) {
            showError("Errore nell'aggiunta del tag: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRemoveTag(ActionEvent event) {
        ObservableList<String> selectedTags = tagsList.getSelectionModel().getSelectedItems();
        if (selectedTags.isEmpty()) {
            showError("Seleziona almeno un tag da rimuovere");
            return;
        }
        
        tagsList.getItems().removeAll(selectedTags);
    }
    
    @FXML
    private void handleAddAdvanceInstruction(ActionEvent event) {
        String instruction = advanceInstructionField.getText().trim();
        if (instruction.isEmpty()) {
            showError("Inserire un'istruzione di preparazione in anticipo");
            return;
        }
        
        try {
            if (isEditMode && currentRecipe != null) {
                currentRecipe.addAdvanceInstruction(instruction);
                loadRecipeData();
            } else {
                advanceInstructionsList.getItems().add(instruction);
            }
            
            advanceInstructionField.clear();
            clearError();
            
        } catch (Exception e) {
            showError("Errore nell'aggiunta dell'istruzione: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAddLastMinuteInstruction(ActionEvent event) {
        String instruction = lastMinuteInstructionField.getText().trim();
        if (instruction.isEmpty()) {
            showError("Inserire un'istruzione dell'ultimo minuto");
            return;
        }
        
        try {
            if (isEditMode && currentRecipe != null) {
                currentRecipe.addLastMinuteInstruction(instruction);
                loadRecipeData();
            } else {
                lastMinuteInstructionsList.getItems().add(instruction);
            }
            
            lastMinuteInstructionField.clear();
            clearError();
            
        } catch (Exception e) {
            showError("Errore nell'aggiunta dell'istruzione: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRemoveAdvanceInstruction(ActionEvent event) {
        String selectedInstruction = advanceInstructionsList.getSelectionModel().getSelectedItem();
        if (selectedInstruction == null) {
            showError("Seleziona un'istruzione da rimuovere");
            return;
        }
        
        advanceInstructionsList.getItems().remove(selectedInstruction);
    }
    
    @FXML
    private void handleRemoveLastMinuteInstruction(ActionEvent event) {
        String selectedInstruction = lastMinuteInstructionsList.getSelectionModel().getSelectedItem();
        if (selectedInstruction == null) {
            showError("Seleziona un'istruzione da rimuovere");
            return;
        }
        
        lastMinuteInstructionsList.getItems().remove(selectedInstruction);
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        try {
            if (isEditMode) {
                // Aggiorna ricetta esistente
                updateCurrentRecipe();
                showSuccess("Ricetta aggiornata con successo");
            } else {
                // Crea nuova ricetta
                String recipeId = createNewRecipe();
                showSuccess("Ricetta creata con successo! ID: " + recipeId);
            }
            
            updateStatusLabel();
            
            if (mainController != null) {
                mainController.refreshAllData();
            }
            
        } catch (Exception e) {
            showError("Errore nel salvataggio: " + e.getMessage());
        }
    }
    
    @FXML
    private void handlePublish(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        if (ingredientsTable.getItems().isEmpty()) {
            showError("La ricetta deve avere almeno un ingrediente per essere pubblicata");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma pubblicazione");
        alert.setHeaderText("Pubblicare la ricetta?");
        alert.setContentText("Una volta pubblicata, la ricetta non potrà più essere modificata.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (isEditMode && currentRecipe != null) {
                    updateCurrentRecipe();
                    recipeController.publishRecipe(currentRecipe.getId());
                } else {
                    String recipeId = createNewRecipe();
                    recipeController.publishRecipe(recipeId);
                }
                
                showSuccess("Ricetta pubblicata con successo!");
                updateStatusLabel();
                setAllControlsEnabled(false);
                
                if (mainController != null) {
                    mainController.refreshAllData();
                }
                
            } catch (Exception e) {
                showError("Errore nella pubblicazione: " + e.getMessage());
            }
        }
    }
    
    private String createNewRecipe() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        int preparationTime = preparationTimeSpinner.getValue();
        
        String recipeId = recipeController.createRecipe(currentUser.getId(), name, description, preparationTime);
        Recipe recipe = recipeController.getRecipe(recipeId);
        
        // Imposta porzioni base
        recipe.setBasePortions(basePortionsSpinner.getValue());
        
        // Imposta autore se specificato
        String author = authorField.getText().trim();
        if (!author.isEmpty()) {
            recipe.setAuthorId(author);
        }
        
        // Aggiungi ingredienti
        for (Recipe.Ingredient ingredient : ingredientsTable.getItems()) {
            recipe.addIngredient(ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());
        }
        
        // Aggiungi tags
        for (String tag : tagsList.getItems()) {
            recipe.addTag(tag);
        }
        
        // Aggiungi istruzioni
        for (String instruction : advanceInstructionsList.getItems()) {
            recipe.addAdvanceInstruction(instruction);
        }
        
        for (String instruction : lastMinuteInstructionsList.getItems()) {
            recipe.addLastMinuteInstruction(instruction);
        }
        
        // Imposta come ricetta corrente e modalità modifica
        this.currentRecipe = recipe;
        this.isEditMode = true;
        
        return recipeId;
    }
    
    private void updateCurrentRecipe() {
        if (currentRecipe == null) return;
        
        // Aggiorna solo i campi che possono essere modificati
        if (currentRecipe.canBeModified(currentUser.getId())) {
            currentRecipe.setDescription(descriptionArea.getText().trim());
            currentRecipe.setBasePortions(basePortionsSpinner.getValue());
            
            String author = authorField.getText().trim();
            if (!author.isEmpty()) {
                currentRecipe.setAuthorId(author);
            }
        }
    }
    
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        if (nameField.getText().trim().isEmpty()) {
            errors.append("Il nome della ricetta è obbligatorio\n");
        }
        
        if (preparationTimeSpinner.getValue() <= 0) {
            errors.append("Il tempo di preparazione deve essere positivo\n");
        }
        
        if (basePortionsSpinner.getValue() <= 0) {
            errors.append("Il numero di porzioni base deve essere positivo\n");
        }
        
        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        
        return true;
    }
    
    private void updateStatusLabel() {
        if (currentRecipe != null) {
            statusLabel.setText("Stato: " + currentRecipe.getState().name());
            statusLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
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
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazione");
        alert.setHeaderText("Informazione");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void clearError() {
        errorLabel.setVisible(false);
    }
}