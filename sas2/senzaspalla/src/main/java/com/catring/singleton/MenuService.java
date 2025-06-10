package com.catring.singleton;

import com.catring.creator.MenuCreator;
import com.catring.model.*;
import com.catring.observer.MenuObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * PATTERN GOF: SINGLETON
 * Questa classe implementa il pattern Singleton per garantire una sola istanza
 * del servizio di gestione menu in tutta l'applicazione.
 */
public class MenuService {
    
    // L'unica istanza della classe (Singleton)
    private static MenuService instance;
    
    // Dati gestiti dal service
    private List<Menu> menus;
    private List<Ricetta> ricette;
    private List<Evento> eventi;
    
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
    
    public String generaPDF(Menu menu) {
        return "http://catring.com/pdf/" + menu.getId() + ".pdf";
    }
    
    public String pubblicaSuBacheca(Menu menu) {
        return "http://catring.com/bacheca/" + menu.getId();
    }
    
    public boolean eliminaMenu(Menu menu) {
        boolean rimosso = menus.remove(menu);
        if (rimosso) {
            notifyMenuDeleted(menu);
        }
        return rimosso;
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
        
        ricette.add(ricetta1);
        ricette.add(ricetta2);
        ricette.add(ricetta3);
        
        // Menu di esempio
        Menu menuEsempio = menuCreator.creaMenu("Menu Matrimonio", 
                "Menu elegante per matrimoni", "Menu completo");
        
        SezioniMenu antipasti = menuCreator.creaSezione("Antipasti", 1);
        SezioniMenu primi = menuCreator.creaSezione("Primi piatti", 2);
        SezioniMenu dolci = menuCreator.creaSezione("Dolci", 3);
        
        VoceMenu voce1 = menuCreator.creaVoceMenu(ricetta2);
        VoceMenu voce2 = menuCreator.creaVoceMenu(ricetta1);
        VoceMenu voce3 = menuCreator.creaVoceMenu(ricetta3);
        
        antipasti.getVoci().add(voce1);
        primi.getVoci().add(voce2);
        dolci.getVoci().add(voce3);
        
        menuEsempio.getSezioni().add(antipasti);
        menuEsempio.getSezioni().add(primi);
        menuEsempio.getSezioni().add(dolci);
        
        menus.add(menuEsempio);
    }
}