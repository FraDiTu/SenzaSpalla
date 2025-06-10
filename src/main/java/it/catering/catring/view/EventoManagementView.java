package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.EventoController;
import it.catering.catring.controller.PersonaleController;
import it.catering.catring.controller.MenuController;
import it.catering.catring.model.entities.*;
import it.catering.catring.model.entities.Menu;
import it.catering.catring.model.states.StatoEvento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class EventoManagementView {
    private EventoController eventoController;
    private PersonaleController personaleController;
    private MenuController menuController;
    private VBox mainContent;
    private ListView<Evento> eventiListView;
    private VBox eventoDetailsPane;
    private Evento selectedEvento;
    
    public EventoManagementView() {
        this.eventoController = new EventoController();
        this.personaleController = new PersonaleController();
        this.menuController = ApplicationController.getInstance().getMenuController();
        
        User currentUser = ApplicationController.getInstance().getAuthController().getCurrentUser();
        this.eventoController.setCurrentUser(currentUser);
        this.personaleController.setCurrentUser(currentUser);
        
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
        
        Label titleLabel = new Label("Gestione Eventi");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button newEventoButton = new Button("Nuovo Evento");
        newEventoButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        newEventoButton.setOnAction(e -> showCreateEventoDialog());
        
        Button clientiButton = new Button("Gestisci Clienti");
        clientiButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        clientiButton.setOnAction(e -> showClientiDialog());
        
        Button statsButton = new Button("Statistiche");
        statsButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        statsButton.setOnAction(e -> showStatistiche());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, statsButton, clientiButton, newEventoButton);
        return header;
    }
    
    private VBox createEventiListPane() {
        VBox pane = new VBox(10);
        pane.setPrefWidth(350);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label listTitle = new Label("I Miei Eventi");
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
        
        Label nomeLabel = new Label("Nome: " + selectedEvento.getNome());
        nomeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        eventoInfo.getChildren().add(nomeLabel);
        
        Label periodoLabel = new Label("Periodo: " + selectedEvento.getDataInizio() + 
                                     (selectedEvento.getDataFine().equals(selectedEvento.getDataInizio()) ? "" : 
                                      " - " + selectedEvento.getDataFine()));
        eventoInfo.getChildren().add(periodoLabel);
        
        Label luogoLabel = new Label("Luogo: " + selectedEvento.getLuogo());
        eventoInfo.getChildren().add(luogoLabel);
        
        Label tipoLabel = new Label("Tipo: " + selectedEvento.getTipo());
        eventoInfo.getChildren().add(tipoLabel);
        
        Label clienteLabel = new Label("Cliente: " + selectedEvento.getCliente().getNome() + 
                                     " (" + selectedEvento.getCliente().getTipo() + ")");
        eventoInfo.getChildren().add(clienteLabel);
        
        Label organizzatoreLabel = new Label("Organizzatore: " + selectedEvento.getOrganizzatore().getNomeCompleto());
        eventoInfo.getChildren().add(organizzatoreLabel);
        
        if (selectedEvento.getChef() != null) {
            Label chefLabel = new Label("Chef: " + selectedEvento.getChef().getNomeCompleto());
            eventoInfo.getChildren().add(chefLabel);
        }
        
        Label statoLabel = new Label("Stato: " + selectedEvento.getStato());
        statoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + getColorForStato(selectedEvento.getStato()) + ";");
        eventoInfo.getChildren().add(statoLabel);
        
        if (selectedEvento.getNote() != null && !selectedEvento.getNote().trim().isEmpty()) {
            Label noteLabel = new Label("Note: " + selectedEvento.getNote());
            noteLabel.setWrapText(true);
            eventoInfo.getChildren().add(noteLabel);
        }
        
        // Servizi
        VBox serviziArea = new VBox(10);
        serviziArea.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label serviziTitle = new Label("Servizi:");
        serviziTitle.setStyle("-fx-font-weight: bold;");
        serviziArea.getChildren().add(serviziTitle);
        
        if (selectedEvento.getServizi().isEmpty()) {
            Label noServiziLabel = new Label("Nessun servizio presente. Aggiungi un servizio per iniziare.");
            noServiziLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            serviziArea.getChildren().add(noServiziLabel);
        } else {
            for (Servizio servizio : selectedEvento.getServizi()) {
                VBox servizioBox = new VBox(5);
                servizioBox.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 1; -fx-padding: 10; -fx-background-radius: 3;");
                
                Label servizioLabel = new Label(servizio.getTipo() + " (" + 
                    servizio.getFasciaOrariaInizio() + " - " + servizio.getFasciaOrariaFine() + ")");
                servizioLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057;");
                servizioBox.getChildren().add(servizioLabel);
                
                Label personeLabel = new Label("Persone: " + servizio.getNumeroPersone());
                servizioBox.getChildren().add(personeLabel);
                
                if (servizio.getMenu() != null) {
                    Label menuLabel = new Label("Menu: " + servizio.getMenu().getTitolo());
                    menuLabel.setStyle("-fx-text-fill: #27ae60;");
                    servizioBox.getChildren().add(menuLabel);
                } else {
                    Label noMenuLabel = new Label("Menu: Non assegnato");
                    noMenuLabel.setStyle("-fx-text-fill: #e74c3c;");
                    servizioBox.getChildren().add(noMenuLabel);
                }
                
                serviziArea.getChildren().add(servizioBox);
            }
        }
        
        // Action buttons
        HBox buttonBox = createEventoActionButtons();
        
        eventoDetailsPane.getChildren().addAll(eventoInfo, serviziArea, buttonBox);
    }
    
    private HBox createEventoActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        VBox buttonContainer = new VBox(10);
        HBox topButtons = new HBox(10);
        HBox bottomButtons = new HBox(10);
        
        Button assegnaChefButton = new Button("Assegna Chef");
        assegnaChefButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        assegnaChefButton.setOnAction(e -> showAssegnaChefDialog());
        assegnaChefButton.setDisable(selectedEvento == null || selectedEvento.hasChefAssegnato());
        
        Button addServizioButton = new Button("Aggiungi Servizio");
        addServizioButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        addServizioButton.setOnAction(e -> showAddServizioDialog());
        addServizioButton.setDisable(selectedEvento == null || selectedEvento.getStato() == StatoEvento.COMPLETATO);
        
        Button assegnaMenuButton = new Button("Assegna Menu");
        assegnaMenuButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        assegnaMenuButton.setOnAction(e -> showAssegnaMenuDialog());
        assegnaMenuButton.setDisable(selectedEvento == null || selectedEvento.getServizi().isEmpty());
        
        Button approvaButton = new Button("Approva Menu");
        approvaButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        approvaButton.setOnAction(e -> approvaEvento());
        approvaButton.setDisable(selectedEvento == null || selectedEvento.getStato() == StatoEvento.APPROVATO || selectedEvento.getStato() == StatoEvento.COMPLETATO);
        
        topButtons.getChildren().addAll(assegnaChefButton, addServizioButton, assegnaMenuButton, approvaButton);
        
        Button completaButton = new Button("Completa Evento");
        completaButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        completaButton.setOnAction(e -> completaEvento());
        completaButton.setDisable(selectedEvento == null || selectedEvento.getStato() != StatoEvento.APPROVATO);
        
        Button annullaButton = new Button("Annulla Evento");
        annullaButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        annullaButton.setOnAction(e -> annullaEvento());
        annullaButton.setDisable(selectedEvento == null || selectedEvento.getStato() == StatoEvento.COMPLETATO);
        
        Button deleteButton = new Button("Elimina Evento");
        deleteButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        deleteButton.setOnAction(e -> deleteEvento());
        deleteButton.setDisable(selectedEvento == null || selectedEvento.getStato() != StatoEvento.BOZZA);
        
        bottomButtons.getChildren().addAll(completaButton, annullaButton, deleteButton);
        
        buttonContainer.getChildren().addAll(topButtons, bottomButtons);
        buttonBox.getChildren().add(buttonContainer);
        
        return buttonBox;
    }
    
    private void showCreateEventoDialog() {
        Dialog<Evento> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Evento");
        dialog.setHeaderText("Crea un nuovo evento");
        
        ButtonType createButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome evento");
        
        DatePicker dataInizioPicker = new DatePicker(LocalDate.now());
        DatePicker dataFinePicker = new DatePicker(LocalDate.now());
        
        TextField luogoField = new TextField();
        luogoField.setPromptText("Luogo evento");
        
        ComboBox<Cliente> clienteCombo = new ComboBox<>();
        List<Cliente> clienti = eventoController.getAllClienti();
        clienteCombo.setItems(FXCollections.observableArrayList(clienti));
        clienteCombo.setConverter(new javafx.util.StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                return cliente != null ? cliente.getNome() + " (" + cliente.getTipo() + ")" : "";
            }
            
            @Override
            public Cliente fromString(String string) {
                return null;
            }
        });
        
        CheckBox ricorrenteBox = new CheckBox("Evento ricorrente");
        
        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Note aggiuntive");
        noteArea.setPrefRowCount(3);
        
        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Data inizio:"), 0, 1);
        grid.add(dataInizioPicker, 1, 1);
        grid.add(new Label("Data fine:"), 0, 2);
        grid.add(dataFinePicker, 1, 2);
        grid.add(new Label("Luogo:"), 0, 3);
        grid.add(luogoField, 1, 3);
        grid.add(new Label("Cliente:"), 0, 4);
        grid.add(clienteCombo, 1, 4);
        grid.add(ricorrenteBox, 1, 5);
        grid.add(new Label("Note:"), 0, 6);
        grid.add(noteArea, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String nome = nomeField.getText().trim();
                    LocalDate dataInizio = dataInizioPicker.getValue();
                    LocalDate dataFine = dataFinePicker.getValue();
                    String luogo = luogoField.getText().trim();
                    Cliente cliente = clienteCombo.getValue();
                    String note = noteArea.getText().trim();
                    
                    if (nome.isEmpty() || dataInizio == null || luogo.isEmpty() || cliente == null) {
                        showAlert("Errore", "Compilare tutti i campi obbligatori");
                        return null;
                    }
                    
                    if (dataFine.isBefore(dataInizio)) {
                        showAlert("Errore", "La data fine deve essere successiva alla data inizio");
                        return null;
                    }
                    
                    Evento evento = eventoController.createEvento(nome, dataInizio, dataFine, luogo, cliente);
                    evento.setRicorrente(ricorrenteBox.isSelected());
                    if (!note.isEmpty()) {
                        evento.setNote(note);
                    }
                    
                    return evento;
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(evento -> {
            loadEventi();
            eventiListView.getSelectionModel().select(evento);
            showAlert("Successo", "Evento creato con successo!");
        });
    }
    
    private void showAssegnaChefDialog() {
        if (selectedEvento == null) return;
        
        List<Chef> chefs = personaleController.getAllChef();
        
        ChoiceDialog<Chef> dialog = new ChoiceDialog<>();
        dialog.setTitle("Assegna Chef");
        dialog.setHeaderText("Seleziona lo chef per l'evento");
        dialog.setContentText("Chef:");
        
        dialog.getItems().addAll(chefs);
        
        dialog.showAndWait().ifPresent(chef -> {
            try {
                eventoController.assegnaChef(selectedEvento, chef);
                updateEventoDetails();
                showAlert("Successo", "Chef assegnato con successo!");
            } catch (Exception e) {
                showAlert("Errore", e.getMessage());
            }
        });
    }
    
    private void showAddServizioDialog() {
        if (selectedEvento == null) return;
        
        Dialog<Servizio> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Servizio");
        dialog.setHeaderText("Aggiungi un servizio all'evento");
        
        ButtonType addButtonType = new ButtonType("Aggiungi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.setItems(FXCollections.observableArrayList(
            "pranzo", "cena", "buffet", "coffee break", "aperitivo", "colazione"));
        tipoCombo.setValue("pranzo");
        
        Spinner<Integer> oraInizioSpin = new Spinner<>(0, 23, 12);
        Spinner<Integer> minInizioSpin = new Spinner<>(0, 59, 0, 15);
        
        Spinner<Integer> oraFineSpin = new Spinner<>(0, 23, 14);
        Spinner<Integer> minFineSpin = new Spinner<>(0, 59, 0, 15);
        
        Spinner<Integer> personeSpin = new Spinner<>(1, 1000, 50);
        
        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Note servizio");
        noteArea.setPrefRowCount(2);
        
        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(tipoCombo, 1, 0);
        grid.add(new Label("Ora inizio:"), 0, 1);
        grid.add(new HBox(5, oraInizioSpin, new Label(":"), minInizioSpin), 1, 1);
        grid.add(new Label("Ora fine:"), 0, 2);
        grid.add(new HBox(5, oraFineSpin, new Label(":"), minFineSpin), 1, 2);
        grid.add(new Label("Persone:"), 0, 3);
        grid.add(personeSpin, 1, 3);
        grid.add(new Label("Note:"), 0, 4);
        grid.add(noteArea, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String tipo = tipoCombo.getValue();
                    LocalTime inizio = LocalTime.of(oraInizioSpin.getValue(), minInizioSpin.getValue());
                    LocalTime fine = LocalTime.of(oraFineSpin.getValue(), minFineSpin.getValue());
                    int persone = personeSpin.getValue();
                    String note = noteArea.getText().trim();
                    
                    if (fine.isBefore(inizio)) {
                        showAlert("Errore", "L'orario di fine deve essere successivo a quello di inizio");
                        return null;
                    }
                    
                    Servizio servizio = eventoController.createServizio(tipo, inizio, fine, persone);
                    if (!note.isEmpty()) {
                        servizio.setNote(note);
                    }
                    
                    return servizio;
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(servizio -> {
            try {
                eventoController.aggiungiServizio(selectedEvento, servizio);
                updateEventoDetails();
                showAlert("Successo", "Servizio aggiunto con successo!");
            } catch (Exception e) {
                showAlert("Errore", e.getMessage());
            }
        });
    }
    
    private void showAssegnaMenuDialog() {
        if (selectedEvento == null || selectedEvento.getServizi().isEmpty()) return;
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Assegna Menu");
        dialog.setHeaderText("Assegna menu ai servizi");
        
        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        List<Menu> menusDisponibili = menuController.getAllMenusDisponibili();
        
        for (Servizio servizio : selectedEvento.getServizi()) {
            VBox servizioBox = new VBox(10);
            servizioBox.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 1; -fx-padding: 10; -fx-background-radius: 3;");
            
            Label servizioLabel = new Label("Servizio: " + servizio.getTipo() + " (" + 
                servizio.getFasciaOrariaInizio() + " - " + servizio.getFasciaOrariaFine() + ")");
            servizioLabel.setStyle("-fx-font-weight: bold;");
            
            ComboBox<Menu> menuCombo = new ComboBox<>();
            menuCombo.setItems(FXCollections.observableArrayList(menusDisponibili));
            menuCombo.setValue(servizio.getMenu());
            menuCombo.setConverter(new javafx.util.StringConverter<Menu>() {
                @Override
                public String toString(Menu menu) {
                    return menu != null ? menu.getTitolo() : "Nessun menu";
                }
                
                @Override
                public Menu fromString(String string) {
                    return null;
                }
            });
            
            servizioBox.getChildren().addAll(servizioLabel, new Label("Menu:"), menuCombo);
            content.getChildren().add(servizioBox);
            
            // Store reference to combo in servizio for later retrieval
            servizio.setNote(servizio.getNote() + "_COMBO_REF_" + content.getChildren().size());
        }
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        
        dialog.getDialogPane().setContent(scrollPane);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Get all combos and assign menus
                    for (int i = 0; i < selectedEvento.getServizi().size(); i++) {
                        Servizio servizio = selectedEvento.getServizi().get(i);
                        VBox servizioBox = (VBox) content.getChildren().get(i);
                        ComboBox<Menu> combo = (ComboBox<Menu>) servizioBox.getChildren().get(2);
                        Menu selectedMenu = combo.getValue();
                        
                        eventoController.assegnaMenuAServizio(servizio, selectedMenu);
                    }
                    return null;
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            updateEventoDetails();
            showAlert("Successo", "Menu assegnati con successo!");
        });
    }
    
    private void approvaEvento() {
        if (selectedEvento == null) return;
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Approva Menu Evento");
        confirmAlert.setHeaderText("Confermare l'approvazione dei menu?");
        confirmAlert.setContentText("Questa azione renderà i menu non più modificabili.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    eventoController.approvaMenuEvento(selectedEvento);
                    updateEventoDetails();
                    showAlert("Successo", "Menu approvati! Evento pronto per l'esecuzione.");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void completaEvento() {
        if (selectedEvento == null) return;
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Completa Evento");
        dialog.setHeaderText("Inserisci note finali per l'evento");
        dialog.setContentText("Note finali:");
        
        dialog.showAndWait().ifPresent(noteFinali -> {
            try {
                eventoController.completaEvento(selectedEvento, noteFinali);
                updateEventoDetails();
                showAlert("Successo", "Evento completato!");
            } catch (Exception e) {
                showAlert("Errore", e.getMessage());
            }
        });
    }
    
    private void annullaEvento() {
        if (selectedEvento == null) return;
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Annulla Evento");
        dialog.setHeaderText("Inserisci il motivo dell'annullamento");
        dialog.setContentText("Motivo:");
        
        dialog.showAndWait().ifPresent(motivo -> {
            if (!motivo.trim().isEmpty()) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Conferma Annullamento");
                confirmAlert.setHeaderText("Confermare l'annullamento dell'evento?");
                confirmAlert.setContentText("Questa azione non può essere annullata.");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            eventoController.annullaEvento(selectedEvento, motivo);
                            updateEventoDetails();
                            showAlert("Successo", "Evento annullato.");
                        } catch (Exception e) {
                            showAlert("Errore", e.getMessage());
                        }
                    }
                });
            }
        });
    }
    
    private void deleteEvento() {
        if (selectedEvento == null) return;
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Elimina Evento");
        confirmAlert.setHeaderText("Eliminare l'evento selezionato?");
        confirmAlert.setContentText("Questa azione non può essere annullata.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    eventoController.deleteEvento(selectedEvento);
                    loadEventi();
                    selectedEvento = null;
                    updateEventoDetails();
                    showAlert("Successo", "Evento eliminato!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void showClientiDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Gestione Clienti");
        dialog.setHeaderText("Gestisci i clienti");
        
        ButtonType closeButtonType = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);
        
        // Buttons
        HBox buttonsBox = new HBox(10);
        Button addClienteButton = new Button("Nuovo Cliente");
        addClienteButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        addClienteButton.setOnAction(e -> showAddClienteDialog(dialog));
        buttonsBox.getChildren().add(addClienteButton);
        
        // Table
        TableView<Cliente> clientiTable = new TableView<>();
        
        TableColumn<Cliente, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));
        nomeCol.setPrefWidth(200);
        
        TableColumn<Cliente, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTipo()));
        tipoCol.setPrefWidth(100);
        
        TableColumn<Cliente, String> contattiCol = new TableColumn<>("Contatti");
        contattiCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getContatti()));
        contattiCol.setPrefWidth(180);
        
        clientiTable.getColumns().addAll(nomeCol, tipoCol, contattiCol);
        
        List<Cliente> clienti = eventoController.getAllClienti();
        clientiTable.setItems(FXCollections.observableArrayList(clienti));
        
        content.getChildren().addAll(buttonsBox, clientiTable);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    private void showAddClienteDialog(Dialog<?> parentDialog) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Cliente");
        dialog.setHeaderText("Crea un nuovo cliente");
        
        ButtonType createButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome cliente");
        
        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.setItems(FXCollections.observableArrayList("privato", "azienda"));
        tipoCombo.setValue("privato");
        
        TextField contattiField = new TextField();
        contattiField.setPromptText("Contatti");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        TextField telefonoField = new TextField();
        telefonoField.setPromptText("Telefono");
        
        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Tipo:"), 0, 1);
        grid.add(tipoCombo, 1, 1);
        grid.add(new Label("Contatti:"), 0, 2);
        grid.add(contattiField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Telefono:"), 0, 4);
        grid.add(telefonoField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String nome = nomeField.getText().trim();
                    String tipo = tipoCombo.getValue();
                    String contatti = contattiField.getText().trim();
                    String email = emailField.getText().trim();
                    String telefono = telefonoField.getText().trim();
                    
                    if (nome.isEmpty() || contatti.isEmpty()) {
                        showAlert("Errore", "Nome e contatti sono obbligatori");
                        return null;
                    }
                    
                    Cliente cliente = eventoController.createCliente(nome, tipo, contatti);
                    cliente.setEmail(email);
                    cliente.setTelefono(telefono);
                    
                    return cliente;
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(cliente -> {
            showAlert("Successo", "Cliente creato con successo!");
            parentDialog.close();
            showClientiDialog(); // Refresh
        });
    }
    
    private void showStatistiche() {
        try {
            Map<String, Object> stats = eventoController.getStatisticheEventi();
            
            Alert statsAlert = new Alert(Alert.AlertType.INFORMATION);
            statsAlert.setTitle("Statistiche Eventi");
            statsAlert.setHeaderText("Statistiche del sistema eventi");
            
            StringBuilder content = new StringBuilder();
            content.append("Eventi totali: ").append(stats.get("totaleEventi")).append("\n");
            content.append("Eventi in corso: ").append(stats.get("eventiInCorso")).append("\n");
            content.append("Eventi approvati: ").append(stats.get("eventiApprovati")).append("\n");
            content.append("Eventi completati: ").append(stats.get("eventiCompletati")).append("\n");
            content.append("Eventi mese corrente: ").append(stats.get("eventiMeseCorrente")).append("\n\n");
            
            @SuppressWarnings("unchecked")
            Map<String, Long> eventiPerTipo = (Map<String, Long>) stats.get("eventiPerTipo");
            if (eventiPerTipo != null && !eventiPerTipo.isEmpty()) {
                content.append("Eventi per tipo:\n");
                eventiPerTipo.forEach((tipo, count) -> 
                    content.append("• ").append(tipo).append(": ").append(count).append("\n"));
            }
            
            statsAlert.setContentText(content.toString());
            statsAlert.showAndWait();
        } catch (Exception e) {
            showAlert("Errore", "Impossibile caricare le statistiche: " + e.getMessage());
        }
    }
    
    private String getColorForStato(StatoEvento stato) {
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
    
    // Custom ListCell for better event display
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
                
                Label nameLabel = new Label(evento.getNome());
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                
                Label dateLabel = new Label(evento.getDataInizio().toString() + 
                    (evento.getDataFine().equals(evento.getDataInizio()) ? "" : " - " + evento.getDataFine()));
                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                
                Label statusLabel = new Label(evento.getStato().toString());
                statusLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + 
                    getColorForStato(evento.getStato()) + ";");
                
                content.getChildren().addAll(nameLabel, dateLabel, statusLabel);
                
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
        
        private String getColorForStato(StatoEvento stato) {
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