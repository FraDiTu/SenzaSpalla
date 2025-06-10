// File: src/main/java/it/catering/catring/model/DataInitializer.java
package it.catering.catring.model;

import java.util.List;

import it.catering.catring.controller.AuthController;
import it.catering.catring.model.entities.*;
import it.catering.catring.model.managers.*;

public class DataInitializer {
    private static boolean initialized = false;
    
    public static void initializeData() {
        if (initialized) return;
        
        // Initialize managers
        RicettarioManager ricettarioManager = RicettarioManager.getInstance();
        MenuManager menuManager = MenuManager.getInstance();
        CompitoManager compitoManager = CompitoManager.getInstance();
        AuthController authController = AuthController.getInstance();
        
        // Controlla se ci sono già dati caricati dai file JSON
        List<Ricetta> existingRicette = ricettarioManager.getRicettePubblicate();
        List<Menu> existingMenus = menuManager.getAllMenus();
        
        // Se ci sono già dati, non inizializzare dati di demo
        if (!existingRicette.isEmpty() || !existingMenus.isEmpty()) {
            System.out.println("Dati già presenti nel sistema, saltando l'inizializzazione dei dati demo.");
            initialized = true;
            return;
        }
        
        // Get users
        User chef = authController.getAllUsers().stream()
            .filter(u -> u instanceof Chef)
            .findFirst()
            .orElse(null);
        
        User cuoco = authController.getAllUsers().stream()
            .filter(u -> u instanceof Cuoco)
            .findFirst()
            .orElse(null);
        
        if (chef == null || cuoco == null) {
            System.out.println("Utenti di base non trovati, saltando l'inizializzazione.");
            initialized = true;
            return;
        }
        
        System.out.println("Inizializzazione dati demo...");
        
        // Create sample recipes
        createSampleRecipes(ricettarioManager, chef, cuoco);
        
        // Create sample menus
        createSampleMenus(menuManager, ricettarioManager, (Chef) chef);
        
        // Create sample tasks - solo se ci sono ricette disponibili
        List<Ricetta> ricetteDisponibili = ricettarioManager.getRicettePubblicate();
        if (!ricetteDisponibili.isEmpty()) {
            createSampleTasks(compitoManager, ricettarioManager, (Cuoco) cuoco);
        }
        
        System.out.println("Inizializzazione dati demo completata.");
        initialized = true;
    }
    
    private static void createSampleRecipes(RicettarioManager ricettarioManager, User chef, User cuoco) {
        try {
            // Ricetta 1: Pasta al Pomodoro
            Ricetta pastaPomodoro = ricettarioManager.createRicetta(
                "Pasta al Pomodoro Demo", 
                "Classica pasta italiana con sugo di pomodoro fresco (demo)", 
                chef, 
                4
            );
            pastaPomodoro.setTempoPreparazione(30);
            pastaPomodoro.setAutore("Chef Francesco");
            pastaPomodoro.aggiungiTag("italiano");
            pastaPomodoro.aggiungiTag("pasta");
            pastaPomodoro.aggiungiTag("vegetariano");
            
            // Add ingredients
            try {
                pastaPomodoro.aggiungiIngrediente(new Dose(
                    ricettarioManager.getIngredientiBase().stream()
                        .filter(i -> i.getNome().equals("Pomodoro"))
                        .findFirst().orElse(new Ingrediente("Pomodoro", "g")), 
                    400
                ));
            } catch (Exception e) {
                System.err.println("Errore nell'aggiunta ingredienti: " + e.getMessage());
            }
            
            // Add instructions
            pastaPomodoro.aggiungiIstruzioneAnticipo("Preparare il sugo di pomodoro");
            pastaPomodoro.aggiungiIstruzioneAnticipo("Cuocere la pasta");
            pastaPomodoro.aggiungiIstruzioneUltimo("Mantecare la pasta con il sugo");
            
            pastaPomodoro.pubblica();
            
            // Ricetta 2: Risotto ai Funghi
            Ricetta risottoFunghi = ricettarioManager.createRicetta(
                "Risotto ai Funghi Demo", 
                "Cremoso risotto con funghi porcini (demo)", 
                chef, 
                6
            );
            risottoFunghi.setTempoPreparazione(45);
            risottoFunghi.setAutore("Chef Francesco");
            risottoFunghi.aggiungiTag("italiano");
            risottoFunghi.aggiungiTag("risotto");
            risottoFunghi.aggiungiTag("funghi");
            
            risottoFunghi.aggiungiIstruzioneAnticipo("Preparare il brodo vegetale");
            risottoFunghi.aggiungiIstruzioneAnticipo("Pulire e tagliare i funghi");
            risottoFunghi.aggiungiIstruzioneUltimo("Tostare il riso e mantecarlo");
            
            risottoFunghi.pubblica();
            
            // Preparazione: Salsa Béchamel
            Preparazione bechamel = ricettarioManager.createPreparazione(
                "Salsa Béchamel Demo", 
                "Salsa base per molte preparazioni (demo)", 
                chef
            );
            bechamel.setTempoPreparazione(20);
            bechamel.setQuantitaRisultante(500);
            bechamel.setUnitaMisuraRisultato("ml");
            bechamel.aggiungiTag("salsa");
            bechamel.aggiungiTag("base");
            
            bechamel.aggiungiIstruzioneAnticipo("Fare un roux con burro e farina");
            bechamel.aggiungiIstruzioneAnticipo("Aggiungere il latte gradualmente");
            
            bechamel.pubblica();
            
        } catch (Exception e) {
            System.err.println("Errore nella creazione delle ricette demo: " + e.getMessage());
        }
    }
    
    private static void createSampleMenus(MenuManager menuManager, RicettarioManager ricettarioManager, Chef chef) {
        try {
            // Menu Demo: Menu Italiano Tradizionale
            Menu menuItaliano = menuManager.createMenu("Menu Demo Italiano", chef);
            menuItaliano.setDescrizione("Menu demo per testare il sistema");
            menuItaliano.setCucinaRichiesta(true);
            menuItaliano.setAdeguatoBuffet(false);
            
            // Add sections
            SezioneMenu antipasti = new SezioneMenu("Antipasti");
            SezioneMenu primi = new SezioneMenu("Primi Piatti");
            
            menuItaliano.aggiungiSezione(antipasti);
            menuItaliano.aggiungiSezione(primi);
            
            // Add recipes to sections
            List<Ricetta> ricettePubblicate = ricettarioManager.getRicettePubblicate();
            
            Ricetta pastaPomodoro = ricettePubblicate.stream()
                .filter(r -> r.getNome().contains("Pasta al Pomodoro"))
                .findFirst().orElse(null);
            
            Ricetta risottoFunghi = ricettePubblicate.stream()
                .filter(r -> r.getNome().contains("Risotto ai Funghi"))
                .findFirst().orElse(null);
            
            if (pastaPomodoro != null) {
                primi.aggiungiVoce(new VoceMenu(pastaPomodoro, "Spaghetti al Pomodoro Demo", "Primi Piatti"));
            }
            
            if (risottoFunghi != null) {
                primi.aggiungiVoce(new VoceMenu(risottoFunghi, "Risotto ai Porcini Demo", "Primi Piatti"));
            }
            
            menuManager.updateMenu(menuItaliano);
            
        } catch (Exception e) {
            System.err.println("Errore nella creazione dei menu demo: " + e.getMessage());
        }
    }
    
    private static void createSampleTasks(CompitoManager compitoManager, RicettarioManager ricettarioManager, Cuoco cuoco) {
        try {
            // Get recipes for tasks
            List<Ricetta> ricettePubblicate = ricettarioManager.getRicettePubblicate();
            List<Preparazione> preparazioniPubblicate = ricettarioManager.getPreparazioniPubblicate();
            
            Ricetta pastaPomodoro = ricettePubblicate.stream()
                .filter(r -> r.getNome().contains("Pasta al Pomodoro"))
                .findFirst().orElse(null);
            
            Preparazione bechamel = preparazioniPubblicate.stream()
                .filter(p -> p.getNome().contains("Salsa Béchamel"))
                .findFirst().orElse(null);
            
            // Create sample tasks
            if (pastaPomodoro != null) {
                Compito compito1 = compitoManager.assegnaCompito(pastaPomodoro, cuoco, 30, 20);
                compito1.inizia(); // Start the task
                compitoManager.updateCompito(compito1);
            }
            
            if (bechamel != null) {
                Compito compito3 = compitoManager.assegnaCompito(bechamel, cuoco, 20, 2);
                compito3.inizia();
                compito3.completa(); // Complete this task
                compitoManager.updateCompito(compito3);
            }
            
        } catch (Exception e) {
            System.err.println("Errore nella creazione dei compiti demo: " + e.getMessage());
        }
    }
}