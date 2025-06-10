package com.catring.controller;

import com.catring.model.*;
import com.catring.model.Menu;
import com.catring.observer.MenuObserver;
import com.catring.singleton.MenuService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * PATTERN GRASP: CONTROLLER
 * Questa classe implementa il pattern Controller del GRASP.
 * Gestisce le interazioni dell'utente e coordina le operazioni tra la vista e il modello.
 */
public class MenuController implements Initializable, MenuObserver {
    
    // Controlli JavaFX per la gestione eventi
    @FXML private ListView<Evento> eventiListView;
    @FXML private TextArea dettagliEventoArea;
    
    // Controlli JavaFX per la gestione menu
    @FXML private TextField nomeMenuField;
    @FXML private TextField descrizioneMenuField;
    @FXML private TextArea noteMenuArea;
    @FXML private TableView<Menu> menuTableView;
    @FXML private TableColumn<Menu, String> nomeMenuColumn;
    @FXML private TableColumn<Menu, String> descrizioneMenuColumn;
    
    // Controlli per gestione sezioni e ricette
    @FXML private TextField titoloSezioneField;
    @FXML private ComboBox<String> sezioniComboBox;
    @FXML private ComboBox<Ricetta> ricetteComboBox;
    @FXML private ListView<SezioniMenu> sezioniListView;
    @FXML private ListView<VoceMenu> vociListView;
    
    // Controlli per gestione ricette
    @FXML private TextField nomeRicettaField;
    @FXML private TextField descrizioneRicettaField;
    @FXML private TextField tempoPreparazioneField;
    @FXML private TextField autoreRicettaField;
    @FXML private ComboBox<String> statoRicettaComboBox;
    
    // Altri controlli
    @FXML private TextField nuovoTitoloField;
    @FXML private TextArea nuoveNoteArea;
    @FXML private Label statusLabel;
    
    // Pattern utilizzati
    private MenuService menuService; // Singleton
    private Menu menuSelezionato;
    
    // Liste observable (implementano Observer internamente)
    private ObservableList<Evento> eventiList;
    private ObservableList<Menu> menuList;
    private ObservableList<SezioniMenu> sezioniList;
    private ObservableList<VoceMenu> vociList;
    private ObservableList<Ricetta> ricetteList;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Utilizzo del pattern Singleton
        menuService = MenuService.getInstance();
        
        // Registrazione come observer
        menuService.addObserver(this);
        
        initializeComponents();
        setupEventListeners();
        loadInitialData();
    }
    
    /**
     * Inizializza i componenti dell'interfaccia
     */
    private void initializeComponents() {
        // Inizializza le liste observable
        eventiList = FXCollections.observableArrayList();
        menuList = FXCollections.observableArrayList();
        sezioniList = FXCollections.observableArrayList();
        vociList = FXCollections.observableArrayList();
        ricetteList = FXCollections.observableArrayList();
        
        // Configura la tabella
        nomeMenuColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        descrizioneMenuColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescrizione()));
        
        // Assegna le liste ai controlli
        eventiListView.setItems(eventiList);
        menuTableView.setItems(menuList);
        sezioniListView.setItems(sezioniList);
        vociListView.setItems(vociList);
        ricetteComboBox.setItems(ricetteList);
        
        // Configura combo box stati ricetta
        if (statoRicettaComboBox != null) {
            statoRicettaComboBox.setItems(FXCollections.observableArrayList("bozza", "pubblicata"));
            statoRicettaComboBox.setValue("bozza");
        }
        
        // Configura cell factory per ricette
        setupRicetteCellFactory();
    }
    
    /**
     * Configura gli event listeners (pattern Observer implicito)
     */
    private void setupEventListeners() {
        // Listener per selezione evento
        eventiListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && dettagliEventoArea != null) {
                String dettagli = menuService.getDettagliEvento(newSelection.getId());
                dettagliEventoArea.setText(dettagli);
            }
        });
        
        // Listener per selezione menu
        menuTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                menuSelezionato = newSelection;
                aggiornaSezioniMenu();
                aggiornaComboBoxSezioni();
            }
        });
        
        // Listener per selezione sezione
        sezioniListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                aggiornaVociSezione(newSelection);
            }
        });
    }
    
    /**
     * Carica i dati iniziali
     */
    private void loadInitialData() {
        aggiornaEventi();
        aggiornaMenu();
        aggiornaRicette();
    }
    
    // ========================================
    // METODI DEL SEQUENCE DIAGRAM
    // ========================================
    
    @FXML
    private void handleConsultaEvento() {
        aggiornaEventi();
        mostraMessaggio("Info", "Eventi caricati: " + eventiList.size());
    }
    
    @FXML
    private void handleCreaMenu() {
        String nome = getNomeMenuInput();
        String descrizione = getDescrizioneMenuInput();
        String note = getNoteMenuInput();
        
        if (!isValidMenuName(nome)) {
            mostraMessaggio("Errore", "Il nome del menu è obbligatorio");
            return;
        }
        
        Menu nuovoMenu = menuService.creaMenu(nome, descrizione, note);
        menuList.add(nuovoMenu);
        clearMenuInputs();
        mostraMessaggio("Successo", "Menu creato: " + nuovoMenu.getId());
    }
    
    @FXML
    private void handleSelezionaMenu() {
        if (menuSelezionato != null) {
            String dettagli = menuService.getDettagliMenu(menuSelezionato);
            mostraMessaggio("Dettagli Menu", dettagli);
        } else {
            mostraMessaggio("Errore", "Nessun menu selezionato");
        }
    }
    
    @FXML
    private void handleAggiungiSezione() {
        if (!isMenuSelected()) return;
        
        String titolo = getTitoloSezioneInput();
        if (!isValidSectionTitle(titolo)) {
            mostraMessaggio("Errore", "Il titolo della sezione è obbligatorio");
            return;
        }
        
        menuService.definisciSezioni(menuSelezionato, titolo);
        aggiornaSezioniMenu();
        aggiornaComboBoxSezioni();
        clearSezioneInput();
        mostraMessaggio("Successo", "Sezione aggiunta: " + titolo);
    }
    
    @FXML
    private void handleConsultaRicettario() {
        aggiornaRicette();
        mostraMessaggio("Info", "Ricette caricate: " + ricetteList.size());
    }
    
    @FXML
    private void handleInserisciRicetta() {
        String nome = getNomeRicettaInput();
        String descrizione = getDescrizioneRicettaInput();
        String tempoStr = getTempoPreparazioneInput();
        String autore = getAutoreRicettaInput();
        String stato = getStatoRicettaInput();
        
        if (!isValidRicettaName(nome)) {
            mostraMessaggio("Errore", "Il nome della ricetta è obbligatorio");
            return;
        }
        
        int tempo = parseTempoPreparazione(tempoStr);
        if (tempo < 0) {
            mostraMessaggio("Errore", "Tempo preparazione deve essere un numero valido");
            return;
        }
        
        Ricetta nuovaRicetta = menuService.inserisciRicetta(nome, descrizione, tempo, stato, autore);
        ricetteList.add(nuovaRicetta);
        clearRicettaInputs();
        mostraMessaggio("Successo", "Ricetta aggiunta: " + nuovaRicetta.getNome());
    }
    
    @FXML
    private void handleAggiungiRicetta() {
        if (!isMenuSelected()) return;
        
        String sezioneSelezionata = getSezioneSelezionata();
        Ricetta ricettaSelezionata = getRicettaSelezionata();
        
        if (sezioneSelezionata == null) {
            mostraMessaggio("Errore", "Seleziona una sezione");
            return;
        }
        
        if (ricettaSelezionata == null) {
            mostraMessaggio("Errore", "Seleziona una ricetta");
            return;
        }
        
        menuService.aggiungiRicettaASezione(menuSelezionato, sezioneSelezionata, ricettaSelezionata);
        aggiornaSezioniMenu();
        mostraMessaggio("Successo", "Ricetta aggiunta alla sezione");
    }
    
    @FXML
    private void handleEliminaRicetta() {
        if (!isMenuSelected()) return;
        
        VoceMenu voceSelezionata = getVoceSelezionata();
        if (voceSelezionata == null) {
            mostraMessaggio("Errore", "Seleziona una ricetta da eliminare");
            return;
        }
        
        menuService.eliminaRicetta(menuSelezionato, voceSelezionata.getRicetta());
        aggiornaSezioniMenu();
        mostraMessaggio("Successo", "Ricetta eliminata dal menu");
    }
    
    @FXML
    private void handleSpostaRicetta() {
        if (!isMenuSelected()) return;
        
        VoceMenu voceSelezionata = getVoceSelezionata();
        String nuovaSezione = getSezioneSelezionata();
        
        if (voceSelezionata == null) {
            mostraMessaggio("Errore", "Seleziona una ricetta da spostare");
            return;
        }
        
        if (nuovaSezione == null) {
            mostraMessaggio("Errore", "Seleziona la sezione di destinazione");
            return;
        }
        
        String ricettaId = voceSelezionata.getRicetta().getId();
        menuService.spostaRicetta(menuSelezionato.getId(), ricettaId, nuovaSezione);
        aggiornaSezioniMenu();
        mostraMessaggio("Successo", "Ricetta spostata nella sezione: " + nuovaSezione);
    }
    
    @FXML
    private void handleAggiornaTitolo() {
        if (!isMenuSelected()) return;
        
        String nuovoTitolo = getNuovoTitoloInput();
        if (!isValidMenuName(nuovoTitolo)) {
            mostraMessaggio("Errore", "Inserisci un nuovo titolo valido");
            return;
        }
        
        menuService.aggiornaTitolo(menuSelezionato, nuovoTitolo);
        menuTableView.refresh();
        clearNuovoTitoloInput();
        mostraMessaggio("Successo", "Titolo aggiornato: " + nuovoTitolo);
    }
    
    @FXML
    private void handleAggiungiAnnotazione() {
        if (!isMenuSelected()) return;
        
        String nuoveNote = getNuoveNoteInput();
        if (nuoveNote.trim().isEmpty()) {
            mostraMessaggio("Errore", "Inserisci delle note");
            return;
        }
        
        menuService.aggiungiAnnotazione(menuSelezionato, nuoveNote);
        clearNuoveNoteInput();
        mostraMessaggio("Successo", "Annotazione aggiunta al menu");
    }
    
    @FXML
    private void handleGeneraPDF() {
        if (!isMenuSelected()) return;
        
        String pdfLink = menuService.generaPDF(menuSelezionato);
        mostraMessaggio("PDF Generato", "Link PDF: " + pdfLink);
    }
    
    @FXML
    private void handlePubblicaBacheca() {
        if (!isMenuSelected()) return;
        
        String linkBacheca = menuService.pubblicaSuBacheca(menuSelezionato);
        mostraMessaggio("Pubblicato", "Link bacheca: " + linkBacheca);
    }
    
    @FXML
    private void handleEliminaMenu() {
        if (!isMenuSelected()) return;
        
        if (confermaEliminazione()) {
            boolean eliminato = menuService.eliminaMenu(menuSelezionato);
            if (eliminato) {
                menuList.remove(menuSelezionato);
                resetMenuSelection();
                mostraMessaggio("Successo", "Menu eliminato con successo");
            } else {
                mostraMessaggio("Errore", "Impossibile eliminare il menu");
            }
        }
    }
    
    // ========================================
    // IMPLEMENTAZIONE PATTERN OBSERVER
    // ========================================
    
    @Override
    public void onMenuCreated(Menu menu) {
        if (!menuList.contains(menu)) {
            menuList.add(menu);
        }
    }
    
    @Override
    public void onMenuUpdated(Menu menu) {
        menuTableView.refresh();
    }
    
    @Override
    public void onMenuDeleted(Menu menu) {
        menuList.remove(menu);
    }
    
    // ========================================
    // METODI DI SUPPORTO (Information Expert)
    // ========================================
    
    private void setupRicetteCellFactory() {
        ricetteComboBox.setCellFactory(lv -> new ListCell<Ricetta>() {
            @Override
            protected void updateItem(Ricetta ricetta, boolean empty) {
                super.updateItem(ricetta, empty);
                setText(empty || ricetta == null ? null : ricetta.getNome());
            }
        });
        
        ricetteComboBox.setButtonCell(new ListCell<Ricetta>() {
            @Override
            protected void updateItem(Ricetta ricetta, boolean empty) {
                super.updateItem(ricetta, empty);
                setText(empty || ricetta == null ? null : ricetta.getNome());
            }
        });
    }
    
    private void aggiornaEventi() {
        eventiList.clear();
        eventiList.addAll(menuService.consultaEventi());
    }
    
    private void aggiornaMenu() {
        menuList.clear();
        menuList.addAll(menuService.getMenus());
    }
    
    private void aggiornaRicette() {
        ricetteList.clear();
        ricetteList.addAll(menuService.consultaRicettario());
    }
    
    private void aggiornaSezioniMenu() {
        sezioniList.clear();
        if (menuSelezionato != null) {
            sezioniList.addAll(menuSelezionato.getSezioni());
        }
    }
    
    private void aggiornaVociSezione(SezioniMenu sezione) {
        vociList.clear();
        if (sezione != null) {
            vociList.addAll(sezione.getVoci());
        }
    }
    
    private void aggiornaComboBoxSezioni() {
        sezioniComboBox.getItems().clear();
        if (menuSelezionato != null) {
            for (SezioniMenu sezione : menuSelezionato.getSezioni()) {
                sezioniComboBox.getItems().add(sezione.getTitolo());
            }
        }
    }
    
    // Metodi per ottenere input dai controlli
    private String getNomeMenuInput() {
        return nomeMenuField != null ? nomeMenuField.getText().trim() : "";
    }
    
    private String getDescrizioneMenuInput() {
        return descrizioneMenuField != null ? descrizioneMenuField.getText().trim() : "";
    }
    
    private String getNoteMenuInput() {
        return noteMenuArea != null ? noteMenuArea.getText().trim() : "";
    }
    
    private String getTitoloSezioneInput() {
        return titoloSezioneField != null ? titoloSezioneField.getText().trim() : "";
    }
    
    private String getNomeRicettaInput() {
        return nomeRicettaField != null ? nomeRicettaField.getText().trim() : "";
    }
    
    private String getDescrizioneRicettaInput() {
        return descrizioneRicettaField != null ? descrizioneRicettaField.getText().trim() : "";
    }
    
    private String getTempoPreparazioneInput() {
        return tempoPreparazioneField != null ? tempoPreparazioneField.getText().trim() : "0";
    }
    
    private String getAutoreRicettaInput() {
        return autoreRicettaField != null ? autoreRicettaField.getText().trim() : "";
    }
    
    private String getStatoRicettaInput() {
        return statoRicettaComboBox != null ? statoRicettaComboBox.getValue() : "bozza";
    }
    
    private String getNuovoTitoloInput() {
        return nuovoTitoloField != null ? nuovoTitoloField.getText().trim() : "";
    }
    
    private String getNuoveNoteInput() {
        return nuoveNoteArea != null ? nuoveNoteArea.getText().trim() : "";
    }
    
    private String getSezioneSelezionata() {
        return sezioniComboBox != null ? sezioniComboBox.getValue() : null;
    }
    
    private Ricetta getRicettaSelezionata() {
        return ricetteComboBox != null ? ricetteComboBox.getValue() : null;
    }
    
    private VoceMenu getVoceSelezionata() {
        return vociListView != null ? vociListView.getSelectionModel().getSelectedItem() : null;
    }
    
    // Metodi di validazione
    private boolean isValidMenuName(String nome) {
        return nome != null && !nome.trim().isEmpty();
    }
    
    private boolean isValidSectionTitle(String titolo) {
        return titolo != null && !titolo.trim().isEmpty();
    }
    
    private boolean isValidRicettaName(String nome) {
        return nome != null && !nome.trim().isEmpty();
    }
    
    private boolean isMenuSelected() {
        if (menuSelezionato == null) {
            mostraMessaggio("Errore", "Seleziona prima un menu");
            return false;
        }
        return true;
    }
    
    private int parseTempoPreparazione(String tempoStr) {
        try {
            return Integer.parseInt(tempoStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private boolean confermaEliminazione() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare il menu selezionato?");
        alert.setContentText("Questa azione non può essere annullata");
        return alert.showAndWait().get() == ButtonType.OK;
    }
    
    // Metodi per pulire i campi
    private void clearMenuInputs() {
        if (nomeMenuField != null) nomeMenuField.clear();
        if (descrizioneMenuField != null) descrizioneMenuField.clear();
        if (noteMenuArea != null) noteMenuArea.clear();
    }
    
    private void clearSezioneInput() {
        if (titoloSezioneField != null) titoloSezioneField.clear();
    }
    
    private void clearRicettaInputs() {
        if (nomeRicettaField != null) nomeRicettaField.clear();
        if (descrizioneRicettaField != null) descrizioneRicettaField.clear();
        if (tempoPreparazioneField != null) tempoPreparazioneField.clear();
        if (autoreRicettaField != null) autoreRicettaField.clear();
        if (statoRicettaComboBox != null) statoRicettaComboBox.setValue("bozza");
    }
    
    private void clearNuovoTitoloInput() {
        if (nuovoTitoloField != null) nuovoTitoloField.clear();
    }
    
    private void clearNuoveNoteInput() {
        if (nuoveNoteArea != null) nuoveNoteArea.clear();
    }
    
    private void resetMenuSelection() {
        menuSelezionato = null;
        sezioniList.clear();
        vociList.clear();
        sezioniComboBox.getItems().clear();
    }
    
    private void mostraMessaggio(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
        
        if (statusLabel != null) {
            statusLabel.setText(messaggio);
        }
    }
}