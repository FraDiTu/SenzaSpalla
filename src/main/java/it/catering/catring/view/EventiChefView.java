// File: src/main/java/it/catering/catring/view/EventiChefView.java
package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.EventoController;
import it.catering.catring.model.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class EventiChefView {
    private EventoController eventoController;
    private VBox mainContent;
    private ListView<Evento> eventiListView;
    private VBox eventoDetailsPane;
    private Evento selectedEvento;
    
    public EventiChefView() {
        this.eventoController = new EventoController();
        
        User currentUser = ApplicationController.getInstance().getAuthController().getCurrentUser();
        this.eventoController.setCurrentUser(currentUser);
        
        createView();
        loadEventi();
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
        
        // Content area
        HBox contentArea = new HBox(20);
        contentArea.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        
        // Eventi list
        VBox eventiListPane = createEventiListPane();
        
        // Evento details
        eventoDetailsPane = createEventoDetailsPane();
        
        contentArea.getChildren().addAll(eventiListPane, eventoDetailsPane);
        
        mainContent.getChildren().addAll(header, new Separator(), contentArea);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("I Miei Eventi Assegnati");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> loadEventi());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Nota informativa
        Label infoLabel = new Label("📋 Visualizzazione eventi dove sei stato assegnato come chef");
        infoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        header.getChildren().addAll(titleLabel, spacer, infoLabel, refreshButton);
        return header;
    }
    
    private VBox createEventiListPane() {
        VBox pane = new VBox(10);
        pane.setPrefWidth(350);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label listTitle = new Label("Eventi Assegnati");
        listTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        eventiListView = new ListView<>();
        eventiListView.setPrefHeight(500);
        eventiListView.setCellFactory(listView -> new EventoListCell());
        eventiListView.getSelectionModel().selectedItemProperty().addListener((obs, oldEvento, newEvento) -> {
            selectedEvento = newEvento;
            updateEventoDetails();
        });
        
        pane.getChildren().addAll(listTitle, eventiListView);
        return pane;
    }
    
    private VBox createEventoDetailsPane() {
        VBox pane = new VBox(15);
        pane.setPrefWidth(650);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label detailsTitle = new Label("Dettagli Evento");
        detailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label noSelectionLabel = new Label("Seleziona un evento dalla lista per visualizzare i dettagli");
        noSelectionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        pane.getChildren().addAll(detailsTitle, noSelectionLabel);
        return pane;
    }
    
    private void updateEventoDetails() {
        eventoDetailsPane.getChildren().clear();
        
        Label detailsTitle = new Label("Dettagli Evento");
        detailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        eventoDetailsPane.getChildren().add(detailsTitle);
        
        if (selectedEvento == null) {
            Label noSelectionLabel = new Label("Seleziona un evento dalla lista per visualizzare i dettagli");
            noSelectionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            eventoDetailsPane.getChildren().add(noSelectionLabel);
            return;
        }
        
        // Evento info
        VBox eventoInfo = new VBox(10);
        eventoInfo.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label nomeLabel = new Label("📅 " + selectedEvento.getNome());
        nomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        eventoInfo.getChildren().add(nomeLabel);
        
        Label periodoLabel = new Label("🗓 Periodo: " + selectedEvento.getDataInizio() + 
                                     (selectedEvento.getDataFine().equals(selectedEvento.getDataInizio()) ? "" : 
                                      " - " + selectedEvento.getDataFine()));
        eventoInfo.getChildren().add(periodoLabel);
        
        Label luogoLabel = new Label("📍 Luogo: " + selectedEvento.getLuogo());
        eventoInfo.getChildren().add(luogoLabel);
        
        Label tipoLabel = new Label("🏷 Tipo: " + selectedEvento.getTipo());
        eventoInfo.getChildren().add(tipoLabel);
        
        Label clienteLabel = new Label("👤 Cliente: " + selectedEvento.getCliente().getNome() + 
                                     " (" + selectedEvento.getCliente().getTipo() + ")");
        eventoInfo.getChildren().add(clienteLabel);
        
        Label organizzatoreLabel = new Label("👔 Organizzatore: " + selectedEvento.getOrganizzatore().getNomeCompleto());
        eventoInfo.getChildren().add(organizzatoreLabel);
        
        Label statoLabel = new Label("⚡ Stato: " + selectedEvento.getStato());
        statoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + getColorForStato(selectedEvento.getStato()) + ";");
        eventoInfo.getChildren().add(statoLabel);
        
        if (selectedEvento.getNote() != null && !selectedEvento.getNote().trim().isEmpty()) {
            Label noteLabel = new Label("📝 Note: " + selectedEvento.getNote());
            noteLabel.setWrapText(true);
            eventoInfo.getChildren().add(noteLabel);
        }
        
        // Informazioni di contatto cliente
        if (selectedEvento.getCliente().getEmail() != null || selectedEvento.getCliente().getTelefono() != null) {
            VBox contattiBox = new VBox(5);
            contattiBox.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 10; -fx-background-radius: 5;");
            
            Label contattiTitle = new Label("📞 Contatti Cliente:");
            contattiTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
            contattiBox.getChildren().add(contattiTitle);
            
            if (selectedEvento.getCliente().getEmail() != null) {
                Label emailLabel = new Label("✉ Email: " + selectedEvento.getCliente().getEmail());
                contattiBox.getChildren().add(emailLabel);
            }
            
            if (selectedEvento.getCliente().getTelefono() != null) {
                Label telefonoLabel = new Label("📱 Telefono: " + selectedEvento.getCliente().getTelefono());
                contattiBox.getChildren().add(telefonoLabel);
            }
            
            eventoInfo.getChildren().add(contattiBox);
        }
        
        // Servizi
// Sezione servizi corretta in EventiChefView.java - updateEventoDetails()
// Servizi
VBox serviziArea = new VBox(10);
serviziArea.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");

Label serviziTitle = new Label("🍽 Servizi Richiesti:");
serviziTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
serviziArea.getChildren().add(serviziTitle);

if (selectedEvento.getServizi().isEmpty()) {
    Label noServiziLabel = new Label("Nessun servizio ancora definito.");
    noServiziLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
    serviziArea.getChildren().add(noServiziLabel);
} else {
    for (Servizio servizio : selectedEvento.getServizi()) {
        VBox servizioBox = new VBox(5);
        servizioBox.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 1; -fx-padding: 10; -fx-background-radius: 3;");
        
        Label servizioLabel = new Label("🍴 " + servizio.getTipo().toUpperCase() + " (" + 
            servizio.getFasciaOrariaInizio() + " - " + servizio.getFasciaOrariaFine() + ")");
        servizioLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057;");
        servizioBox.getChildren().add(servizioLabel);
        
        Label personeLabel = new Label("👥 Persone: " + servizio.getNumeroPersone());
        servizioBox.getChildren().add(personeLabel);
        
        if (servizio.getMenu() != null) {
            Label menuLabel = new Label("📋 Menu assegnato: " + servizio.getMenu().getTitolo());
            menuLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            servizioBox.getChildren().add(menuLabel);
            
            // Mostra le caratteristiche del menu
            if (servizio.getMenu().getDescrizione() != null && !servizio.getMenu().getDescrizione().trim().isEmpty()) {
                Label menuDescLabel = new Label("📝 Caratteristiche: " + servizio.getMenu().getDescrizione());
                menuDescLabel.setWrapText(true);
                menuDescLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");
                servizioBox.getChildren().add(menuDescLabel);
            }
        } else {
            Label noMenuLabel = new Label("⚠ Menu: Non ancora assegnato");
            noMenuLabel.setStyle("-fx-text-fill: #e74c3c;");
            servizioBox.getChildren().add(noMenuLabel);
        }        
        serviziArea.getChildren().add(servizioBox);
    }
}
        
        eventoDetailsPane.getChildren().addAll(eventoInfo, serviziArea);
    }
    
    private String getColorForStato(it.catering.catring.model.states.StatoEvento stato) {
        return switch (stato) {
            case BOZZA -> "#95a5a6";
            case IN_CORSO -> "#f39c12";
            case MENU_PROPOSTI -> "#3498db";
            case APPROVATO -> "#27ae60";
            case COMPLETATO -> "#2ecc71";
            case ANNULLATO -> "#e74c3c";
        };
    }
    
    private void loadEventi() {
        try {
            List<Evento> eventi = eventoController.getEventiForCurrentUser();
            ObservableList<Evento> eventiList = FXCollections.observableArrayList(eventi);
            eventiListView.setItems(eventiList);
        } catch (Exception e) {
            showAlert("Errore", "Impossibile caricare gli eventi: " + e.getMessage());
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
    
    // Custom ListCell for better event display (sola lettura)
    private static class EventoListCell extends ListCell<Evento> {
        @Override
        protected void updateItem(Evento evento, boolean empty) {
            super.updateItem(evento, empty);
            
            if (empty || evento == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                VBox content = new VBox(3);
                
                Label nameLabel = new Label("📅 " + evento.getNome());
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                
                Label dateLabel = new Label("🗓 " + evento.getDataInizio().toString() + 
                    (evento.getDataFine().equals(evento.getDataInizio()) ? "" : " - " + evento.getDataFine()));
                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                
                Label statusLabel = new Label("⚡ " + evento.getStato().toString());
                statusLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + 
                    getColorForStato(evento.getStato()) + ";");
                
                Label clienteLabel = new Label("👤 " + evento.getCliente().getNome());
                clienteLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
                
                content.getChildren().addAll(nameLabel, dateLabel, statusLabel, clienteLabel);
                
                setGraphic(content);
                setText(null);
                
                // Style based on status
                String bgColor = switch (evento.getStato()) {
                    case BOZZA -> "#f8f9fa";
                    case IN_CORSO -> "#fff3cd";
                    case APPROVATO -> "#d4edda";
                    case COMPLETATO -> "#d1ecf1";
                    case ANNULLATO -> "#f8d7da";
                    default -> "#ffffff";
                };
                
                setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 8;");
            }
        }
        
        private String getColorForStato(it.catering.catring.model.states.StatoEvento stato) {
            return switch (stato) {
                case BOZZA -> "#95a5a6";
                case IN_CORSO -> "#f39c12";
                case MENU_PROPOSTI -> "#3498db";
                case APPROVATO -> "#27ae60";
                case COMPLETATO -> "#2ecc71";
                case ANNULLATO -> "#e74c3c";
            };
        }
    }
}