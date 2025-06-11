package com.catring.viewfx;

import com.catring.controller.MenuController;
import com.catring.model.Menu;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * VISTA PER LA BACHECA DEI MENU PUBBLICATI
 * Mostra tutti i menu che sono stati pubblicati sulla bacheca
 */
public class BachecaView {
    
    private MenuController controller;
    private VBox layoutPrincipale;
    
    // Componenti dell'interfaccia
    private ListView<Menu> listaMenuPubblicati;
    private TextArea areaDettagliMenu;
    private Button bottoneAggiorna;
    private Label labelStato;
    
    public BachecaView(MenuController controller) {
        this.controller = controller;
        creaInterfaccia();
        collegaController();
    }
    
    /**
     * Crea l'interfaccia per la bacheca
     */
    private void creaInterfaccia() {
        layoutPrincipale = new VBox();
        layoutPrincipale.setSpacing(15);
        layoutPrincipale.setStyle("-fx-padding: 15px;");
        
        // Intestazione
        Label titolo = new Label("Bacheca Menu Pubblicati");
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        Label descrizione = new Label("Qui puoi visualizzare tutti i menu pubblicati dai chef");
        descrizione.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        // Pulsante aggiorna
        HBox pannelloAzioni = creaPannelloAzioni();
        
        // Contenuto principale
        HBox contenutoPrincipale = creaContenutoPrincipale();
        
        // Area di stato
        labelStato = new Label("Carica la bacheca per visualizzare i menu pubblicati");
        labelStato.setStyle("-fx-text-fill: #27ae60; -fx-padding: 10px; -fx-background-color: #f8f9fa;");
        
        layoutPrincipale.getChildren().addAll(titolo, descrizione, pannelloAzioni, contenutoPrincipale, labelStato);
    }
    
    /**
     * Crea il pannello con i pulsanti di azione
     */
    private HBox creaPannelloAzioni() {
        HBox pannello = new HBox();
        pannello.setSpacing(10);
        
        bottoneAggiorna = new Button("Aggiorna Bacheca");
        bottoneAggiorna.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        pannello.getChildren().add(bottoneAggiorna);
        return pannello;
    }
    
    /**
     * Crea il contenuto principale con lista e dettagli
     */
    private HBox creaContenutoPrincipale() {
        HBox contenuto = new HBox();
        contenuto.setSpacing(15);
        
        // Pannello sinistro - Lista menu pubblicati
        VBox pannelloLista = creaPannelloListaMenu();
        
        // Pannello destro - Dettagli menu selezionato
        VBox pannelloDettagli = creaPannelloDettagli();
        
        contenuto.getChildren().addAll(pannelloLista, pannelloDettagli);
        return contenuto;
    }
    
    /**
     * Crea il pannello con la lista dei menu pubblicati
     */
    private VBox creaPannelloListaMenu() {
        VBox pannello = new VBox();
        pannello.setSpacing(8);
        pannello.setPrefWidth(350);
        
        Label etichetta = new Label("Menu Pubblicati");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        listaMenuPubblicati = new ListView<>();
        listaMenuPubblicati.setPrefHeight(300);
        listaMenuPubblicati.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        
        // Personalizza la visualizzazione dei menu
        listaMenuPubblicati.setCellFactory(listView -> new ListCell<Menu>() {
            @Override
            protected void updateItem(Menu menu, boolean empty) {
                super.updateItem(menu, empty);
                if (empty || menu == null) {
                    setText(null);
                } else {
                    setText(menu.getNome() + " - " + menu.getDescrizione());
                }
            }
        });
        
        pannello.getChildren().addAll(etichetta, listaMenuPubblicati);
        return pannello;
    }
    
    /**
     * Crea il pannello con i dettagli del menu selezionato
     */
    private VBox creaPannelloDettagli() {
        VBox pannello = new VBox();
        pannello.setSpacing(8);
        HBox.setHgrow(pannello, Priority.ALWAYS);
        
        Label etichetta = new Label("Dettagli Menu Selezionato");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        areaDettagliMenu = new TextArea();
        areaDettagliMenu.setPrefHeight(300);
        areaDettagliMenu.setEditable(false);
        areaDettagliMenu.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        areaDettagliMenu.setText("Seleziona un menu dalla lista per vedere i dettagli");
        
        pannello.getChildren().addAll(etichetta, areaDettagliMenu);
        return pannello;
    }
    
    /**
     * Collega i componenti dell'interfaccia al controller
     */
    private void collegaController() {
        // Imposta i componenti nel controller
        controller.setComponentiBacheca(listaMenuPubblicati, areaDettagliMenu, labelStato);
        
        // Collega il pulsante al controller
        bottoneAggiorna.setOnAction(e -> controller.handleAggiornaBackeca());
        
        // Listener per la selezione dei menu
        listaMenuPubblicati.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                controller.handleSelezionaMenuBacheca(newSelection);
            }
        });
    }
    
    /**
     * Restituisce il nodo principale della vista
     */
    public Node getView() {
        return layoutPrincipale;
    }
    
    /**
     * Aggiorna il messaggio di stato
     */
    public void aggiornaStato(String messaggio) {
        if (labelStato != null) {
            labelStato.setText(messaggio);
        }
    }
}