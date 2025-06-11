package com.catring.controller;

import com.catring.model.*;
import com.catring.model.Menu;
import com.catring.observer.MenuObserver;
import com.catring.singleton.MenuService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * PATTERN GRASP: CONTROLLER
 * Controller aggiornato per la gestione completa dei menu con nuove funzionalità
 */
public class MenuController implements MenuObserver {
    
    // Pattern Singleton
    private MenuService menuService;
    private Menu menuSelezionato;
    
    // Liste observable
    private ObservableList<Menu> menuList;
    private ObservableList<Menu> menuPubblicatiList;
    private ObservableList<SezioniMenu> sezioniList;
    private ObservableList<VoceMenu> vociList;
    private ObservableList<Ricetta> ricetteList;
    private ObservableList<Ricetta> tutteRicetteList;
    
    // Componenti dell'interfaccia (collegati dalle view)
    // Componenti Menu
    private TextField campoNomeMenu;
    private TextField campoDescrizioneMenu;
    private TextArea areaNoteMenu;
    private TextField campoTitoloSezione;
    private ComboBox<String> comboSezioni;
    private ComboBox<Ricetta> comboRicette;
    private TableView<Menu> tabellaMenu;
    private ListView<SezioniMenu> listaSezioni;
    private ListView<VoceMenu> listaVoci;
    
    // Componenti Ricettario
    private TextField campoNomeRicetta;
    private TextField campoDescrizioneRicetta;
    private TextField campoTempoPreparazione;
    private TextField campoAutoreRicetta;
    private ComboBox<String> comboStatoRicetta;
    private ListView<Ricetta> listaRicette;
    
    // Componenti Finale
    private TextField campoNuovoTitolo;
    private TextArea areaNuoveNote;
    private Label labelStato;
    
    // Componenti Bacheca
    private ListView<Menu> listaMenuPubblicati;
    private TextArea areaDettagliMenuBacheca;
    private Label labelStatoBacheca;
    
    public MenuController() {
        this.menuService = MenuService.getInstance();
        this.menuService.addObserver(this);
        
        // Inizializza le liste
        this.menuList = FXCollections.observableArrayList();
        this.menuPubblicatiList = FXCollections.observableArrayList();
        this.sezioniList = FXCollections.observableArrayList();
        this.vociList = FXCollections.observableArrayList();
        this.ricetteList = FXCollections.observableArrayList();
        this.tutteRicetteList = FXCollections.observableArrayList();
        
        caricaDatiIniziali();
    }
    
    // ========================================
    // METODI PER COLLEGARE LE VIEW
    // ========================================
    
    /**
     * Imposta i componenti della MenuView
     */
    public void setComponentiMenu(TextField nomeMenu, TextField descrizioneMenu, TextArea noteMenu,
                                 TextField titoloSezione, ComboBox<String> sezioni, ComboBox<Ricetta> ricette,
                                 TableView<Menu> tabella, ListView<SezioniMenu> sezioniLista, ListView<VoceMenu> vociLista) {
        this.campoNomeMenu = nomeMenu;
        this.campoDescrizioneMenu = descrizioneMenu;
        this.areaNoteMenu = noteMenu;
        this.campoTitoloSezione = titoloSezione;
        this.comboSezioni = sezioni;
        this.comboRicette = ricette;
        this.tabellaMenu = tabella;
        this.listaSezioni = sezioniLista;
        this.listaVoci = vociLista;
        
        // Collega le liste
        if (tabella != null) tabella.setItems(menuList);
        if (sezioniLista != null) sezioniLista.setItems(sezioniList);
        if (vociLista != null) vociLista.setItems(vociList);
        if (ricette != null) ricette.setItems(ricetteList);
    }
    
    /**
     * Imposta i componenti della RicettarioView
     */
    public void setComponentiRicettario(TextField nomeRicetta, TextField descrizioneRicetta, TextField tempoPreparazione,
                                       TextField autoreRicetta, ComboBox<String> statoRicetta, ListView<Ricetta> listaRicette) {
        this.campoNomeRicetta = nomeRicetta;
        this.campoDescrizioneRicetta = descrizioneRicetta;
        this.campoTempoPreparazione = tempoPreparazione;
        this.campoAutoreRicetta = autoreRicetta;
        this.comboStatoRicetta = statoRicetta;
        this.listaRicette = listaRicette;
        
        // Collega la lista
        if (listaRicette != null) listaRicette.setItems(tutteRicetteList);
    }
    
    /**
     * Imposta i componenti della FinaleView
     */
    public void setComponentiFinale(TextField nuovoTitolo, TextArea nuoveNote, Label stato) {
        this.campoNuovoTitolo = nuovoTitolo;
        this.areaNuoveNote = nuoveNote;
        this.labelStato = stato;
    }
    
    /**
     * Imposta i componenti della BachecaView
     */
    public void setComponentiBacheca(ListView<Menu> listaMenuPubblicati, TextArea areaDettagli, Label stato) {
        this.listaMenuPubblicati = listaMenuPubblicati;
        this.areaDettagliMenuBacheca = areaDettagli;
        this.labelStatoBacheca = stato;
        
        // Collega la lista
        if (listaMenuPubblicati != null) listaMenuPubblicati.setItems(menuPubblicatiList);
    }
    
    // ========================================
    // GESTORI EVENTI MENU
    // ========================================
    
    public void handleCreaMenu() {
        String nome = campoNomeMenu != null ? campoNomeMenu.getText().trim() : "";
        String descrizione = campoDescrizioneMenu != null ? campoDescrizioneMenu.getText().trim() : "";
        String note = areaNoteMenu != null ? areaNoteMenu.getText().trim() : "";
        
        if (nome.isEmpty()) {
            mostraErrore("Nome menu mancante", "Inserisci un nome per il menu");
            return;
        }
        
        Menu nuovoMenu = menuService.creaMenu(nome, descrizione, note);
        pulisciCampiMenu();
        mostraSuccesso("Menu creato!", "Il menu '" + nuovoMenu.getNome() + "' e stato creato");
    }
    
    public void handleDuplicaMenu() {
        if (menuSelezionato == null) {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu da duplicare");
            return;
        }
        
        if (confermaAzione("Conferma Duplicazione", "Duplicare il menu '" + menuSelezionato.getNome() + "'?")) {
            Menu menuDuplicato = menuService.duplicaMenu(menuSelezionato);
            mostraSuccesso("Menu duplicato!", "Il menu e stato duplicato come '" + menuDuplicato.getNome() + "'");
        }
    }
    
    public void handleSelezionaMenu() {
        if (menuSelezionato != null) {
            String dettagli = "Menu: " + menuSelezionato.getNome() + 
                            "\nDescrizione: " + menuSelezionato.getDescrizione() +
                            "\nSezioni: " + menuSelezionato.getSezioni().size();
            mostraInfo("Dettagli Menu", dettagli);
        } else {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu dalla tabella");
        }
    }
    
    public void handleAggiungiSezione() {
        if (menuSelezionato == null) {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu prima di aggiungere sezioni");
            return;
        }
        
        String titolo = campoTitoloSezione != null ? campoTitoloSezione.getText().trim() : "";
        if (titolo.isEmpty()) {
            mostraErrore("Titolo sezione mancante", "Inserisci un titolo per la sezione");
            return;
        }
        
        menuService.definisciSezioni(menuSelezionato, titolo);
        aggiornaSezioniMenu();
        aggiornaComboBoxSezioni();
        pulisciCampoSezione();
        mostraSuccesso("Sezione aggiunta!", "La sezione '" + titolo + "' e stata aggiunta");
    }
    
    public void handleAggiungiRicetta() {
        if (menuSelezionato == null) {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu");
            return;
        }
        
        String sezione = comboSezioni != null ? comboSezioni.getValue() : null;
        Ricetta ricetta = comboRicette != null ? comboRicette.getValue() : null;
        
        if (sezione == null) {
            mostraErrore("Sezione non selezionata", "Seleziona una sezione");
            return;
        }
        
        if (ricetta == null) {
            mostraErrore("Ricetta non selezionata", "Seleziona una ricetta");
            return;
        }
        
        menuService.aggiungiRicettaASezione(menuSelezionato, sezione, ricetta);
        aggiornaSezioniMenu();
        aggiornaVociSezioneCorrente(); // Aggiorna automaticamente
        mostraSuccesso("Ricetta aggiunta!", ricetta.getNome() + " aggiunta a " + sezione);
    }
    
    public void handleEliminaRicetta() {
        if (menuSelezionato == null || listaVoci == null) {
            mostraErrore("Nessuna selezione", "Seleziona un menu e una ricetta");
            return;
        }
        
        VoceMenu voce = listaVoci.getSelectionModel().getSelectedItem();
        if (voce == null) {
            mostraErrore("Nessuna ricetta selezionata", "Seleziona una ricetta da eliminare");
            return;
        }
        
        if (confermaAzione("Conferma rimozione", "Rimuovere '" + voce.getNomeVisuale() + "'?")) {
            menuService.eliminaRicetta(menuSelezionato, voce.getRicetta());
            aggiornaSezioniMenu();
            aggiornaVociSezioneCorrente(); // Aggiorna automaticamente
            mostraSuccesso("Ricetta rimossa", "La ricetta e stata rimossa");
        }
    }
    
    public void handleSpostaRicetta() {
        if (menuSelezionato == null || listaVoci == null) {
            mostraErrore("Nessuna selezione", "Seleziona un menu e una ricetta");
            return;
        }
        
        VoceMenu voce = listaVoci.getSelectionModel().getSelectedItem();
        String nuovaSezione = comboSezioni != null ? comboSezioni.getValue() : null;
        
        if (voce == null) {
            mostraErrore("Nessuna ricetta selezionata", "Seleziona una ricetta da spostare");
            return;
        }
        
        if (nuovaSezione == null) {
            mostraErrore("Sezione non selezionata", "Seleziona la sezione di destinazione");
            return;
        }
        
        menuService.spostaRicetta(menuSelezionato.getId(), voce.getRicetta().getId(), nuovaSezione);
        aggiornaSezioniMenu();
        aggiornaVociSezioneCorrente(); // Aggiorna automaticamente
        mostraSuccesso("Ricetta spostata!", voce.getNomeVisuale() + " spostata in " + nuovaSezione);
    }
    
    // ========================================
    // GESTORI EVENTI RICETTARIO
    // ========================================
    
    public void handleConsultaRicettario() {
        aggiornaRicette();
        mostraSuccesso("Ricettario aggiornato", "Disponibili " + tutteRicetteList.size() + " ricette");
    }
    
    public void handleInserisciRicetta() {
        String nome = campoNomeRicetta != null ? campoNomeRicetta.getText().trim() : "";
        String descrizione = campoDescrizioneRicetta != null ? campoDescrizioneRicetta.getText().trim() : "";
        String tempoStr = campoTempoPreparazione != null ? campoTempoPreparazione.getText().trim() : "0";
        String autore = campoAutoreRicetta != null ? campoAutoreRicetta.getText().trim() : "";
        String stato = comboStatoRicetta != null ? comboStatoRicetta.getValue() : "bozza";
        
        if (nome.isEmpty()) {
            mostraErrore("Nome ricetta mancante", "Inserisci il nome della ricetta");
            return;
        }
        
        int tempo;
        try {
            tempo = Integer.parseInt(tempoStr);
            if (tempo < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostraErrore("Tempo non valido", "Inserisci un tempo valido in minuti");
            return;
        }
        
        Ricetta nuovaRicetta = menuService.inserisciRicetta(nome, descrizione, tempo, stato, autore);
        ricetteList.add(nuovaRicetta);
        tutteRicetteList.add(nuovaRicetta);
        pulisciCampiRicetta();
        mostraSuccesso("Ricetta aggiunta!", "La ricetta '" + nuovaRicetta.getNome() + "' e stata aggiunta");
    }
    
    public void handleEliminaRicettaDalRicettario() {
        if (listaRicette == null) {
            mostraErrore("Lista ricette non disponibile", "Errore interno");
            return;
        }
        
        Ricetta ricettaSelezionata = listaRicette.getSelectionModel().getSelectedItem();
        if (ricettaSelezionata == null) {
            mostraErrore("Nessuna ricetta selezionata", "Seleziona una ricetta da eliminare");
            return;
        }
        
        if (confermaAzione("Conferma Eliminazione", "Eliminare la ricetta '" + ricettaSelezionata.getNome() + "'?\n\nQuesta azione non puo essere annullata!")) {
            boolean eliminata = menuService.eliminaRicettaDalRicettario(ricettaSelezionata);
            if (eliminata) {
                tutteRicetteList.remove(ricettaSelezionata);
                ricetteList.remove(ricettaSelezionata);
                mostraSuccesso("Ricetta eliminata", "La ricetta '" + ricettaSelezionata.getNome() + "' e stata eliminata");
            } else {
                mostraErrore("Errore", "Impossibile eliminare la ricetta");
            }
        }
    }
    
    // ========================================
    // GESTORI EVENTI FINALE
    // ========================================
    
    public void handleAggiornaTitolo() {
        if (menuSelezionato == null) {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu");
            return;
        }
        
        String nuovoTitolo = campoNuovoTitolo != null ? campoNuovoTitolo.getText().trim() : "";
        if (nuovoTitolo.isEmpty()) {
            mostraErrore("Nuovo titolo non valido", "Inserisci un nuovo titolo");
            return;
        }
        
        String titoloVecchio = menuSelezionato.getNome();
        menuService.aggiornaTitolo(menuSelezionato, nuovoTitolo);
        if (tabellaMenu != null) tabellaMenu.refresh();
        pulisciCampoTitolo();
        mostraSuccesso("Titolo aggiornato!", "Menu rinominato da '" + titoloVecchio + "' a '" + nuovoTitolo + "'");
    }
    
    public void handleAggiungiAnnotazione() {
        if (menuSelezionato == null) {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu");
            return;
        }
        
        String nuoveNote = areaNuoveNote != null ? areaNuoveNote.getText().trim() : "";
        if (nuoveNote.isEmpty()) {
            mostraErrore("Note vuote", "Inserisci delle note");
            return;
        }
        
        menuService.aggiungiAnnotazione(menuSelezionato, nuoveNote);
        pulisciCampoNote();
        mostraSuccesso("Note aggiunte!", "Le annotazioni sono state salvate");
    }
    
    public void handleGeneraPDF() {
        if (menuSelezionato == null) {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu per generare il PDF");
            return;
        }
        
        // Apri finestra per scegliere cartella di salvataggio
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli cartella per salvare il PDF");
        
        Stage stage = new Stage(); // In un'applicazione reale, passare il stage corrente
        File selectedDirectory = directoryChooser.showDialog(stage);
        
        if (selectedDirectory != null) {
            String percorsoFile = menuService.generaPDFFile(menuSelezionato, selectedDirectory.getAbsolutePath());
            mostraInfo("PDF Generato!", "Il menu e stato salvato in:\n" + percorsoFile);
        }
    }
    
    public void handlePubblicaBacheca() {
        if (menuSelezionato == null) {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu da pubblicare");
            return;
        }
        
        String linkBacheca = menuService.pubblicaSuBacheca(menuSelezionato);
        if (!linkBacheca.contains("già pubblicato")) {
            aggiornaMenuPubblicati();
            mostraInfo("Menu Pubblicato!", "Il menu '" + menuSelezionato.getNome() + "' e ora visibile sulla bacheca!\n\nLink: " + linkBacheca);
        } else {
            mostraErrore("Menu già pubblicato", "Questo menu è già presente sulla bacheca");
        }
    }
    
    public void handleEliminaMenu() {
        if (menuSelezionato == null) {
            mostraErrore("Nessun menu selezionato", "Seleziona un menu da eliminare");
            return;
        }
        
        if (confermaAzione("Conferma Eliminazione", "Eliminare SOLO il menu '" + menuSelezionato.getNome() + "'?\n\nQuesta azione non puo essere annullata!")) {
            String nomeMenu = menuSelezionato.getNome();
            Menu menuDaEliminare = menuSelezionato; // Salva riferimento
            boolean eliminato = menuService.eliminaMenuSingolo(menuDaEliminare);
            if (eliminato) {
                menuList.remove(menuDaEliminare);
                resetMenuSelection();
                mostraSuccesso("Menu eliminato", "Il menu '" + nomeMenu + "' e stato eliminato");
            } else {
                mostraErrore("Errore", "Impossibile eliminare il menu");
            }
        }
    }
    
    // ========================================
    // GESTORI EVENTI BACHECA
    // ========================================
    
    public void handleAggiornaBackeca() {
        aggiornaMenuPubblicati();
        aggiornaStato("Bacheca aggiornata - " + menuPubblicatiList.size() + " menu pubblicati");
    }
    
    public void handleSelezionaMenuBacheca(Menu menu) {
        if (menu != null && areaDettagliMenuBacheca != null) {
            String dettagli = creaDettagliMenuBacheca(menu);
            areaDettagliMenuBacheca.setText(dettagli);
            aggiornaStato("Menu selezionato: " + menu.getNome());
        }
    }
    
    // ========================================
    // GESTORI SELEZIONE
    // ========================================
    
    public void handleSelezionaMenuDaTabella(Menu menu) {
        menuSelezionato = menu;
        aggiornaSezioniMenu();
        aggiornaComboBoxSezioni();
        aggiornaStato("Menu selezionato: " + menu.getNome());
    }
    
    public void handleSelezionaSezione(SezioniMenu sezione) {
        aggiornaVociSezione(sezione);
        aggiornaStato("Sezione selezionata: " + sezione.getTitolo());
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
        if (tabellaMenu != null) tabellaMenu.refresh();
        if (listaSezioni != null) listaSezioni.refresh();
        if (listaVoci != null) listaVoci.refresh();
    }
    
    @Override
    public void onMenuDeleted(Menu menu) {
        menuList.remove(menu);
        menuPubblicatiList.remove(menu);
    }
    
    // ========================================
    // METODI DI SUPPORTO
    // ========================================
    
    private void caricaDatiIniziali() {
        aggiornaMenu();
        aggiornaRicette();
        aggiornaMenuPubblicati();
        aggiornaStato("Sistema pronto - " + menuList.size() + " menu e " + tutteRicetteList.size() + " ricette");
    }
    
    private void aggiornaMenu() {
        menuList.clear();
        menuList.addAll(menuService.getMenus());
    }
    
    private void aggiornaRicette() {
        ricetteList.clear();
        tutteRicetteList.clear();
        ricetteList.addAll(menuService.consultaRicettario());
        tutteRicetteList.addAll(menuService.consultaRicettario());
    }
    
    private void aggiornaMenuPubblicati() {
        menuPubblicatiList.clear();
        menuPubblicatiList.addAll(menuService.getMenuPubblicati());
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
    
    private void aggiornaVociSezioneCorrente() {
        if (listaSezioni != null) {
            SezioniMenu sezioneSelezionata = listaSezioni.getSelectionModel().getSelectedItem();
            if (sezioneSelezionata != null) {
                aggiornaVociSezione(sezioneSelezionata);
            }
        }
    }
    
    private void aggiornaComboBoxSezioni() {
        if (comboSezioni != null) {
            comboSezioni.getItems().clear();
            if (menuSelezionato != null) {
                for (SezioniMenu sezione : menuSelezionato.getSezioni()) {
                    comboSezioni.getItems().add(sezione.getTitolo());
                }
            }
        }
    }
    
    private void resetMenuSelection() {
        menuSelezionato = null;
        sezioniList.clear();
        vociList.clear();
        if (comboSezioni != null) comboSezioni.getItems().clear();
    }
    
    private String creaDettagliMenuBacheca(Menu menu) {
        StringBuilder dettagli = new StringBuilder();
        
        dettagli.append("MENU: ").append(menu.getNome()).append("\n\n");
        dettagli.append("Descrizione: ").append(menu.getDescrizione()).append("\n");
        dettagli.append("Sezioni: ").append(menu.getSezioni().size()).append("\n\n");
        
        if (menu.getNote() != null && !menu.getNote().trim().isEmpty()) {
            dettagli.append("Note: ").append(menu.getNote()).append("\n\n");
        }
        
        dettagli.append("CONTENUTO:\n");
        for (SezioniMenu sezione : menu.getSezioni()) {
            dettagli.append("\n").append(sezione.getTitolo()).append(":\n");
            for (VoceMenu voce : sezione.getVoci()) {
                dettagli.append("- ").append(voce.getNomeVisuale()).append("\n");
            }
        }
        
        return dettagli.toString();
    }
    
    // Metodi per pulire i campi
    private void pulisciCampiMenu() {
        if (campoNomeMenu != null) campoNomeMenu.clear();
        if (campoDescrizioneMenu != null) campoDescrizioneMenu.clear();
        if (areaNoteMenu != null) areaNoteMenu.clear();
    }
    
    private void pulisciCampoSezione() {
        if (campoTitoloSezione != null) campoTitoloSezione.clear();
    }
    
    private void pulisciCampiRicetta() {
        if (campoNomeRicetta != null) campoNomeRicetta.clear();
        if (campoDescrizioneRicetta != null) campoDescrizioneRicetta.clear();
        if (campoTempoPreparazione != null) campoTempoPreparazione.clear();
        if (campoAutoreRicetta != null) campoAutoreRicetta.clear();
        if (comboStatoRicetta != null) comboStatoRicetta.setValue("bozza");
    }
    
    private void pulisciCampoTitolo() {
        if (campoNuovoTitolo != null) campoNuovoTitolo.clear();
    }
    
    private void pulisciCampoNote() {
        if (areaNuoveNote != null) areaNuoveNote.clear();
    }
    
    // Metodi per messaggi
    private void mostraSuccesso(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
        aggiornaStato(titolo);
    }
    
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
        aggiornaStato("Errore: " + titolo);
    }
    
    private void mostraInfo(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    
    private boolean confermaAzione(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        return alert.showAndWait().get() == ButtonType.OK;
    }
    
    private void aggiornaStato(String messaggio) {
        if (labelStato != null) {
            labelStato.setText(messaggio);
        }
        if (labelStatoBacheca != null) {
            labelStatoBacheca.setText(messaggio);
        }
    }
}