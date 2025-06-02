package com.saslab.ui;

import com.saslab.controller.MenuController;
import com.saslab.controller.RecipeController;
import com.saslab.model.Menu;
import com.saslab.model.MenuSection;
import com.saslab.model.MenuItem;
import com.saslab.Recipe;
import com.saslab.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la creazione e modifica dei menù
 * Implementa pattern Controller (GRASP) per gestire l'interfaccia utente
 */
public class MenuEditController {
    
    @FXML private TextField menuNameField;
    @FXML private TextArea menuDescriptionArea;
    @FXML private TextArea menuNotesArea;
    
    // Caratteristiche del menù
    @FXML private CheckBox requiresChefPresenceCheckBox;
    @FXML private CheckBox hasOnlyHotDishesCheckBox;
    @FXML private CheckBox hasOnlyColdDishesCheckBox;
    @FXML private CheckBox requiresKitchenOnSiteCheckBox;
    @FXML private CheckBox suitableForBuffetCheckBox;
    @FXML private CheckBox fingerFoodOnlyCheckBox;
    
    // Gestione sezioni
    @FXML private TextField sectionNameField;
    @FXML private ListView<String> sectionsList;
    @FXML private Button addSectionButton;
    @FXML private Button removeSectionButton;
    @FXML private Button moveSectionUpButton;
    @FXML private Button moveSectionDownButton;
    
    // Gestione ricette
    @FXML private ComboBox<String> sectionComboBox;
    @FXML private TableView<Recipe> availableRecipesTable;
    @FXML private TableColumn<Recipe, String> recipeNameColumn;
    @FXML private TableColumn<Recipe, String> recipeOwnerColumn;
    @FXML private TableColumn<Recipe, Integer> recipeTimeColumn;
    @FXML private Button addRecipeToMenuButton;
    
    // Visualizzazione menù corrente
    @FXML private TreeView<String> menuTreeView;
    @FXML private Button removeRecipeFromMenuButton;
    @FXML private Button moveRecipeBetweenSectionsButton;
    
    // Controlli
    @FXML private Button saveButton;
    @FXML private Button generatePDFButton;
    @FXML private Button cancelButton;
    @FXML private Label statusLabel;
    @FXML private Label errorLabel;
    
    private MenuController menuController;
    private RecipeController recipeController;
    private User currentUser;
    private MainController mainController;
    private Menu currentMenu;
    private boolean isEditMode = false;
    
    @FXML
    public void initialize() {
        setupTables();
        setupTreeView();
        setupComboBox();
        errorLabel.setVisible(false);
        statusLabel.setVisible(false);
        
        // Setup listeners for checkboxes per evitare combinazioni invalide
        setupCheckBoxListeners();
    }
    
    private void setupTables() {
        recipeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        recipeOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        recipeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("preparationTime"));
        
        availableRecipesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void setupTreeView() {
        menuTreeView.setShowRoot(false);
        menuTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void setupComboBox() {
        // Il combo box verrà popolato quando le sezioni vengono create
    }
    
    private void setupCheckBoxListeners() {
        // Solo piatti caldi e solo piatti freddi sono mutuamente esclusivi
        hasOnlyHotDishesCheckBox.setOnAction(e -> {
            if (hasOnlyHotDishesCheckBox.isSelected()) {
                hasOnlyColdDishesCheckBox.setSelected(false);
            }
        });
        
        hasOnlyColdDishesCheckBox.setOnAction(e -> {
            if (hasOnlyColdDishesCheckBox.isSelected()) {
                hasOnlyHotDishesCheckBox.setSelected(false);
            }
        });
    }
    
    public void setControllers(MenuController menuController, RecipeController recipeController) {
        this.menuController = menuController;
        this.recipeController = recipeController;
        loadAvailableRecipes();
    }
    
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void setMenuForEditing(Menu menu) {
        this.currentMenu = menu;
        this.isEditMode = true;
        loadMenuData();
        updateUIForEditMode();
    }
    
    private void loadAvailableRecipes() {
        if (recipeController == null) return;
        
        List<Recipe> publishedRecipes = recipeController.getPublishedRecipes();
        ObservableList<Recipe> recipes = FXCollections.observableArrayList(publishedRecipes);
        availableRecipesTable.setItems(recipes);
    }
    
    private void loadMenuData() {
        if (currentMenu == null) return;
        
        menuNameField.setText(currentMenu.getName());
        menuDescriptionArea.setText(currentMenu.getDescription());
        menuNotesArea.setText(currentMenu.getNotes());
        
        // Carica caratteristiche
        requiresChefPresenceCheckBox.setSelected(currentMenu.isRequiresChefPresence());
        hasOnlyHotDishesCheckBox.setSelected(currentMenu.isHasOnlyHotDishes());
        hasOnlyColdDishesCheckBox.setSelected(currentMenu.isHasOnlyColdDishes());
        requiresKitchenOnSiteCheckBox.setSelected(currentMenu.isRequiresKitchenOnSite());
        suitableForBuffetCheckBox.setSelected(currentMenu.isSuitableForBuffet());
        fingerFoodOnlyCheckBox.setSelected(currentMenu.isFingerFoodOnly());
        
        // Carica sezioni
        ObservableList<String> sections = FXCollections.observableArrayList();
        for (MenuSection section : currentMenu.getSections()) {
            sections.add(section.getTitle());
        }
        sectionsList.setItems(sections);
        updateSectionComboBox();
        
        updateMenuTreeView();
        updateStatusLabel();
    }
    
    private void updateUIForEditMode() {
        if (currentMenu != null && !currentMenu.canBeModified()) {
            setAllControlsEnabled(false);
            showError("Il menù non può essere modificato nel suo stato attuale");
        }
    }
    
    private void setAllControlsEnabled(boolean enabled) {
        menuNameField.setDisable(!enabled);
        menuDescriptionArea.setDisable(!enabled);
        
        requiresChefPresenceCheckBox.setDisable(!enabled);
        hasOnlyHotDishesCheckBox.setDisable(!enabled);
        hasOnlyColdDishesCheckBox.setDisable(!enabled);
        requiresKitchenOnSiteCheckBox.setDisable(!enabled);
        suitableForBuffetCheckBox.setDisable(!enabled);
        fingerFoodOnlyCheckBox.setDisable(!enabled);
        
        sectionNameField.setDisable(!enabled);
        addSectionButton.setDisable(!enabled);
        removeSectionButton.setDisable(!enabled);
        moveSectionUpButton.setDisable(!enabled);
        moveSectionDownButton.setDisable(!enabled);
        
        addRecipeToMenuButton.setDisable(!enabled);
        removeRecipeFromMenuButton.setDisable(!enabled);
        moveRecipeBetweenSectionsButton.setDisable(!enabled);
        
        saveButton.setDisable(!enabled);
        generatePDFButton.setDisable(!enabled);
    }
    
    @FXML
    private void handleAddSection(ActionEvent event) {
        String sectionName = sectionNameField.getText().trim();
        if (sectionName.isEmpty()) {
            showError("Inserire il nome della sezione");
            return;
        }
        
        if (sectionsList.getItems().contains(sectionName)) {
            showError("Una sezione con questo nome esiste già");
            return;
        }
        
        sectionsList.getItems().add(sectionName);
        sectionNameField.clear();
        updateSectionComboBox();
        clearError();
    }
    
    @FXML
    private void handleRemoveSection(ActionEvent event) {
        String selectedSection = sectionsList.getSelectionModel().getSelectedItem();
        if (selectedSection == null) {
            showError("Seleziona una sezione da rimuovere");
            return;
        }
        
        // Verifica se la sezione contiene ricette
        if (isEditMode && currentMenu != null) {
            MenuSection section = currentMenu.findSectionByName(selectedSection);
            if (section != null && section.hasMenuItems()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sezione non vuota");
                alert.setHeaderText("La sezione contiene delle ricette");
                alert.setContentText("Rimuovi prima tutte le ricette dalla sezione.");
                alert.showAndWait();
                return;
            }
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma rimozione");
        confirmAlert.setHeaderText("Rimuovere la sezione selezionata?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            sectionsList.getItems().remove(selectedSection);
            updateSectionComboBox();
            updateMenuTreeView();
        }
    }
    
    @FXML
    private void handleMoveSectionUp(ActionEvent event) {
        int selectedIndex = sectionsList.getSelectionModel().getSelectedIndex();
        if (selectedIndex <= 0) {
            showError("Seleziona una sezione da spostare verso l'alto");
            return;
        }
        
        ObservableList<String> items = sectionsList.getItems();
        String item = items.remove(selectedIndex);
        items.add(selectedIndex - 1, item);
        sectionsList.getSelectionModel().select(selectedIndex - 1);
    }
    
    @FXML
    private void handleMoveSectionDown(ActionEvent event) {
        int selectedIndex = sectionsList.getSelectionModel().getSelectedIndex();
        ObservableList<String> items = sectionsList.getItems();
        
        if (selectedIndex < 0 || selectedIndex >= items.size() - 1) {
            showError("Seleziona una sezione da spostare verso il basso");
            return;
        }
        
        String item = items.remove(selectedIndex);
        items.add(selectedIndex + 1, item);
        sectionsList.getSelectionModel().select(selectedIndex + 1);
    }
    
    @FXML
    private void handleAddRecipeToMenu(ActionEvent event) {
        Recipe selectedRecipe = availableRecipesTable.getSelectionModel().getSelectedItem();
        String selectedSection = sectionComboBox.getSelectionModel().getSelectedItem();
        
        if (selectedRecipe == null) {
            showError("Seleziona una ricetta da aggiungere");
            return;
        }
        
        if (selectedSection == null) {
            showError("Seleziona una sezione di destinazione");
            return;
        }
        
        try {
            if (isEditMode && currentMenu != null) {
                menuController.addRecipeToSection(currentMenu.getId(), selectedRecipe.getId(), selectedSection);
                loadMenuData(); // Ricarica per aggiornare la vista
            } else {
                // Modalità creazione - per ora mostra messaggio
                showInfo("Salva prima il menù per aggiungere ricette");
                return;
            }
            
            updateMenuTreeView();
            clearError();
            
        } catch (Exception e) {
            showError("Errore nell'aggiunta della ricetta: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRemoveRecipeFromMenu(ActionEvent event) {
        TreeItem<String> selectedItem = menuTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.getParent() == null) {
            showError("Seleziona una ricetta da rimuovere");
            return;
        }
        
        String recipeName = selectedItem.getValue();
        String sectionName = selectedItem.getParent().getValue();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setHeaderText("Rimuovere la ricetta dal menù?");
        alert.setContentText("Ricetta: " + recipeName + "\nSezione: " + sectionName);
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (isEditMode && currentMenu != null) {
                    // Trova l'ID della ricetta dal nome (semplificazione)
                    MenuItem menuItem = findMenuItemByName(recipeName, sectionName);
                    if (menuItem != null) {
                        menuController.removeRecipeFromSection(currentMenu.getId(), menuItem.getRecipeId(), sectionName);
                        updateMenuTreeView();
                    }
                }
            } catch (Exception e) {
                showError("Errore nella rimozione: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleMoveRecipeBetweenSections(ActionEvent event) {
        TreeItem<String> selectedItem = menuTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.getParent() == null) {
            showError("Seleziona una ricetta da spostare");
            return;
        }
        
        String recipeName = selectedItem.getValue();
        String currentSection = selectedItem.getParent().getValue();
        
        // Mostra dialog per scegliere la sezione di destinazione
        List<String> availableSections = new ArrayList<>(sectionsList.getItems());
        availableSections.remove(currentSection);
        
        if (availableSections.isEmpty()) {
            showError("Non ci sono altre sezioni disponibili");
            return;
        }
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(availableSections.get(0), availableSections);
        dialog.setTitle("Sposta ricetta");
        dialog.setHeaderText("Scegli la sezione di destinazione");
        dialog.setContentText("Ricetta: " + recipeName + "\nSezione attuale: " + currentSection);
        
        dialog.showAndWait().ifPresent(destinationSection -> {
            try {
                if (isEditMode && currentMenu != null) {
                    MenuItem menuItem = findMenuItemByName(recipeName, currentSection);
                    if (menuItem != null) {
                        menuController.moveRecipesBetweenSection(currentMenu.getId(), menuItem.getRecipeId(), 
                                                              currentSection, destinationSection);
                        updateMenuTreeView();
                    }
                }
            } catch (Exception e) {
                showError("Errore nello spostamento: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        try {
            if (isEditMode) {
                updateCurrentMenu();
                showSuccess("Menù aggiornato con successo");
            } else {
                String menuId = createNewMenu();
                showSuccess("Menù creato con successo! ID: " + menuId);
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
    private void handleGeneratePDF(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        if (sectionsList.getItems().isEmpty()) {
            showError("Il menù deve avere almeno una sezione");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma generazione PDF");
        alert.setHeaderText("Generare il PDF del menù?");
        alert.setContentText("Il menù verrà pubblicato e non potrà più essere modificato.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (!isEditMode) {
                    createNewMenu();
                } else {
                    updateCurrentMenu();
                }
                
                String pdfPath = menuController.confirmMenuCreationAndGeneratePDF(currentMenu.getId());
                showSuccess("PDF generato con successo: " + pdfPath);
                
                updateStatusLabel();
                setAllControlsEnabled(false);
                
                if (mainController != null) {
                    mainController.refreshAllData();
                }
                
            } catch (Exception e) {
                showError("Errore nella generazione PDF: " + e.getMessage());
            }
        }
    }
    
    private String createNewMenu() {
        String name = menuNameField.getText().trim();
        String description = menuDescriptionArea.getText().trim();
        
        String menuId = menuController.createMenu(currentUser.getId(), name);
        Menu menu = menuController.getMenu(menuId);
        
        // Imposta descrizione e note
        if (!description.isEmpty()) {
            menu.setDescription(description);
        }
        
        String notes = menuNotesArea.getText().trim();
        if (!notes.isEmpty()) {
            menu.setNotes(notes);
        }
        
        // Imposta caratteristiche
        menu.setRequiresChefPresence(requiresChefPresenceCheckBox.isSelected());
        menu.setHasOnlyHotDishes(hasOnlyHotDishesCheckBox.isSelected());
        menu.setHasOnlyColdDishes(hasOnlyColdDishesCheckBox.isSelected());
        menu.setRequiresKitchenOnSite(requiresKitchenOnSiteCheckBox.isSelected());
        menu.setSuitableForBuffet(suitableForBuffetCheckBox.isSelected());
        menu.setFingerFoodOnly(fingerFoodOnlyCheckBox.isSelected());
        
        // Definisci sezioni
        if (!sectionsList.getItems().isEmpty()) {
            List<String> sections = new ArrayList<>(sectionsList.getItems());
            menuController.defineMenuSections(menuId, sections);
        }
        
        // Imposta come menù corrente e modalità modifica
        this.currentMenu = menu;
        this.isEditMode = true;
        
        return menuId;
    }
    
    private void updateCurrentMenu() {
        if (currentMenu == null || !currentMenu.canBeModified()) return;
        
        // Aggiorna campi modificabili
        currentMenu.setDescription(menuDescriptionArea.getText().trim());
        currentMenu.setNotes(menuNotesArea.getText().trim());
        
        // Aggiorna caratteristiche
        currentMenu.setRequiresChefPresence(requiresChefPresenceCheckBox.isSelected());
        currentMenu.setHasOnlyHotDishes(hasOnlyHotDishesCheckBox.isSelected());
        currentMenu.setHasOnlyColdDishes(hasOnlyColdDishesCheckBox.isSelected());
        currentMenu.setRequiresKitchenOnSite(requiresKitchenOnSiteCheckBox.isSelected());
        currentMenu.setSuitableForBuffet(suitableForBuffetCheckBox.isSelected());
        currentMenu.setFingerFoodOnly(fingerFoodOnlyCheckBox.isSelected());
    }
    
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        if (menuNameField.getText().trim().isEmpty()) {
            errors.append("Il nome del menù è obbligatorio\n");
        }
        
        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        
        return true;
    }
    
    private void updateSectionComboBox() {
        ObservableList<String> sections = FXCollections.observableArrayList(sectionsList.getItems());
        sectionComboBox.setItems(sections);
        
        if (!sections.isEmpty() && sectionComboBox.getSelectionModel().isEmpty()) {
            sectionComboBox.getSelectionModel().selectFirst();
        }
    }
    
    private void updateMenuTreeView() {
        TreeItem<String> root = new TreeItem<>("Menù");
        
        if (isEditMode && currentMenu != null) {
            for (MenuSection section : currentMenu.getSections()) {
                TreeItem<String> sectionItem = new TreeItem<>(section.getTitle());
                
                for (MenuItem menuItem : section.getMenuItems()) {
                    TreeItem<String> recipeItem = new TreeItem<>(menuItem.getDisplayName());
                    sectionItem.getChildren().add(recipeItem);
                }
                
                sectionItem.setExpanded(true);
                root.getChildren().add(sectionItem);
            }
        } else {
            // Modalità creazione - mostra solo le sezioni
            for (String sectionName : sectionsList.getItems()) {
                TreeItem<String> sectionItem = new TreeItem<>(sectionName);
                sectionItem.setExpanded(true);
                root.getChildren().add(sectionItem);
            }
        }
        
        root.setExpanded(true);
        menuTreeView.setRoot(root);
    }
    
    private MenuItem findMenuItemByName(String recipeName, String sectionName) {
        if (currentMenu == null) return null;
        
        MenuSection section = currentMenu.findSectionByName(sectionName);
        if (section == null) return null;
        
        return section.getMenuItems().stream()
                .filter(item -> item.getDisplayName().equals(recipeName))
                .findFirst()
                .orElse(null);
    }
    
    private void updateStatusLabel() {
        if (currentMenu != null) {
            statusLabel.setText("Stato: " + currentMenu.getState().getDisplayName());
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