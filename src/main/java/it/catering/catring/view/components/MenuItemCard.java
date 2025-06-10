package it.catering.catring.view.components;

import it.catering.catring.model.entities.VoceMenu;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MenuItemCard extends VBox {
    private VoceMenu voceMenu;
    
    public MenuItemCard(VoceMenu voceMenu) {
        this.voceMenu = voceMenu;
        setupCard();
    }
    
    private void setupCard() {
        setSpacing(5);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; " +
               "-fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5;");
        
        Label nomeLabel = new Label(voceMenu.getNomeVoce());
        nomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        if (!voceMenu.getNomeVoce().equals(voceMenu.getRicetta().getNome())) {
            Label ricettaLabel = new Label("(" + voceMenu.getRicetta().getNome() + ")");
            ricettaLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");
            getChildren().addAll(nomeLabel, ricettaLabel);
        } else {
            getChildren().add(nomeLabel);
        }
        
        // Hover effect
        setOnMouseEntered(e -> setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #3498db; " +
                                      "-fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5;"));
        setOnMouseExited(e -> setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; " +
                                     "-fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5;"));
    }
    
    public VoceMenu getVoceMenu() {
        return voceMenu;
    }
}
