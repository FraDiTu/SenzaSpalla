package com.saslab.ui;

import com.saslab.Recipe;
import com.saslab.RecipeBook;
import com.saslab.controller.RecipeController;
import com.saslab.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller per la visualizzazione del ricettario
 * Implementa pattern Controller (GRASP) per gestire l'interfaccia utente
 */
public class RecipeListController {
    
    @FXML private Label recipeCountLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> tagFilterComboBox;
    @FXML private ComboBox<String> stateFilterComboBox;
    @FXML private Button searchButton;
    @FXML private Button clearFiltersButton;
    
    @FXML private TableView<Recipe> recipesTable;
    @FXML private TableColumn<Recipe, String> nameColumn;
    @FXML private TableColumn<Recipe, String> ownerColumn;
    @FXML private TableColumn<Recipe, Integer> timeColumn;
    @FXML private TableColumn<Recipe, String> tagsColumn;
    @FXML private TableColumn<Recipe, Recipe.RecipeState> stateColumn;
    
    @FXML private TitledPane detailsPane;
    @FXML private Label descriptionLabel;
    @FXML private Label portionsLabel;
    @FXML private ListView<String> ingredientsList;
    @FXML private Button viewFullRecipeButton;
    @FXML private Button copyRecipeButton;
    @FXML private Button useInMenuButton;
    @FXML private Button closeButton;
    
    private RecipeController recipeController;
    private RecipeBook recipeBook;
    private User currentUser;
    private ObservableList<Recipe> allRecipes;
    private ObservableList<Recipe> filteredRecipes;
    
    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        setupDetailsPane();
        
        recipeBook = RecipeBook.getInstance();
        
        // Listener per selezione ricetta
        recipesTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    showRecipeDetails(newSelection);
                } else {
                    hideRecipeDetails();
                }
            }
        );
    }
    
    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("preparationTime"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        
        // Colonna tags personalizzata
        tagsColumn.setCellValueFactory(cellData -> {
            List<String> tags = cellData.getValue().getTags();
            String tagsString = tags.isEmpty() ? "" : String.join(", ", tags);
            return new SimpleStringProperty(tagsString);
        });
        
        recipesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void setupFilters() {
        // Setup state filter
        stateFilterComboBox.getItems().addAll(
            "Tutti",
            "Bozza",
            "Pubblicato"
        );
        stateFilterComboBox.getSelectionModel().selectFirst();
        
        // Listener per filtri
        searchField.textProperty().addListener((obs, oldText, newText) -> filterRecipes());
        tagFilterComboBox.setOnAction(e -> filterRecipes());
        stateFilterComboBox.setOnAction(e -> filterRecipes());
    }
    
    private void setupDetailsPane() {
        detailsPane.setExpanded(false);
        hideRecipeDetails();
    }
    
    public void setRecipeController(RecipeController recipeController) {
        this.recipeController = recipeController;
        loadRecipes();
    }
    
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        updateButtonsState();
    }
    
    private void loadRecipes() {
        if (recipeController == null) return;
        
        List<Recipe> recipes;
        if (currentUser != null && currentUser.getRole() == User.UserRole.COOK || 
            currentUser != null && currentUser.getRole() == User.UserRole.CHEF) {
            // Chef e cuochi vedono le proprie ricette + quelle pubblicate
            recipes = recipeController.getRecipesByOwner(currentUser.getId());
            recipes.addAll(recipeController.getPublishedRecipes());
            // Rimuovi duplicati
            recipes = recipes.stream().distinct().collect(Collectors.toList());
        } else {
            // Altri vedono solo ricette pubblicate
            recipes = recipeController.getPublishedRecipes();
        }
        
        allRecipes = FXCollections.observableArrayList(recipes);
        filteredRecipes = FXCollections.observableArrayList(recipes);
        recipesTable.setItems(filteredRecipes);
        
        updateRecipeCount();
        loadTags();
    }
    
    private void loadTags() {
        Set<String> allTags = recipeBook.getAllTags();
        
        tagFilterComboBox.getItems().clear();
        tagFilterComboBox.getItems().add("Tutti i tag");
        tagFilterComboBox.getItems().addAll(allTags.stream().sorted().collect(Collectors.toList()));
        tagFilterComboBox.getSelectionModel().selectFirst();
    }
    
    private void filterRecipes() {
        if (allRecipes == null) return;
        
        String searchText = searchField.getText().toLowerCase();
        String selectedTag = tagFilterComboBox.getSelectionModel().getSelectedItem();
        String selectedState = stateFilterComboBox.getSelectionModel().getSelectedItem();
        
        List<Recipe> filtered = allRecipes.stream()
            .filter(recipe -> {
                // Filtro per testo
                if (!searchText.isEmpty()) {
                    boolean matchesName = recipe.getName().toLowerCase().contains(searchText);
                    boolean matchesDescription = recipe.getDescription() != null && 
                                               recipe.getDescription().toLowerCase().contains(searchText);
                    if (!matchesName && !matchesDescription) {
                        return false;
                    }
                }
                
                // Filtro per tag
                if (selectedTag != null && !"Tutti i tag".equals(selectedTag)) {
                    if (!recipe.getTags().contains(selectedTag)) {
                        return false;
                    }
                }
                
                // Filtro per stato
                if (selectedState != null && !"Tutti".equals(selectedState)) {
                    Recipe.RecipeState state = "Bozza".equals(selectedState) ? 
                                              Recipe.RecipeState.DRAFT : 
                                              Recipe.RecipeState.PUBLISHED;
                    if (recipe.getState() != state) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        filteredRecipes.clear();
        filteredRecipes.addAll(filtered);
        updateRecipeCount();
    }
    
    private void showRecipeDetails(Recipe recipe) {
        descriptionLabel.setText(recipe.getDescription() != null ? 
                               recipe.getDescription() : "Nessuna descrizione");
        portionsLabel.setText(String.valueOf(recipe.getBasePortions()));
        
        ObservableList<String> ingredientItems = FXCollections.observableArrayList();
        for (Recipe.Ingredient ingredient : recipe.getIngredients()) {
            ingredientItems.add(String.format("%s: %.2f %s", 
                                            ingredient.getName(), 
                                            ingredient.getQuantity(), 
                                            ingredient.getUnit()));
        }
        ingredientsList.setItems(ingredientItems);
        
        detailsPane.setExpanded(true);
        updateButtonsState();
    }

    private void hideRecipeDetails() {
    descriptionLabel.setText("");
    portionsLabel.setText("");
    ingredientsList.getItems().clear();
    detailsPane.setExpanded(false);
    updateButtonsState();
}

private void updateButtonsState() {
    Recipe selectedRecipe = recipesTable.getSelectionModel().getSelectedItem();
    boolean hasSelection = selectedRecipe != null;
    
    viewFullRecipeButton.setDisable(!hasSelection);
    copyRecipeButton.setDisable(!hasSelection || currentUser == null || 
                               (currentUser.getRole() != User.UserRole.CHEF && 
                                currentUser.getRole() != User.UserRole.COOK));
    useInMenuButton.setDisable(!hasSelection || currentUser == null || 
                              currentUser.getRole() != User.UserRole.CHEF ||
                              selectedRecipe.getState() != Recipe.RecipeState.PUBLISHED);
}

private void updateRecipeCount() {
    int count = filteredRecipes != null ? filteredRecipes.size() : 0;
    recipeCountLabel.setText(count + " ricette");
}

@FXML
private void handleSearch(ActionEvent event) {
    filterRecipes();
}

@FXML
private void handleClearFilters(ActionEvent event) {
    searchField.clear();
    tagFilterComboBox.getSelectionModel().selectFirst();
    stateFilterComboBox.getSelectionModel().selectFirst();
    filterRecipes();
}

@FXML
private void handleViewFullRecipe(ActionEvent event) {
    Recipe selectedRecipe = recipesTable.getSelectionModel().getSelectedItem();
    if (selectedRecipe == null) return;
    
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecipeViewDialog.fxml"));
        Parent root = loader.load();
        
        RecipeViewDialogController controller = loader.getController();
        controller.setRecipe(selectedRecipe);
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Dettagli Ricetta: " + selectedRecipe.getName());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(root));
        dialogStage.showAndWait();
        
    } catch (IOException e) {
        showError("Errore nell'apertura dei dettagli: " + e.getMessage());
    }
}

@FXML
private void handleCopyRecipe(ActionEvent event) {
    Recipe selectedRecipe = recipesTable.getSelectionModel().getSelectedItem();
    if (selectedRecipe == null || currentUser == null) return;
    
    try {
        String newRecipeId = recipeController.copyRecipe(selectedRecipe.getId(), currentUser.getId());
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ricetta Copiata");
        alert.setHeaderText("Copia creata con successo");
        alert.setContentText("È stata creata una copia della ricetta.\nID: " + newRecipeId + 
                           "\nPuoi trovarla nelle tue ricette in bozza.");
        alert.showAndWait();
        
        loadRecipes(); // Ricarica per mostrare la nuova copia
        
    } catch (Exception e) {
        showError("Errore nella copia della ricetta: " + e.getMessage());
    }
}

@FXML
private void handleUseInMenu(ActionEvent event) {
    Recipe selectedRecipe = recipesTable.getSelectionModel().getSelectedItem();
    if (selectedRecipe == null) return;
    
    // Chiudi questa finestra e torna al controller chiamante con la ricetta selezionata
    Stage stage = (Stage) recipesTable.getScene().getWindow();
    stage.setUserData(selectedRecipe);
    stage.close();
}

@FXML
private void handleClose(ActionEvent event) {
    Stage stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
}

private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Errore");
    alert.setHeaderText("Si è verificato un errore");
    alert.setContentText(message);
    alert.showAndWait();
}

/**
 * Metodo per selezionare una ricetta specifica (utile per selezione esterna)
 */
public void selectRecipe(String recipeId) {
    if (filteredRecipes == null) return;
    
    Recipe recipeToSelect = filteredRecipes.stream()
        .filter(r -> r.getId().equals(recipeId))
        .findFirst()
        .orElse(null);
        
    if (recipeToSelect != null) {
        recipesTable.getSelectionModel().select(recipeToSelect);
        recipesTable.scrollTo(recipeToSelect);
    }
}

/**
 * Ottiene la ricetta selezionata
 */
public Recipe getSelectedRecipe() {
    return recipesTable.getSelectionModel().getSelectedItem();
}
}