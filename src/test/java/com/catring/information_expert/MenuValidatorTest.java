package com.catring.information_expert;

import com.catring.model.Menu;
import com.catring.model.Ricetta;
import com.catring.model.SezioniMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per il pattern Information Expert - Validazione
 */
class MenuValidatorTest {
    
    private MenuValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new MenuValidator();
    }
    
    @Test
    void testValidazioneNomeMenu() {
        // Test Information Expert per validazione nomi menu
        assertTrue(validator.isValidMenuName("Menu Valido"));
        assertTrue(validator.isValidMenuName("ABC"));
        
        assertFalse(validator.isValidMenuName(""));
        assertFalse(validator.isValidMenuName("  "));
        assertFalse(validator.isValidMenuName(null));
        assertFalse(validator.isValidMenuName("AB")); // troppo corto
    }
    
    @Test
    void testValidazioneRicetta() {
        // Test Information Expert per validazione ricette
        Ricetta ricettaValida = new Ricetta("R001", "Pasta", "Pasta buona", 30, "bozza", "Chef");
        Ricetta ricettaNonValida = new Ricetta("R002", "", "", -10, "invalido", "");
        
        assertTrue(validator.isValidRicetta(ricettaValida));
        assertFalse(validator.isValidRicetta(ricettaNonValida));
        assertFalse(validator.isValidRicetta(null));
    }
    
    @Test
    void testValidazioneTempoPreparazione() {
        // Test Information Expert per tempi di preparazione
        assertTrue(validator.isValidTempoPreparazione(30));
        assertTrue(validator.isValidTempoPreparazione(1));
        assertTrue(validator.isValidTempoPreparazione(600));
        
        assertFalse(validator.isValidTempoPreparazione(0));
        assertFalse(validator.isValidTempoPreparazione(-10));
        assertFalse(validator.isValidTempoPreparazione(700)); // troppo lungo
    }
    
    @Test
    void testValidazioneStato() {
        // Test Information Expert per stati ricetta
        assertTrue(validator.isValidStato("bozza"));
        assertTrue(validator.isValidStato("pubblicata"));
        
        assertFalse(validator.isValidStato("invalido"));
        assertFalse(validator.isValidStato(""));
        assertFalse(validator.isValidStato(null));
    }
   
    @Test
    void testValidazioneMenuCompleto() {
        // Test Information Expert per menu completo
        Menu menu = new Menu("M001", "Menu Test", "Descrizione valida", "Note");
        SezioniMenu sezione = new SezioniMenu("S001", "Antipasti", 1);
        menu.getSezioni().add(sezione);
        
        assertTrue(validator.isValidMenu(menu));
        
        // Menu senza sezioni
        Menu menuSenzaSezioni = new Menu("M002", "Menu Vuoto", "Descrizione", "Note");
        assertFalse(validator.hasValidSections(menuSenzaSezioni));
    }
}