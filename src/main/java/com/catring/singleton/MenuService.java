package com.catring.singleton;

import com.catring.creator.MenuCreator;
import com.catring.model.*;
import com.catring.observer.MenuObserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PATTERN GOF: SINGLETON
 * Servizio aggiornato con gestione completa di ricette con ingredienti, dosi e tag
 */
public class MenuService {
    
    // L'unica istanza della classe (Singleton)
    private static MenuService instance;
    
    // Dati gestiti dal service
    private List<Menu> menus;
    private List<Ricetta> ricette;
    private List<Evento> eventi;
    private List<Menu> menuPubblicati;
    private List<Ingrediente> ingredientiBase;
    
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
        this.ingredientiBase = new ArrayList<>();
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
        Menu menu = menuCreator.creaMenu(nome, descrizione, note);
        menus.add(menu);
        notifyMenuCreated(menu);
        return menu;
    }
    
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
    
    /**
     * MODIFICA: Restituisce solo le ricette con stato "pubblicata" per i menu
     */
    public List<Ricetta> consultaRicettePubblicate() {
        return ricette.stream()
                .filter(ricetta -> "pubblicata".equals(ricetta.getStato()))
                .collect(Collectors.toList());
    }
    
    public Ricetta inserisciRicetta(String nome, String descrizione, int tempoPreparazione, String stato, String autore) {
        Ricetta ricetta = menuCreator.creaRicetta(nome, descrizione, tempoPreparazione, stato, autore);
        ricette.add(ricetta);
        return ricetta;
    }
    
    /**
     * NUOVO METODO: Inserisce una ricetta completa con ingredienti e tag
     */
    public boolean inserisciRicettaCompleta(Ricetta ricettaCompleta) {
        if (ricettaCompleta == null || ricettaCompleta.getNome() == null || ricettaCompleta.getNome().trim().isEmpty()) {
            return false;
        }
        
        // Verifica che non esista già una ricetta con lo stesso nome
        boolean esisteGia = ricette.stream()
                .anyMatch(r -> r.getNome().equalsIgnoreCase(ricettaCompleta.getNome()));
        
        if (esisteGia) {
            return false;
        }
        
        ricette.add(ricettaCompleta);
        return true;
    }
    
    /**
     * NUOVO METODO: Aggiorna una ricetta esistente
     */
    public boolean aggiornaRicetta(Ricetta ricettaAggiornata) {
        if (ricettaAggiornata == null || ricettaAggiornata.getId() == null) {
            return false;
        }
        
        for (int i = 0; i < ricette.size(); i++) {
            if (ricette.get(i).getId().equals(ricettaAggiornata.getId())) {
                ricette.set(i, ricettaAggiornata);
                return true;
            }
        }
        
        return false;
    }
    
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
            return false;
        }
        
        return ricette.remove(ricetta);
    }
    
    public void aggiungiRicettaASezione(Menu menu, String titoloSezione, Ricetta ricetta) {
        // CONTROLLO: Solo ricette pubblicate possono essere aggiunte ai menu
        if (!"pubblicata".equals(ricetta.getStato())) {
            throw new IllegalArgumentException("Solo le ricette pubblicate possono essere aggiunte ai menu");
        }
        
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
     * MODIFICA: Genera PDF senza la parola "porte"
     */
    public String generaPDFFile(Menu menu, String percorsoCartella) {
        try {
            String nomeFile = menu.getNome().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf.txt";
            String percorsoCompleto = percorsoCartella + File.separator + nomeFile;
            
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
                        Ricetta ricetta = voce.getRicetta();
                        contenutoPDF.append("  Tempo preparazione: ")
                                   .append(ricetta.getTempoPreparazione())
                                   .append(" minuti (").append(ricetta.getNumeroPorte()).append(")\n");
                        
                        if (ricetta.getDescrizione() != null && !ricetta.getDescrizione().trim().isEmpty()) {
                            contenutoPDF.append("  ").append(ricetta.getDescrizione()).append("\n");
                        }
                        
                        // Aggiungi ingredienti
                        if (!ricetta.getIngredienti().isEmpty()) {
                            contenutoPDF.append("  Ingredienti:\n");
                            for (int i = 0; i < ricetta.getIngredienti().size(); i++) {
                                Ingrediente ingrediente = ricetta.getIngredienti().get(i);
                                contenutoPDF.append("    - ").append(ingrediente.getNome());
                                
                                if (i < ricetta.getDosi().size()) {
                                    Dose dose = ricetta.getDosi().get(i);
                                    contenutoPDF.append(": ").append(dose.getQuantitativo())
                                               .append(" ").append(dose.getUnitaMisura());
                                }
                                contenutoPDF.append("\n");
                            }
                        }
                        
                        // Aggiungi tag
                        if (!ricetta.getTags().isEmpty()) {
                            contenutoPDF.append("  Tag: ");
                            for (int i = 0; i < ricetta.getTags().size(); i++) {
                                if (i > 0) contenutoPDF.append(", ");
                                contenutoPDF.append(ricetta.getTags().get(i).getNome());
                            }
                            contenutoPDF.append("\n");
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
    
    public String generaPDF(Menu menu) {
        return "PDF per il menu '" + menu.getNome() + "' pronto per la generazione";
    }
    
    public String pubblicaSuBacheca(Menu menu) {
        if (!menuPubblicati.contains(menu)) {
            menuPubblicati.add(menu);
            notifyMenuUpdated(menu);
            return "http://catring.com/bacheca/" + menu.getId();
        }
        return "Menu già pubblicato sulla bacheca";
    }
    
    public boolean eliminaMenuSingolo(Menu menu) {
        boolean rimosso = menus.remove(menu);
        if (rimosso) {
            menuPubblicati.remove(menu);
            notifyMenuDeleted(menu);
        }
        return rimosso;
    }
    
    @Deprecated
    public boolean eliminaMenu(Menu menu) {
        return eliminaMenuSingolo(menu);
    }
    
    // ========================================
    // NUOVI METODI PER GESTIONE INGREDIENTI
    // ========================================
    
    /**
     * Restituisce la lista degli ingredienti base disponibili
     */
    public List<Ingrediente> getIngredientiBase() {
        return new ArrayList<>(ingredientiBase);
    }
    
    /**
     * Aggiunge un nuovo ingrediente base al sistema
     */
    public Ingrediente aggiungiIngredienteBase(String nome, String tipo, String unitaMisura) {
        String id = "IB" + System.currentTimeMillis();
        Ingrediente ingrediente = new Ingrediente(id, nome, tipo, unitaMisura);
        ingredientiBase.add(ingrediente);
        return ingrediente;
    }
    
    /**
     * Cerca ingredienti per nome
     */
    public List<Ingrediente> cercaIngredienti(String nome) {
        List<Ingrediente> risultati = new ArrayList<>();
        String nomeMinuscolo = nome.toLowerCase();
        
        for (Ingrediente ingrediente : ingredientiBase) {
            if (ingrediente.getNome().toLowerCase().contains(nomeMinuscolo)) {
                risultati.add(ingrediente);
            }
        }
        
        return risultati;
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
    
    public List<Menu> getMenuPubblicati() {
        return new ArrayList<>(menuPubblicati);
    }
    
    public boolean rimuoviDaBacheca(Menu menu) {
        boolean rimosso = menuPubblicati.remove(menu);
        if (rimosso) {
            notifyMenuUpdated(menu);
        }
        return rimosso;
    }
    
    public boolean isMenuPubblicato(Menu menu) {
        return menuPubblicati.contains(menu);
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
        // Inizializza ingredienti base
        ingredientiBase.add(new Ingrediente("IB001", "Pomodoro", "verdura", "kg"));
        ingredientiBase.add(new Ingrediente("IB002", "Mozzarella", "latticino", "kg"));
        ingredientiBase.add(new Ingrediente("IB003", "Basilico", "erba", "mazzi"));
        ingredientiBase.add(new Ingrediente("IB004", "Olio extravergine", "condimento", "litri"));
        ingredientiBase.add(new Ingrediente("IB005", "Pasta", "cereale", "kg"));
        ingredientiBase.add(new Ingrediente("IB006", "Aglio", "verdura", "spicchi"));
        ingredientiBase.add(new Ingrediente("IB007", "Parmigiano", "formaggio", "kg"));
        ingredientiBase.add(new Ingrediente("IB008", "Vitello", "carne", "kg"));
        ingredientiBase.add(new Ingrediente("IB009", "Tonno", "pesce", "kg"));
        ingredientiBase.add(new Ingrediente("IB010", "Maionese", "salsa", "kg"));
        
        // Dati di test per eventi CON NUMERO PERSONE
        Cliente cliente1 = new Cliente("C001", "Matrimonio Rossi", "privato", "mario.rossi@email.com");
        Cliente cliente2 = new Cliente("C002", "Azienda Tech", "azienda", "info@tech.com");
        
        Evento evento1 = new Evento("E001", 
                java.time.LocalDate.of(2024, 6, 15), 
                java.time.LocalDate.of(2024, 6, 15), 
                "Villa Reale", "matrimonio", "Matrimonio elegante con cerimonia e ricevimento");
        evento1.setCliente(cliente1);
        evento1.setNumeroPersone(120);
        
        Evento evento2 = new Evento("E002", 
                java.time.LocalDate.of(2024, 7, 10), 
                java.time.LocalDate.of(2024, 7, 12), 
                "Centro Congressi", "conferenza", "Conferenza aziendale di tre giorni");
        evento2.setCliente(cliente2);
        evento2.setNumeroPersone(80);
        
        Cliente cliente3 = new Cliente("C003", "Festa Compleanno", "privato", "festa@email.com");
        Evento evento3 = new Evento("E003",
                java.time.LocalDate.of(2024, 8, 20),
                java.time.LocalDate.of(2024, 8, 20),
                "Ristorante La Tavola", "compleanno", "Festa di compleanno con buffet");
        evento3.setCliente(cliente3);
        evento3.setNumeroPersone(35);
        
        eventi.add(evento1);
        eventi.add(evento2);
        eventi.add(evento3);
        
        // Inizializza ricette di test con stati diversi
        Ricetta ricetta1 = new Ricetta("R001", "Pasta al pomodoro", "Pasta semplice con salsa di pomodoro", 20, "pubblicata", "Chef Mario");
        Ricetta ricetta2 = new Ricetta("R002", "Vitello tonnato", "Vitello con salsa tonnata", 60, "pubblicata", "Chef Luigi");
        Ricetta ricetta3 = new Ricetta("R003", "Tiramisu", "Dolce al caffe", 30, "pubblicata", "Chef Anna");
        Ricetta ricetta4 = new Ricetta("R004", "Risotto bozza", "Risotto in fase di sviluppo", 25, "bozza", "Chef Marco");
        
        ricette.add(ricetta1);
        ricette.add(ricetta2);
        ricette.add(ricetta3);
        ricette.add(ricetta4);
    }
}