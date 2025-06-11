package com.catring.creator;

import com.catring.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per il pattern Creator
 */
class MenuCreatorTest {
    
    private MenuCreator menuCreator;
    
    @BeforeEach
    void setUp() {
        menuCreator = new MenuCreator();
    }
    
    @Test
    void testCreaMenu() {
        // Test del pattern Creator per Menu
        String nome = "Menu Creator Test";
        String descrizione = "Test del pattern Creator";
        String note = "Note di test";
        
        Menu menu = menuCreator.creaMenu(nome, descrizione, note);
        
        assertNotNull(menu);
        assertEquals(nome, menu.getNome());
        assertEquals(descrizione, menu.getDescrizione());
        assertEquals(note, menu.getNote());
        assertNotNull(menu.getId());
        assertTrue(menu.getId().startsWith("M"));
    }
    
    @Test
    void testCreaSezione() {
        // Test del pattern Creator per SezioniMenu
        String titolo = "Antipasti";
        int ordine = 1;
        
        SezioniMenu sezione = menuCreator.creaSezione(titolo, ordine);
        
        assertNotNull(sezione);
        assertEquals(titolo, sezione.getTitolo());
        assertEquals(ordine, sezione.getOrdine());
        assertNotNull(sezione.getId());
        assertTrue(sezione.getId().startsWith("S"));
    }
    
    @Test
    void testCreaVoceMenu() {
        // Test del pattern Creator per VoceMenu
        Ricetta ricetta = menuCreator.creaRicetta("Test Ricetta", "Descrizione", 30, "bozza", "Chef Test");
        
        VoceMenu voce = menuCreator.creaVoceMenu(ricetta);
        
        assertNotNull(voce);
        assertEquals(ricetta.getNome(), voce.getNomeVisuale());
        assertEquals(ricetta.getId(), voce.getRiferimento());
        assertSame(ricetta, voce.getRicetta());
        assertNotNull(voce.getId());
        assertTrue(voce.getId().startsWith("V"));
    }
    
    @Test
    void testCreaRicetta() {
        // Test del pattern Creator per Ricetta
        String nome = "Pasta Test";
        String descrizione = "Pasta di test";
        int tempo = 25;
        String stato = "bozza";
        String autore = "Chef Test";
        
        Ricetta ricetta = menuCreator.creaRicetta(nome, descrizione, tempo, stato, autore);
        
        assertNotNull(ricetta);
        assertEquals(nome, ricetta.getNome());
        assertEquals(descrizione, ricetta.getDescrizione());
        assertEquals(tempo, ricetta.getTempoPreparazione());
        assertEquals(stato, ricetta.getStato());
        assertEquals(autore, ricetta.getAutore());
        assertNotNull(ricetta.getId());
        assertTrue(ricetta.getId().startsWith("R"));
    }
    
    @Test
    void testIDUnivoci() {
        // Test che verifica la generazione di ID univoci
        Menu menu1 = menuCreator.creaMenu("Menu 1", "Desc 1", "Note 1");
        Menu menu2 = menuCreator.creaMenu("Menu 2", "Desc 2", "Note 2");
        
        assertNotEquals(menu1.getId(), menu2.getId(), "Gli ID devono essere univoci");
        
        Ricetta ricetta1 = menuCreator.creaRicetta("Ricetta 1", "Desc 1", 20, "bozza", "Chef 1");
        Ricetta ricetta2 = menuCreator.creaRicetta("Ricetta 2", "Desc 2", 30, "bozza", "Chef 2");
        
        assertNotEquals(ricetta1.getId(), ricetta2.getId(), "Gli ID delle ricette devono essere univoci");
    }
}