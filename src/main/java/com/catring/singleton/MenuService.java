package com.catring.singleton;

import com.catring.creator.MenuCreator;
import com.catring.model.*;
import com.catring.observer.MenuObserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PATTERN GOF: SINGLETON
 * Servizio aggiornato con funzionalità di duplicazione, eliminazione ricette e gestione bacheca
 */
public class MenuService {
    
    // L'unica istanza della classe (Singleton)
    private static MenuService instance;
    
    // Dati gestiti dal service
    private List<Menu> menus;
    private List<Ricetta> ricette;
    private List<Evento> eventi;
    private List<Menu> menuPubblicati;
    
    // Lista di observer (Pattern Observer)
    private List<MenuObserver> observers;
    
    // Creator per la creazione di oggetti (Pattern Creator)
    private MenuCreator menuCreator;
    
    /**
     * Costruttore privato per impedire istanziazione diretta (Singleton)
     */
    private MenuService() {
        this.menus = new ArrayList<>();
        this.ricette = new ArrayList<>();
        this.eventi = new ArrayList<>();
        this.menuPubblicati = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.menuCreator = new MenuCreator();
        initializeTestData();
    }
    
    /**
     * Metodo per ottenere l'unica istanza (Singleton)
     */
    public static MenuService getInstance() {
        if (instance == null) {
            instance = new MenuService();
        }
        return instance;
    }
    
    /**
     * Previene la clonazione (Singleton)
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Non è possibile clonare un Singleton");
    }
    
    // ========================================
    // METODI DEL SEQUENCE DIAGRAM
    // ========================================
    
    public List<Evento> consultaEventi() {
        return new ArrayList<>(eventi);
    }
    
    public String getDettagliEvento(String eventoId) {
        Evento evento = eventi.stream()
                .filter(e -> e.getId().equals(eventoId))
                .findFirst()
                .orElse(null);
        
        if (evento != null) {
            return "Evento: " + evento.getId() + " - " + evento.getLuogo() + 
                   " dal " + evento.getDataInizio() + " al " + evento.getDataFine();
        }
        return "Evento non trovato";
    }
    
    public String getDettagliMenu(Menu menu) {
        return "Menu: " + menu.getNome() + 
               "\nDescrizione: " + menu.getDescrizione() +
               "\nSezioni: " + menu.getSezioni().size() +
               "\nNote: " + (menu.getNote() != null ? menu.getNote() : "Nessuna nota");
    }
    
    public Menu creaMenu(String nome, String descrizione, String note) {
        // Utilizza il Creator per creare il menu
        Menu menu = menuCreator.creaMenu(nome, descrizione, note);
        menus.add(menu);
        
        // Notifica gli observer
        notifyMenuCreated(menu);
        
        return menu;
    }
    
    /**
     * NUOVA FUNZIONALITA: Duplica un menu esistente
     */
    public Menu duplicaMenu(Menu menuOriginale) {
        String nuovoNome = "Copia di " + menuOriginale.getNome();
        Menu menuDuplicato = menuCreator.creaMenu(
            nuovoNome, 
            menuOriginale.getDescrizione(), 
            menuOriginale.getNote()
        );
        
        // Duplica tutte le sezioni e le voci
        for (SezioniMenu sezioneOriginale : menuOriginale.getSezioni()) {
            SezioniMenu sezioneDuplicata = menuCreator.creaSezione(
                sezioneOriginale.getTitolo(), 
                sezioneOriginale.getOrdine()
            );
            
            // Duplica tutte le voci della sezione
            for (VoceMenu voceOriginale : sezioneOriginale.getVoci()) {
                VoceMenu voceDuplicata = menuCreator.creaVoceMenu(voceOriginale.getRicetta());
                voceDuplicata.setModificheTesto(voceOriginale.getModificheTesto());
                sezioneDuplicata.getVoci().add(voceDuplicata);
            }
            
            menuDuplicato.getSezioni().add(sezioneDuplicata);
        }
        
        menus.add(menuDuplicato);
        notifyMenuCreated(menuDuplicato);
        
        return menuDuplicato;
    }
    
    public Menu selezionaMenu(String id) {
        return menus.stream()
                .filter(menu -> menu.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public void definisciSezioni(Menu menu, String titolo) {
        SezioniMenu sezione = menuCreator.creaSezione(titolo, menu.getSezioni().size() + 1);
        menu.getSezioni().add(sezione);
        notifyMenuUpdated(menu);
    }
    
    public List<Ricetta> consultaRicettario() {
        return new ArrayList<>(ricette);
    }
    
    public Ricetta inserisciRicetta(String nome, String descrizione, int tempoPreparazione, String stato, String autore) {
        Ricetta ricetta = menuCreator.creaRicetta(nome, descrizione, tempoPreparazione, stato, autore);
        ricette.add(ricetta);
        return ricetta;
    }
    
    /**
     * NUOVA FUNZIONALITA: Elimina una ricetta dal ricettario
     */
    public boolean eliminaRicettaDalRicettario(Ricetta ricetta) {
        // Verifica se la ricetta è usata in qualche menu
        boolean usataInMenu = false;
        for (Menu menu : menus) {
            for (SezioniMenu sezione : menu.getSezioni()) {
                for (VoceMenu voce : sezione.getVoci()) {
                    if (voce.getRicetta() != null && voce.getRicetta().getId().equals(ricetta.getId())) {
                        usataInMenu = true;
                        break;
                    }
                }
                if (usataInMenu) break;
            }
            if (usataInMenu) break;
        }
        
        if (usataInMenu) {
            // Non eliminare se è usata in menu
            return false;
        }
        
        return ricette.remove(ricetta);
    }
    
    public void aggiungiRicettaASezione(Menu menu, String titoloSezione, Ricetta ricetta) {
        for (SezioniMenu sezione : menu.getSezioni()) {
            if (sezione.getTitolo().equals(titoloSezione)) {
                VoceMenu voce = menuCreator.creaVoceMenu(ricetta);
                sezione.getVoci().add(voce);
                notifyMenuUpdated(menu);
                break;
            }
        }
    }
    
    public void eliminaRicetta(Menu menu, Ricetta ricetta) {
        for (SezioniMenu sezione : menu.getSezioni()) {
            sezione.getVoci().removeIf(voce -> voce.getRicetta() != null && 
                                              voce.getRicetta().getId().equals(ricetta.getId()));
        }
        notifyMenuUpdated(menu);
    }
    
    public void spostaRicetta(String menuId, String ricettaId, String nuovaSezione) {
        Menu menu = selezionaMenu(menuId);
        if (menu != null) {
            VoceMenu voceDaSpostare = null;
            
            // Trova e rimuovi la voce dalla sezione corrente
            for (SezioniMenu sezione : menu.getSezioni()) {
                for (VoceMenu voce : sezione.getVoci()) {
                    if (voce.getRicetta() != null && voce.getRicetta().getId().equals(ricettaId)) {
                        voceDaSpostare = voce;
                        sezione.getVoci().remove(voce);
                        break;
                    }
                }
                if (voceDaSpostare != null) break;
            }
            
            // Aggiungi alla nuova sezione
            if (voceDaSpostare != null) {
                for (SezioniMenu sezione : menu.getSezioni()) {
                    if (sezione.getTitolo().equals(nuovaSezione)) {
                        sezione.getVoci().add(voceDaSpostare);
                        break;
                    }
                }
                notifyMenuUpdated(menu);
            }
        }
    }
    
    public void aggiornaTitolo(Menu menu, String nuovoTitolo) {
        menu.setNome(nuovoTitolo);
        notifyMenuUpdated(menu);
    }
    
    public void aggiungiAnnotazione(Menu menu, String note) {
        String noteAttuali = menu.getNote() != null ? menu.getNote() : "";
        menu.setNote(noteAttuali + "\n" + note);
        notifyMenuUpdated(menu);
    }
    
    /**
     * FUNZIONALITA MIGLIORATA: Genera PDF e salva su file system
     */
    public String generaPDFFile(Menu menu, String percorsoCartella) {
        try {
            // Crea il nome del file
            String nomeFile = menu.getNome().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf.txt";
            String percorsoCompleto = percorsoCartella + File.separator + nomeFile;
            
            // Crea il contenuto del "PDF" (in formato testo per semplicità)
            StringBuilder contenutoPDF = new StringBuilder();
            contenutoPDF.append("=== MENU: ").append(menu.getNome()).append(" ===\n\n");
            contenutoPDF.append("Descrizione: ").append(menu.getDescrizione()).append("\n\n");
            
            if (menu.getNote() != null && !menu.getNote().trim().isEmpty()) {
                contenutoPDF.append("Note: ").append(menu.getNote()).append("\n\n");
            }
            
            contenutoPDF.append("CONTENUTO DEL MENU:\n");
            contenutoPDF.append("==================\n\n");
            
            for (SezioniMenu sezione : menu.getSezioni()) {
                contenutoPDF.append(sezione.getTitolo().toUpperCase()).append("\n");
                contenutoPDF.append("-".repeat(sezione.getTitolo().length())).append("\n");
                
                for (VoceMenu voce : sezione.getVoci()) {
                    contenutoPDF.append("• ").append(voce.getNomeVisuale()).append("\n");
                    if (voce.getRicetta() != null) {
                        contenutoPDF.append("  Tempo preparazione: ")
                                   .append(voce.getRicetta().getTempoPreparazione())
                                   .append(" minuti\n");
                        if (voce.getRicetta().getDescrizione() != null) {
                            contenutoPDF.append("  ").append(voce.getRicetta().getDescrizione()).append("\n");
                        }
                    }
                    if (voce.getModificheTesto() != null && !voce.getModificheTesto().trim().isEmpty()) {
                        contenutoPDF.append("  Note: ").append(voce.getModificheTesto()).append("\n");
                    }
                    contenutoPDF.append("\n");
                }
                contenutoPDF.append("\n");
            }
            
            contenutoPDF.append("\n=== Fine Menu ===\n");
            contenutoPDF.append("Generato dal sistema Cat & Ring\n");
            
            // Salva il file
            try (FileWriter writer = new FileWriter(percorsoCompleto)) {
                writer.write(contenutoPDF.toString());
            }
            
            return percorsoCompleto;
            
        } catch (IOException e) {
            throw new RuntimeException("Errore nella generazione del PDF: " + e.getMessage());
        }
    }
    
    /**
     * Metodo di compatibilità per l'interfaccia esistente
     */
    public String generaPDF(Menu menu) {
        return "PDF per il menu '" + menu.getNome() + "' pronto per la generazione";
    }
    
    /**
     * FUNZIONALITA MIGLIORATA: Pubblica menu sulla bacheca
     */
    public String pubblicaSuBacheca(Menu menu) {
        if (!menuPubblicati.contains(menu)) {
            menuPubblicati.add(menu);
            notifyMenuUpdated(menu);
            return "http://catring.com/bacheca/" + menu.getId();
        }
        return "Menu già pubblicato sulla bacheca";
    }
    
    /**
     * FUNZIONALITA CORRETTA: Elimina solo il menu specificato
     */
    public boolean eliminaMenuSingolo(Menu menu) {
        boolean rimosso = menus.remove(menu);
        if (rimosso) {
            // Rimuovi anche dalla bacheca se presente
            menuPubblicati.remove(menu);
            notifyMenuDeleted(menu);
        }
        return rimosso;
    }
    
    /**
     * Metodo di compatibilità (deprecato - usare eliminaMenuSingolo)
     */
    @Deprecated
    public boolean eliminaMenu(Menu menu) {
        return eliminaMenuSingolo(menu);
    }
    
    // ========================================
    // NUOVI METODI PER GESTIONE BACHECA
    // ========================================
    
    /**
     * Restituisce la lista dei menu pubblicati sulla bacheca
     */
    public List<Menu> getMenuPubblicati() {
        return new ArrayList<>(menuPubblicati);
    }
    
    /**
     * Rimuove un menu dalla bacheca
     */
    public boolean rimuoviDaBacheca(Menu menu) {
        boolean rimosso = menuPubblicati.remove(menu);
        if (rimosso) {
            notifyMenuUpdated(menu);
        }
        return rimosso;
    }
    
    /**
     * Verifica se un menu è pubblicato sulla bacheca
     */
    public boolean isMenuPubblicato(Menu menu) {
        return menuPubblicati.contains(menu);
    }
    
    // ========================================
    // GESTIONE PATTERN OBSERVER
    // ========================================
    
    public void addObserver(MenuObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void removeObserver(MenuObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyMenuCreated(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuCreated(menu);
        }
    }
    
    private void notifyMenuUpdated(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuUpdated(menu);
        }
    }
    
    private void notifyMenuDeleted(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuDeleted(menu);
        }
    }
    
    // ========================================
    // METODI DI ACCESSO AI DATI
    // ========================================
    
    public List<Menu> getMenus() {
        return new ArrayList<>(menus);
    }
    
    public List<Ricetta> getRicette() {
        return new ArrayList<>(ricette);
    }
    
    public List<Evento> getEventi() {
        return new ArrayList<>(eventi);
    }
    
    public Menu getMenuById(String id) {
        return menus.stream()
                .filter(menu -> menu.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public Ricetta getRicettaById(String id) {
        return ricette.stream()
                .filter(ricetta -> ricetta.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // ========================================
    // INIZIALIZZAZIONE DATI DI TEST
    // ========================================
    
    private void initializeTestData() {
        // Dati di test per eventi
        Cliente cliente1 = new Cliente("C001", "Matrimonio Rossi", "privato", "mario.rossi@email.com");
        Cliente cliente2 = new Cliente("C002", "Azienda Tech", "azienda", "info@tech.com");
        
        Evento evento1 = new Evento("E001", 
                java.time.LocalDate.of(2024, 6, 15), 
                java.time.LocalDate.of(2024, 6, 15), 
                "Villa Reale", "singolo", "Matrimonio elegante");
        evento1.setCliente(cliente1);
        
        Evento evento2 = new Evento("E002", 
                java.time.LocalDate.of(2024, 7, 10), 
                java.time.LocalDate.of(2024, 7, 12), 
                "Centro Congressi", "complesso", "Conferenza aziendale");
        evento2.setCliente(cliente2);
        
        eventi.add(evento1);
        eventi.add(evento2);
        
        // Dati di test per ricette
        Ricetta ricetta1 = menuCreator.creaRicetta("Pasta al pomodoro", 
                "Pasta semplice con salsa di pomodoro", 20, "pubblicata", "Chef Mario");
        Ricetta ricetta2 = menuCreator.creaRicetta("Vitello tonnato", 
                "Vitello con salsa tonnata", 60, "pubblicata", "Chef Luigi");
        Ricetta ricetta3 = menuCreator.creaRicetta("Tiramisu", 
                "Dolce al caffe", 30, "pubblicata", "Chef Anna");
        Ricetta ricetta4 = menuCreator.creaRicetta("Risotto ai funghi", 
                "Risotto cremoso con porcini", 35, "pubblicata", "Chef Marco");
        Ricetta ricetta5 = menuCreator.creaRicetta("Salmone in crosta", 
                "Salmone con crosta alle erbe", 45, "pubblicata", "Chef Sofia");
        
        ricette.add(ricetta1);
        ricette.add(ricetta2);
        ricette.add(ricetta3);
        ricette.add(ricetta4);
        ricette.add(ricetta5);
        
        // Menu di esempio
        Menu menuEsempio = menuCreator.creaMenu("Menu Matrimonio Classico", 
                "Menu tradizionale per matrimoni", "Menu completo con opzioni vegetariane");
        
        SezioniMenu antipasti = menuCreator.creaSezione("Antipasti", 1);
        SezioniMenu primi = menuCreator.creaSezione("Primi piatti", 2);
        SezioniMenu secondi = menuCreator.creaSezione("Secondi piatti", 3);
        SezioniMenu dolci = menuCreator.creaSezione("Dolci", 4);
        
        VoceMenu voce1 = menuCreator.creaVoceMenu(ricetta2);
        VoceMenu voce2 = menuCreator.creaVoceMenu(ricetta4);
        VoceMenu voce3 = menuCreator.creaVoceMenu(ricetta5);
        VoceMenu voce4 = menuCreator.creaVoceMenu(ricetta3);
        
        antipasti.getVoci().add(voce1);
        primi.getVoci().add(voce2);
        secondi.getVoci().add(voce3);
        dolci.getVoci().add(voce4);
        
        menuEsempio.getSezioni().add(antipasti);
        menuEsempio.getSezioni().add(primi);
        menuEsempio.getSezioni().add(secondi);
        menuEsempio.getSezioni().add(dolci);
        
        menus.add(menuEsempio);
        
        // Menu di esempio per la bacheca
        Menu menuBacheca = menuCreator.creaMenu("Menu del Giorno", 
                "Menu speciale consigliato oggi", "Piatti della tradizione");
        
        SezioniMenu antipatiBacheca = menuCreator.creaSezione("Antipasti del giorno", 1);
        SezioniMenu primiBacheca = menuCreator.creaSezione("Primi del giorno", 2);
        
        VoceMenu voceBacheca1 = menuCreator.creaVoceMenu(ricetta1);
        VoceMenu voceBacheca2 = menuCreator.creaVoceMenu(ricetta2);
        
        antipatiBacheca.getVoci().add(voceBacheca2);
        primiBacheca.getVoci().add(voceBacheca1);
        
        menuBacheca.getSezioni().add(antipatiBacheca);
        menuBacheca.getSezioni().add(primiBacheca);
        
        menus.add(menuBacheca);
        menuPubblicati.add(menuBacheca); // Aggiungi alla bacheca
    }
}