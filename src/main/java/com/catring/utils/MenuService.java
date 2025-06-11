package com.catring.utils;

import com.catring.model.*;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private static MenuService instance;
    private List<Menu> menus;
    private List<Ricetta> ricette;
    private List<Evento> eventi;
    
    private MenuService() {
        this.menus = new ArrayList<>();
        this.ricette = new ArrayList<>();
        this.eventi = new ArrayList<>();
        initializeTestData();
    }
    
    public static MenuService getInstance() {
        if (instance == null) {
            instance = new MenuService();
        }
        return instance;
    }
    
    private void initializeTestData() {
        // Dati di test per ricette
        Ricetta ricetta1 = new Ricetta("R001", "Pasta al pomodoro", "Pasta semplice con salsa di pomodoro", 20, "pubblicata", "Chef Mario");
        Ricetta ricetta2 = new Ricetta("R002", "Vitello tonnato", "Vitello con salsa tonnata", 60, "pubblicata", "Chef Luigi");
        Ricetta ricetta3 = new Ricetta("R003", "Tiramisu", "Dolce al caffe", 30, "pubblicata", "Chef Anna");
        
        ricette.add(ricetta1);
        ricette.add(ricetta2);
        ricette.add(ricetta3);
        
        // Dati di test per menu
        Menu menu1 = new Menu("M001", "Menu Matrimonio", "Menu elegante per matrimoni", "Menu completo");
        SezioniMenu antipasti = new SezioniMenu("S001", "Antipasti", 1);
        SezioniMenu primi = new SezioniMenu("S002", "Primi piatti", 2);
        SezioniMenu dolci = new SezioniMenu("S003", "Dolci", 3);
        
        VoceMenu voce1 = new VoceMenu("V001", "Vitello tonnato", "R002", "Specialita della casa");
        VoceMenu voce2 = new VoceMenu("V002", "Pasta al pomodoro", "R001", "");
        VoceMenu voce3 = new VoceMenu("V003", "Tiramisu", "R003", "Dolce della tradizione");
        
        voce1.setRicetta(ricetta2);
        voce2.setRicetta(ricetta1);
        voce3.setRicetta(ricetta3);
        
        antipasti.getVoci().add(voce1);
        primi.getVoci().add(voce2);
        dolci.getVoci().add(voce3);
        
        menu1.getSezioni().add(antipasti);
        menu1.getSezioni().add(primi);
        menu1.getSezioni().add(dolci);
        
        menus.add(menu1);
    }
    
    // FUNZIONALITÀ DEL SEQUENCE DIAGRAM
    
    // consultaEvento() -> restituisce lista eventi
    public List<Evento> consultaEventi() {
        return EventoService.getInstance().getEventi();
    }
    
    // dettagliEvento() -> restituisce dettagli evento selezionato
    public String getDettagliEvento(String eventoId) {
        Evento evento = EventoService.getInstance().getEventoById(eventoId);
        if (evento != null) {
            return "Evento: " + evento.getId() + " - " + evento.getLuogo() + 
                   " dal " + evento.getDataInizio() + " al " + evento.getDataFine();
        }
        return "Evento non trovato";
    }
    
    // creaMenu(id, nome, descrizione, note) -> menuCreato
    public Menu creaMenu(String id, String nome, String descrizione, String note) {
        Menu menu = new Menu(id, nome, descrizione, note);
        menus.add(menu);
        return menu;
    }
    
    // selezionaMenu(id, nome, descrizione, note) -> menuDettagli
    public Menu selezionaMenu(String id) {
        return getMenuById(id);
    }
    
    // definisciSezioni(titolo) -> sezioniAggiornate
    public void definisciSezioni(Menu menu, String titolo) {
        aggiungiSezione(menu, titolo);
    }
    
    // consultaRicettario() -> elencoRicette
    public List<Ricetta> consultaRicettario() {
        return new ArrayList<>(ricette);
    }
    
    // inserisciRicetta(nome, descrizione, tempoPreparazione, stato, autore) -> ricettaAggiunta
    public Ricetta inserisciRicetta(String nome, String descrizione, int tempoPreparazione, String stato, String autore) {
        String id = IdGenerator.generateRicettaId();
        Ricetta ricetta = new Ricetta(id, nome, descrizione, tempoPreparazione, stato, autore);
        ricette.add(ricetta);
        return ricetta;
    }
    
    // eliminaRicetta(menuCreato, ricetta) -> ricettaEliminata
    public void eliminaRicetta(Menu menu, Ricetta ricetta) {
        for (SezioniMenu sezione : menu.getSezioni()) {
            sezione.getVoci().removeIf(voce -> voce.getRicetta() != null && 
                                              voce.getRicetta().getId().equals(ricetta.getId()));
        }
    }
    
    // spostaRicetta(menuId, ricettaId, nuovaSezione) -> ricettaSpostata
    public void spostaRicetta(String menuId, String ricettaId, String nuovaSezione) {
        Menu menu = getMenuById(menuId);
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
            }
        }
    }
    
    // aggiornaTitolo(menuCreato, titolo)
    public void aggiornaTitolo(Menu menu, String nuovoTitolo) {
        menu.setNome(nuovoTitolo);
    }
    
    // aggiungiAnnotazione(menuCreato, note)
    public void aggiungiAnnotazione(Menu menu, String note) {
        String noteAttuali = menu.getNote() != null ? menu.getNote() : "";
        menu.setNote(noteAttuali + "\n" + note);
    }
    
    // generaPDF(menuCreato) -> pdfLink
    public String generaPDF(Menu menu) {
        return "http://catring.com/pdf/" + menu.getId() + ".pdf";
    }
    
    // pubblicaSuBacheca(menuCreato) -> linkBacheca
    public String pubblicaSuBacheca(Menu menu) {
        return "http://catring.com/bacheca/" + menu.getId();
    }
    
    // eliminaMenu(menuCreato) -> confermaEliminazione
    public boolean eliminaMenu(Menu menu) {
        return menus.remove(menu);
    }
    
    // Metodi esistenti mantenuti
    public void aggiungiSezione(Menu menu, String titolo) {
        SezioniMenu sezione = new SezioniMenu(
            IdGenerator.generateSezioneId(), 
            titolo, 
            menu.getSezioni().size() + 1
        );
        menu.getSezioni().add(sezione);
    }
    
    public void aggiungiRicettaASezione(Menu menu, String titoloSezione, Ricetta ricetta) {
        for (SezioniMenu sezione : menu.getSezioni()) {
            if (sezione.getTitolo().equals(titoloSezione)) {
                VoceMenu voce = new VoceMenu(
                    IdGenerator.generateVoceId(),
                    ricetta.getNome(),
                    ricetta.getId(),
                    ""
                );
                voce.setRicetta(ricetta);
                sezione.getVoci().add(voce);
                break;
            }
        }
    }
    
    public void eliminaRicettaDaSezione(Menu menu, String titoloSezione, VoceMenu voce) {
        for (SezioniMenu sezione : menu.getSezioni()) {
            if (sezione.getTitolo().equals(titoloSezione)) {
                sezione.getVoci().remove(voce);
                break;
            }
        }
    }
    
    public void spostaRicetta(Menu menu, VoceMenu voce, String nuovaSezione) {
        // Rimuovi dalla sezione corrente
        for (SezioniMenu sezione : menu.getSezioni()) {
            if (sezione.getVoci().contains(voce)) {
                sezione.getVoci().remove(voce);
                break;
            }
        }
        
        // Aggiungi alla nuova sezione
        for (SezioniMenu sezione : menu.getSezioni()) {
            if (sezione.getTitolo().equals(nuovaSezione)) {
                sezione.getVoci().add(voce);
                break;
            }
        }
    }
    
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
}