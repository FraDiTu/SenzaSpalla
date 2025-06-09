// File: src/main/java/it/catering/catring/model/DataInitializer.java
package it.catering.catring.model;

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
        
        // Get users
        User chef = authController.getAllUsers().stream()
            .filter(u -> u instanceof Chef)
            .findFirst()
            .orElse(null);
        
        User cuoco = authController.getAllUsers().stream()
            .filter(u -> u instanceof Cuoco)
            .findFirst()
            .orElse(null);
        
        if (chef == null || cuoco == null) return;
        
        // Create sample recipes
        createSampleRecipes(ricettarioManager, chef, cuoco);
        
        // Create sample menus
        createSampleMenus(menuManager, ricettarioManager, (Chef) chef);
        
        // Create sample tasks
        createSampleTasks(compitoManager, ricettarioManager, (Cuoco) cuoco);
        
        initialized = true;
    }
    
    private static void createSampleRecipes(RicettarioManager ricettarioManager, User chef, User cuoco) {
        // Ricetta 1: Pasta al Pomodoro
        Ricetta pastaPomodoro = ricettarioManager.createRicetta(
            "Pasta al Pomodoro", 
            "Classica pasta italiana con sugo di pomodoro fresco", 
            chef, 
            4
        );
        pastaPomodoro.setTempoPreparazione(30);
        pastaPomodoro.setAutore("Chef Mario");
        pastaPomodoro.aggiungiTag("italiano");
        pastaPomodoro.aggiungiTag("pasta");
        pastaPomodoro.aggiungiTag("vegetariano");
        
        // Add ingredients
        pastaPomodoro.aggiungiIngrediente(new Dose(
            ricettarioManager.getIngredientiBase().stream()
                .filter(i -> i.getNome().equals("Pomodoro"))
                .findFirst().orElse(new Ingrediente("Pomodoro", "g")), 
            400
        ));
        
        // Add instructions
        pastaPomodoro.aggiungiIstruzioneAnticipo("Preparare il sugo di pomodoro");
        pastaPomodoro.aggiungiIstruzioneAnticipo("Cuocere la pasta");
        pastaPomodoro.aggiungiIstruzioneUltimo("Mantecare la pasta con il sugo");
        pastaPomodoro.setPubblicata(true);
        
        // Ricetta 2: Risotto ai Funghi
        Ricetta risottoFunghi = ricettarioManager.createRicetta(
            "Risotto ai Funghi", 
            "Cremoso risotto con funghi porcini", 
            chef, 
            6
        );
        risottoFunghi.setTempoPreparazione(45);
        risottoFunghi.setAutore("Chef Mario");
        risottoFunghi.aggiungiTag("italiano");
        risottoFunghi.aggiungiTag("risotto");
        risottoFunghi.aggiungiTag("funghi");
        
        risottoFunghi.aggiungiIstruzioneAnticipo("Preparare il brodo vegetale");
        risottoFunghi.aggiungiIstruzioneAnticipo("Pulire e tagliare i funghi");
        risottoFunghi.aggiungiIstruzioneUltimo("Tostare il riso e mantecarlo");
        risottoFunghi.setPubblicata(true);
        
        // Ricetta 3: Tiramisù
        Ricetta tiramisu = ricettarioManager.createRicetta(
            "Tiramisù", 
            "Dolce tradizionale italiano al caffè", 
            cuoco, 
            8
        );
        tiramisu.setTempoPreparazione(60);
        tiramisu.setAutore("Nonna Rosa");
        tiramisu.aggiungiTag("dolce");
        tiramisu.aggiungiTag("caffè");
        tiramisu.aggiungiTag("mascarpone");
        
        tiramisu.aggiungiIstruzioneAnticipo("Preparare il caffè e farlo raffreddare");
        tiramisu.aggiungiIstruzioneAnticipo("Preparare la crema al mascarpone");
        tiramisu.aggiungiIstruzioneAnticipo("Assemblare gli strati");
        tiramisu.aggiungiIstruzioneUltimo("Spolverare con cacao amaro");
        tiramisu.setPubblicata(true);
        
        // Preparazione: Salsa Béchamel
        Preparazione bechamel = ricettarioManager.createPreparazione(
            "Salsa Béchamel", 
            "Salsa base per molte preparazioni", 
            chef
        );
        bechamel.setTempoPreparazione(20);
        bechamel.setQuantitaRisultante(500);
        bechamel.setUnitaMisuraRisultato("ml");
        bechamel.aggiungiTag("salsa");
        bechamel.aggiungiTag("base");
        
        bechamel.aggiungiIstruzioneAnticipo("Fare un roux con burro e farina");
        bechamel.aggiungiIstruzioneAnticipo("Aggiungere il latte gradualmente");
        bechamel.setPubblicata(true);
    }
    
    private static void createSampleMenus(MenuManager menuManager, RicettarioManager ricettarioManager, Chef chef) {
        // Menu 1: Menu Italiano Tradizionale
        Menu menuItaliano = menuManager.createMenu("Menu Italiano Tradizionale", chef);
        menuItaliano.setDescrizione("Un viaggio attraverso i sapori autentici della tradizione italiana");
        menuItaliano.setCucinaRichiesta(true);
        menuItaliano.setAdeguatoBuffet(false);
        
        // Add sections
        SezioneMenu antipasti = new SezioneMenu("Antipasti");
        SezioneMenu primi = new SezioneMenu("Primi Piatti");
        SezioneMenu dolci = new SezioneMenu("Dolci");
        
        menuItaliano.aggiungiSezione(antipasti);
        menuItaliano.aggiungiSezione(primi);
        menuItaliano.aggiungiSezione(dolci);
        
        // Add recipes to sections
        Ricetta pastaPomodoro = ricettarioManager.getRicettePubblicate().stream()
            .filter(r -> r.getNome().equals("Pasta al Pomodoro"))
            .findFirst().orElse(null);
        
        Ricetta risottoFunghi = ricettarioManager.getRicettePubblicate().stream()
            .filter(r -> r.getNome().equals("Risotto ai Funghi"))
            .findFirst().orElse(null);
        
        Ricetta tiramisu = ricettarioManager.getRicettePubblicate().stream()
            .filter(r -> r.getNome().equals("Tiramisù"))
            .findFirst().orElse(null);
        
        if (pastaPomodoro != null) {
            primi.aggiungiVoce(new VoceMenu(pastaPomodoro, "Spaghetti al Pomodoro Fresco", "Primi Piatti"));
        }
        
        if (risottoFunghi != null) {
            primi.aggiungiVoce(new VoceMenu(risottoFunghi, "Risotto ai Porcini", "Primi Piatti"));
        }
        
        if (tiramisu != null) {
            dolci.aggiungiVoce(new VoceMenu(tiramisu, "Tiramisù della Casa", "Dolci"));
        }
        
        // Menu 2: Menu Finger Food
        Menu menuFingerFood = menuManager.createMenu("Menu Finger Food", chef);
        menuFingerFood.setDescrizione("Selezione di stuzzichini per eventi aziendali");
        menuFingerFood.setFingerFood(true);
        menuFingerFood.setAdeguatoBuffet(true);
        menuFingerFood.setSoloPiattiFreddi(true);
        
        SezioneMenu stuzzichini = new SezioneMenu("Stuzzichini");
        menuFingerFood.aggiungiSezione(stuzzichini);
    }
    
    private static void createSampleTasks(CompitoManager compitoManager, RicettarioManager ricettarioManager, Cuoco cuoco) {
        // Get recipes for tasks
        Ricetta pastaPomodoro = ricettarioManager.getRicettePubblicate().stream()
            .filter(r -> r.getNome().equals("Pasta al Pomodoro"))
            .findFirst().orElse(null);
        
        Ricetta risottoFunghi = ricettarioManager.getRicettePubblicate().stream()
            .filter(r -> r.getNome().equals("Risotto ai Funghi"))
            .findFirst().orElse(null);
        
        Preparazione bechamel = ricettarioManager.getPreparazioniPubblicate().stream()
            .filter(p -> p.getNome().equals("Salsa Béchamel"))
            .findFirst().orElse(null);
        
        // Create sample tasks
        if (pastaPomodoro != null) {
            Compito compito1 = compitoManager.assegnaCompito(pastaPomodoro, cuoco, 30, 20);
            compito1.inizia(); // Start the task
        }
        
        if (risottoFunghi != null) {
            compitoManager.assegnaCompito(risottoFunghi, cuoco, 45, 15);
        }
        
        if (bechamel != null) {
            Compito compito3 = compitoManager.assegnaCompito(bechamel, cuoco, 20, 2);
            compito3.inizia();
            compito3.completa(); // Complete this task
        }
        
        // Create a task with problem
        if (pastaPomodoro != null) {
            Compito compitoProblema = compitoManager.assegnaCompito(pastaPomodoro, cuoco, 25, 10);
            compitoProblema.inizia();
            compitoProblema.segnalaProblema("Mancano ingredienti per completare la preparazione");
        }
    }
}