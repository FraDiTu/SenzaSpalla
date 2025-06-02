package com.saslab.ui;

import com.saslab.Recipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.print.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller per la visualizzazione completa di una ricetta
 * Implementa pattern Controller (GRASP) per gestire l'interfaccia utente
 */
public class RecipeViewDialogController {
    
    @FXML private Label recipeNameLabel;
    @FXML private Label ownerLabel;
    @FXML private Label stateLabel;
    @FXML private Label timeLabel;
    @FXML private Label portionsLabel;
    @FXML private Label descriptionLabel;
    @FXML private FlowPane tagsFlowPane;
    @FXML private GridPane ingredientsGrid;
    @FXML private VBox advanceInstructionsBox;
    @FXML private VBox lastMinuteInstructionsBox;
    @FXML private Label authorLabel;
    @FXML private Label ownerDetailLabel;
    @FXML private Button printButton;
    @FXML private Button closeButton;
    
    private Recipe recipe;
    
    @FXML
    public void initialize() {
        // Inizializzazione se necessaria
    }
    
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        displayRecipeDetails();
    }
    
    private void displayRecipeDetails() {
        if (recipe == null) return;
        
        // Informazioni principali
        recipeNameLabel.setText(recipe.getName());
        ownerLabel.setText("Creata da: " + recipe.getOwnerId());
        stateLabel.setText("Stato: " + recipe.getState().name());
        timeLabel.setText("Tempo: " + recipe.getPreparationTime() + " minuti");
        portionsLabel.setText("Porzioni: " + recipe.getBasePortions());
        
        // Descrizione
        descriptionLabel.setText(recipe.getDescription() != null ? 
                               recipe.getDescription() : "Nessuna descrizione disponibile");
        
        // Tags
        displayTags();
        
        // Ingredienti
        displayIngredients();
        
        // Istruzioni
        displayInstructions();
        
        // Informazioni aggiuntive
        authorLabel.setText(recipe.getAuthorId() != null ? 
                           recipe.getAuthorId() : recipe.getOwnerId());
        ownerDetailLabel.setText(recipe.getOwnerId());
    }
    
    private void displayTags() {
        tagsFlowPane.getChildren().clear();
        
        if (recipe.getTags().isEmpty()) {
            Label noTagsLabel = new Label("Nessun tag");
            noTagsLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
            tagsFlowPane.getChildren().add(noTagsLabel);
        } else {
            for (String tag : recipe.getTags()) {
                Label tagLabel = new Label(tag);
                tagLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5 10 5 10; " +
                                "-fx-background-radius: 15; -fx-font-size: 12px;");
                tagsFlowPane.getChildren().add(tagLabel);
            }
        }
    }
    
    private void displayIngredients() {
        ingredientsGrid.getChildren().clear();
        
        if (recipe.getIngredients().isEmpty()) {
            Label noIngredientsLabel = new Label("Nessun ingrediente");
            noIngredientsLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
            ingredientsGrid.add(noIngredientsLabel, 0, 0, 3, 1);
        } else {
            int row = 0;
            for (Recipe.Ingredient ingredient : recipe.getIngredients()) {
                Label nameLabel = new Label(ingredient.getName());
                Label quantityLabel = new Label(String.format("%.2f", ingredient.getQuantity()));
                Label unitLabel = new Label(ingredient.getUnit());
                
                nameLabel.setStyle("-fx-font-size: 14px;");
                quantityLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                unitLabel.setStyle("-fx-font-size: 14px;");
                
                ingredientsGrid.add(nameLabel, 0, row);
                ingredientsGrid.add(quantityLabel, 1, row);
                ingredientsGrid.add(unitLabel, 2, row);
                
                row++;
            }
        }
    }
    
    private void displayInstructions() {
        // Istruzioni preparazione in anticipo
        advanceInstructionsBox.getChildren().clear();
        if (recipe.getAdvanceInstructions().isEmpty()) {
            Label noInstructionsLabel = new Label("Nessuna preparazione in anticipo");
            noInstructionsLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
            advanceInstructionsBox.getChildren().add(noInstructionsLabel);
        } else {
            int step = 1;
            for (String instruction : recipe.getAdvanceInstructions()) {
                HBox instructionBox = new HBox(10);
                Label stepLabel = new Label(step + ".");
                stepLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 20;");
                Label instructionLabel = new Label(instruction);
                instructionLabel.setWrapText(true);
                instructionLabel.setMaxWidth(700);
                
                instructionBox.getChildren().addAll(stepLabel, instructionLabel);
                advanceInstructionsBox.getChildren().add(instructionBox);
                step++;
            }
        }
        
        // Istruzioni ultimo minuto
        lastMinuteInstructionsBox.getChildren().clear();
        if (recipe.getLastMinuteInstructions().isEmpty()) {
            Label noInstructionsLabel = new Label("Nessuna preparazione dell'ultimo minuto");
            noInstructionsLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
            lastMinuteInstructionsBox.getChildren().add(noInstructionsLabel);
        } else {
            int step = 1;
            for (String instruction : recipe.getLastMinuteInstructions()) {
                HBox instructionBox = new HBox(10);
                Label stepLabel = new Label(step + ".");
                stepLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 20;");
                Label instructionLabel = new Label(instruction);
                instructionLabel.setWrapText(true);
                instructionLabel.setMaxWidth(700);
                
                instructionBox.getChildren().addAll(stepLabel, instructionLabel);
                lastMinuteInstructionsBox.getChildren().add(instructionBox);
                step++;
            }
        }
    }
    
    @FXML
    private void handlePrint(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(printButton.getScene().getWindow())) {
            
            // Crea una versione stampabile del contenuto
            VBox printContent = createPrintableContent();
            
            // Configura la pagina
            PageLayout pageLayout = job.getJobSettings().getPageLayout();
            double printableWidth = pageLayout.getPrintableWidth();
            printContent.setPrefWidth(printableWidth);
            printContent.setMaxWidth(printableWidth);
            
            // Stampa
            boolean printed = job.printPage(printContent);
            if (printed) {
                job.endJob();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Stampa completata");
                alert.setHeaderText("Ricetta stampata con successo");
                alert.showAndWait();
            }
        }
    }
    
    private VBox createPrintableContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        // Titolo
        Text title = new Text(recipe.getName());
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        content.getChildren().add(title);
        
        // Info base
        Text info = new Text(String.format("Tempo: %d minuti | Porzioni: %d | Creata da: %s",
                                         recipe.getPreparationTime(),
                                         recipe.getBasePortions(),
                                         recipe.getOwnerId()));
        content.getChildren().add(info);
        
        // Descrizione
        if (recipe.getDescription() != null && !recipe.getDescription().isEmpty()) {
            content.getChildren().add(new Separator());
            Text descTitle = new Text("Descrizione");
            descTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
            content.getChildren().add(descTitle);
            Text desc = new Text(recipe.getDescription());
            desc.setWrappingWidth(500);
            content.getChildren().add(desc);
        }
        
        // Ingredienti
        content.getChildren().add(new Separator());
        Text ingredientsTitle = new Text("Ingredienti");
        ingredientsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        content.getChildren().add(ingredientsTitle);
        
        VBox ingredientsList = new VBox(3);
        for (Recipe.Ingredient ingredient : recipe.getIngredients()) {
            Text ingredientText = new Text(String.format("• %s: %.2f %s",
                                                        ingredient.getName(),
                                                        ingredient.getQuantity(),
                                                        ingredient.getUnit()));
            ingredientsList.getChildren().add(ingredientText);
        }
        content.getChildren().add(ingredientsList);
        
        // Istruzioni anticipo
        if (!recipe.getAdvanceInstructions().isEmpty()) {
            content.getChildren().add(new Separator());
            Text advanceTitle = new Text("Preparazione in Anticipo");
            advanceTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
            content.getChildren().add(advanceTitle);
            
            VBox advanceList = new VBox(3);
            int step = 1;
            for (String instruction : recipe.getAdvanceInstructions()) {
                Text instructionText = new Text(step + ". " + instruction);
                instructionText.setWrappingWidth(480);
                advanceList.getChildren().add(instructionText);
                step++;
            }
            content.getChildren().add(advanceList);
        }
        
        // Istruzioni ultimo minuto
        if (!recipe.getLastMinuteInstructions().isEmpty()) {
            content.getChildren().add(new Separator());
            Text lastMinuteTitle = new Text("Preparazione dell'Ultimo Minuto");
            lastMinuteTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
            content.getChildren().add(lastMinuteTitle);
            
            VBox lastMinuteList = new VBox(3);
            int step = 1;
            for (String instruction : recipe.getLastMinuteInstructions()) {
                Text instructionText = new Text(step + ". " + instruction);
                instructionText.setWrappingWidth(480);
                lastMinuteList.getChildren().add(instructionText);
                step++;
            }
            content.getChildren().add(lastMinuteList);
        }
        
        return content;
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}