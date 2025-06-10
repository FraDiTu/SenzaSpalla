package com.catering.controller;

import com.catering.model.domain.*;
import com.catering.model.domain.Menu;
import com.catering.model.observer.MenuObserver;
import com.catering.model.strategy.PDFExportStrategy;
import com.catering.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Controller pattern - coordina le interazioni dell'interfaccia utente
 * Observer pattern - osserva i cambiamenti del menu
 * Low Coupling - dipende solo dai servizi necessari
 */
public class MainController implements Initializable, MenuObserver {
    
    // Services - Information Expert pattern
    private final MenuService menuService = MenuService.getInstance();
    private final RicettarioService ricettarioService = RicettarioService.getInstance();
    private final EventoService eventoService = EventoService.getInstance();
    
    // UI Components
    @FXML private TextField titoloField;
    @FXML private ComboBox<Evento> eventoComboBox;
    @FXML private ListView<Menu> menuListView;
    @FXML private TreeView<String> menuStructureTree;
    @FXML private TableView<Ricetta> ricetteTable;
    @FXML private TableColumn<Ricetta, String> nomeRicettaColumn;
    @FXML private TableColumn<Ricetta, String> descrizioneRicettaColumn;
    @FXML private TextField ricettaSearchField;
    @FXML private TextField sezioneField;
    @FXML private TextField nomeMenuField;
    @FXML private Button createMenuButton;
    @FXML private Button addSezioneButton;
    @FXML private Button addRicettaButton;
    @FXML private Button publishButton;
    @FXML private Button exportPDFButton;
    @FXML private Label statusLabel;
    @FXML private TextArea eventoInfoArea;
    
    // State
    private final ObservableList<Menu> menus = FXCollections.observableArrayList();
    private final ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
    private final ObservableList<Evento> eventi = FXCollections.observableArrayList();
    private Menu currentMenu;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadData();
        setupEventHandlers();
        
        // Observer pattern - registra questo controller come osservatore
        menuService.addObserver(this);
    }
    
    /**
     * High Cohesion - tutte le operazioni di setup UI insieme
     */
    private void setupUI() {
        // Setup tables
        nomeRicettaColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        descrizioneRicettaColumn.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        
        // Setup ComboBox
        eventoComboBox.setItems(eventi);
        eventoComboBox.setConverter(new javafx.util.StringConverter<Evento>() {
            @Override
            public String toString(Evento evento) {
                return evento != null ? evento.getNome() : "";
            }
            
            @Override
            public Evento fromString(String string) {
                return eventi.stream()
                    .filter(e -> e.getNome().equals(string))
                    .findFirst().orElse(null);
            }
        });
        
        // Setup ListView
        menuListView.setItems(menus);
        menuListView.setCellFactory(listView -> new ListCell<Menu>() {
            @Override
            protected void updateItem(Menu menu, boolean empty) {
                super.updateItem(menu, empty);
                if (empty || menu == null) {
                    setText(null);
                } else {
                    setText(menu.getTitolo() + (menu.isPubblicato() ? " [PUBBLICATO]" : " [BOZZA]"));
                }
            }
        });
        
        // Setup TableView
        ricetteTable.setItems(ricette);
        
        // Initial state
        statusLabel.setText("Pronto");
        updateButtonStates();
    }
    
    /**
     * Information Expert - sa come caricare i propri dati
     */
    private void loadData() {
        eventi.setAll(eventoService.getAllEventi());
        ricette.setAll(ricettarioService.getAllRicette());
        menus.setAll(menuService.getAllMenus());
    }
    
    /**
     * High Cohesion - setup di tutti gli event handler
     */
    private void setupEventHandlers() {
        // Menu selection
        menuListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldMenu, newMenu) -> {
                currentMenu = newMenu;
                updateMenuDisplay();
                updateButtonStates();
            }
        );
        
        // Evento selection
        eventoComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldEvento, newEvento) -> updateEventoInfo(newEvento)
        );
        
        // Ricetta search
        ricettaSearchField.textProperty().addListener(
            (obs, oldText, newText) -> searchRicette(newText)
        );
        
        // Double click to add ricetta
        ricetteTable.setRowFactory(tv -> {
            TableRow<Ricetta> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    addRicettaToCurrentMenu();
                }
            });
            return row;
        });
    }
    
    /**
     * Controller pattern - gestisce la creazione del menu
     */
    @FXML
    private void createMenu() {
        String titolo = titoloField.getText();
        Evento selectedEvento = eventoComboBox.getSelectionModel().getSelectedItem();
        
        if (titolo == null || titolo.trim().isEmpty()) {
            showAlert("Errore", "Il titolo del menu è obbligatorio");
            return;
        }
        
        if (selectedEvento == null) {
            showAlert("Errore", "Seleziona un evento");
            return;
        }
        
        try {
            Menu newMenu = menuService.createMenu(titolo.trim(), selectedEvento.getId());
            statusLabel.setText("Menu creato: " + newMenu.getTitolo());
            titoloField.clear();
            
            // Seleziona il nuovo menu
            menuListView.getSelectionModel().select(newMenu);
            
        } catch (Exception e) {
            showAlert("Errore", "Errore nella creazione del menu: " + e.getMessage());
        }
    }
    
    /**
     * Controller pattern - gestisce l'aggiunta di sezioni
     */
    @FXML
    private void addSezione() {
        if (currentMenu == null) {
            showAlert("Errore", "Seleziona un menu");
            return;
        }
        
        String nomeSezione = sezioneField.getText();
        if (nomeSezione == null || nomeSezione.trim().isEmpty()) {
            showAlert("Errore", "Inserisci il nome della sezione");
            return;
        }
        
        try {
            menuService.addSezione(currentMenu.getId(), nomeSezione.trim());
            sezioneField.clear();
            statusLabel.setText("Sezione aggiunta: " + nomeSezione);
        } catch (Exception e) {
            showAlert("Errore", "Errore nell'aggiunta della sezione: " + e.getMessage());
        }
    }
    
    /**
     * Controller pattern - gestisce l'aggiunta di ricette al menu
     */
    @FXML
    private void addRicettaToMenu() {
        addRicettaToCurrentMenu();
    }
    
    private void addRicettaToCurrentMenu() {
        if (currentMenu == null) {
            showAlert("Errore", "Seleziona un menu");
            return;
        }
        
        Ricetta selectedRicetta = ricetteTable.getSelectionModel().getSelectedItem();
        if (selectedRicetta == null) {
            showAlert("Errore", "Seleziona una ricetta");
            return;
        }
        
        if (currentMenu.getSezioni().isEmpty()) {
            showAlert("Errore", "Aggiungi prima una sezione al menu");
            return;
        }
        
        // Chiedi all'utente di scegliere la sezione
        String sezione = chooseSezione();
        if (sezione == null) return;
        
        // Chiedi il nome da visualizzare nel menu
        String nomeMenu = askNomeMenu(selectedRicetta.getNome());
        if (nomeMenu == null) return;
        
        try {
            menuService.addVoceMenu(currentMenu.getId(), selectedRicetta.getId(), nomeMenu, sezione);
            statusLabel.setText("Ricetta aggiunta al menu: " + nomeMenu);
        } catch (Exception e) {
            showAlert("Errore", "Errore nell'aggiunta della ricetta: " + e.getMessage());
        }
    }
    
    /**
     * Controller pattern - gestisce la pubblicazione del menu
     */
    @FXML
    private void publishMenu() {
        if (currentMenu == null) {
            showAlert("Errore", "Seleziona un menu");
            return;
        }
        
        try {
            menuService.publishMenu(currentMenu.getId());
            statusLabel.setText("Menu pubblicato: " + currentMenu.getTitolo());
        } catch (Exception e) {
            showAlert("Errore", "Errore nella pubblicazione: " + e.getMessage());
        }
    }
    
    /**
     * Controller pattern - gestisce l'esportazione PDF
     */
    @FXML
    private void exportPDF() {
        if (currentMenu == null) {
            showAlert("Errore", "Seleziona un menu");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva PDF");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName(currentMenu.getTitolo() + ".pdf");
        
        File file = fileChooser.showSaveDialog(exportPDFButton.getScene().getWindow());
        if (file != null) {
            try {
                String basePath = file.getAbsolutePath().replaceAll("\\.pdf$", "");
                menuService.exportMenu(currentMenu.getId(), basePath, new PDFExportStrategy());
                statusLabel.setText("PDF esportato: " + file.getName());
            } catch (IOException e) {
                showAlert("Errore", "Errore nell'esportazione PDF: " + e.getMessage());
            }
        }
    }
    
    /**
     * Information Expert - sa come aggiornare la visualizzazione del menu
     */
    private void updateMenuDisplay() {
        if (currentMenu == null) {
            menuStructureTree.setRoot(null);
            eventoInfoArea.clear();
            return;
        }
        
        // Aggiorna tree view del menu
        TreeItem<String> root = new TreeItem<>(currentMenu.getTitolo());
        root.setExpanded(true);
        
        for (Map.Entry<String, List<VoceMenu>> entry : currentMenu.getSezioni().entrySet()) {
            TreeItem<String> sezioneItem = new TreeItem<>(entry.getKey());
            sezioneItem.setExpanded(true);
            
            for (VoceMenu voce : entry.getValue()) {
                TreeItem<String> voceItem = new TreeItem<>(voce.getNomeVisualizzato());
                sezioneItem.getChildren().add(voceItem);
            }
            
            root.getChildren().add(sezioneItem);
        }
        
        menuStructureTree.setRoot(root);
        
        // Aggiorna info evento
        eventoService.getEventoById(currentMenu.getEventoId())
            .ifPresent(this::updateEventoInfo);
    }
    
    private void updateEventoInfo(Evento evento) {
        if (evento == null) {
            eventoInfoArea.clear();
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Nome: ").append(evento.getNome()).append("\n");
        info.append("Data: ").append(evento.getDataInizio());
        if (!evento.getDataInizio().equals(evento.getDataFine())) {
            info.append(" - ").append(evento.getDataFine());
        }
        info.append("\n");
        info.append("Luogo: ").append(evento.getLuogo() != null ? evento.getLuogo() : "Non specificato").append("\n");
        info.append("Tipo: ").append(evento.getTipo() != null ? evento.getTipo() : "Non specificato").append("\n");
        info.append("Durata: ").append(evento.getDurataGiorni()).append(" giorni");
        
        eventoInfoArea.setText(info.toString());
    }
    
    private void updateButtonStates() {
        boolean hasMenu = currentMenu != null;
        boolean canEdit = hasMenu && !currentMenu.isPubblicato();
        
        addSezioneButton.setDisable(!canEdit);
        addRicettaButton.setDisable(!canEdit);
        publishButton.setDisable(!canEdit);
        exportPDFButton.setDisable(!hasMenu);
    }
    
    private void searchRicette(String query) {
        ricette.setAll(ricettarioService.searchRicette(query));
    }
    
    private String chooseSezione() {
        if (currentMenu == null || currentMenu.getSezioni().isEmpty()) {
            return null;
        }
        
        List<String> sezioni = new ArrayList<>(currentMenu.getSezioni().keySet());
        ChoiceDialog<String> dialog = new ChoiceDialog<>(sezioni.get(0), sezioni);
        dialog.setTitle("Selezione Sezione");
        dialog.setHeaderText("Scegli la sezione per la ricetta");
        dialog.setContentText("Sezione:");
        
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    private String askNomeMenu(String nomeOriginale) {
        TextInputDialog dialog = new TextInputDialog(nomeOriginale);
        dialog.setTitle("Nome nel Menu");
        dialog.setHeaderText("Personalizza il nome della ricetta nel menu");
        dialog.setContentText("Nome:");
        
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Observer pattern implementation
    @Override
    public void onMenuChanged(Menu menu) {
        javafx.application.Platform.runLater(() -> {
            // Aggiorna la lista dei menu
            int index = menus.indexOf(menu);
            if (index >= 0) {
                menus.set(index, menu);
            } else {
                menus.add(menu);
            }
            
            // Se è il menu corrente, aggiorna la visualizzazione
            if (currentMenu != null && currentMenu.getId().equals(menu.getId())) {
                currentMenu = menu;
                updateMenuDisplay();
                updateButtonStates();
            }
        });
    }
    
    @Override
    public void onMenuPublished(Menu menu) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText("Menu pubblicato: " + menu.getTitolo());
        });
    }
    
    @Override
    public void onMenuSectionAdded(String sectionName) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText("Sezione aggiunta: " + sectionName);
        });
    }
    
    @Override
    public void onMenuItemAdded(String section, String itemName) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText("Aggiunto a " + section + ": " + itemName);
        });
    }
}