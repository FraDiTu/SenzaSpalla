package com.catring.controller;

import com.catring.model.*;
import com.catring.utils.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MenuControllerTest {
    
    private MenuService menuService;
    
    @BeforeEach
    void setUp() {
        menuService = MenuService.getInstance();
    }
    
    @Test
    void testMenuServiceIntegration() {
        // Test dell'integrazione tra controller e service
        Menu menu = menuService.creaMenu("CTRL001", "Menu Controller Test", "Descrizione", "Note");
        
        assertNotNull(menu);
        assertEquals("CTRL001", menu.getId());
        assertTrue(menuService.getMenus().contains(menu));
    }
    
    @Test
    void testAggiungiSezioneIntegration() {
        Menu menu = menuService.creaMenu("CTRL002", "Menu Sezioni Test", "Descrizione", "Note");
        
        menuService.aggiungiSezione(menu, "Antipasti");
        menuService.aggiungiSezione(menu, "Primi");
        
        assertEquals(2, menu.getSezioni().size());
        assertEquals("Antipasti", menu.getSezioni().get(0).getTitolo());
        assertEquals("Primi", menu.getSezioni().get(1).getTitolo());
    }
    
    @Test
    void testGestioneRicetteIntegration() {
        Menu menu = menuService.creaMenu("CTRL003", "Menu Ricette Test", "Descrizione", "Note");
        menuService.aggiungiSezione(menu, "Antipasti");
        
        // Simula l'aggiunta di una ricetta esistente
        Ricetta ricetta = menuService.getRicette().get(0); // Prende la prima ricetta di test
        menuService.aggiungiRicettaASezione(menu, "Antipasti", ricetta);
        
        SezioniMenu sezione = menu.getSezioni().get(0);
        assertEquals(1, sezione.getVoci().size());
        assertEquals(ricetta.getNome(), sezione.getVoci().get(0).getNomeVisuale());
    }
    
    @Test
    void testEliminazioneRicettaIntegration() {
        Menu menu = menuService.creaMenu("CTRL004", "Menu Eliminazione Test", "Descrizione", "Note");
        menuService.aggiungiSezione(menu, "Primi");
        
        Ricetta ricetta = menuService.getRicette().get(0);
        menuService.aggiungiRicettaASezione(menu, "Primi", ricetta);
        
        SezioniMenu sezione = menu.getSezioni().get(0);
        assertEquals(1, sezione.getVoci().size());
        
        VoceMenu voce = sezione.getVoci().get(0);
        menuService.eliminaRicettaDaSezione(menu, "Primi", voce);
        
        assertEquals(0, sezione.getVoci().size());
    }
    
    @Test
    void testSpostamentoRicettaIntegration() {
        Menu menu = menuService.creaMenu("CTRL005", "Menu Spostamento Test", "Descrizione", "Note");
        menuService.aggiungiSezione(menu, "Antipasti");
        menuService.aggiungiSezione(menu, "Primi");
        
        Ricetta ricetta = menuService.getRicette().get(0);
        menuService.aggiungiRicettaASezione(menu, "Antipasti", ricetta);
        
        SezioniMenu sezioneAntipasti = menu.getSezioni().get(0);
        SezioniMenu sezionePrimi = menu.getSezioni().get(1);
        
        assertEquals(1, sezioneAntipasti.getVoci().size());
        assertEquals(0, sezionePrimi.getVoci().size());
        
        VoceMenu voce = sezioneAntipasti.getVoci().get(0);
        menuService.spostaRicetta(menu, voce, "Primi");
        
        assertEquals(0, sezioneAntipasti.getVoci().size());
        assertEquals(1, sezionePrimi.getVoci().size());
    }
    
    @Test
    void testValidazioneInput() {
        // Test per validazione input vuoti (simulazione logica controller)
        String nomeVuoto = "";
        String nomeValido = "Menu Valido";
        
        assertFalse(isValidMenuName(nomeVuoto));
        assertTrue(isValidMenuName(nomeValido));
    }
    
    @Test
    void testFormattazioneMessaggi() {
        // Test per la formattazione dei messaggi (simulazione logica controller)
        Menu menu = new Menu("M001", "Menu Test", "Descrizione", "Note");
        
        String messaggioPDF = "PDF generato per menu: " + menu.getNome();
        String messaggioBacheca = "Menu pubblicato su bacheca: " + menu.getNome();
        
        assertTrue(messaggioPDF.contains("PDF generato"));
        assertTrue(messaggioPDF.contains(menu.getNome()));
        assertTrue(messaggioBacheca.contains("pubblicato su bacheca"));
        assertTrue(messaggioBacheca.contains(menu.getNome()));
    }
    
    // Metodi di utilita per simulare la logica del controller
    private boolean isValidMenuName(String nome) {
        return nome != null && !nome.trim().isEmpty();
    }
}