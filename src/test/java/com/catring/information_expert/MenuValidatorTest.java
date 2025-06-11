package com.catring.information_expert;

import com.catring.model.Menu;
import com.catring.model.Ricetta;
import com.catring.model.SezioniMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MenuValidatorTest {
    
    private MenuValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new MenuValidator();
    }
    
    @Test
    void testValidazioneNomeMenu() {

        assertTrue(validator.isValidMenuName("Menu Valido"));
        assertTrue(validator.isValidMenuName("ABC"));
        
        assertFalse(validator.isValidMenuName(""));
        assertFalse(validator.isValidMenuName("  "));
        assertFalse(validator.isValidMenuName(null));
        assertFalse(validator.isValidMenuName("AB"));
    }
    
    @Test
    void testValidazioneRicetta() {

        Ricetta ricettaValida = new Ricetta("R001", "Pasta", "Pasta buona", 30, "bozza", "Chef");
        Ricetta ricettaNonValida = new Ricetta("R002", "", "", -10, "invalido", "");
        
        assertTrue(validator.isValidRicetta(ricettaValida));
        assertFalse(validator.isValidRicetta(ricettaNonValida));
        assertFalse(validator.isValidRicetta(null));
    }
    
    @Test
    void testValidazioneTempoPreparazione() {

        assertTrue(validator.isValidTempoPreparazione(30));
        assertTrue(validator.isValidTempoPreparazione(1));
        assertTrue(validator.isValidTempoPreparazione(600));
        
        assertFalse(validator.isValidTempoPreparazione(0));
        assertFalse(validator.isValidTempoPreparazione(-10));
        assertFalse(validator.isValidTempoPreparazione(700));
    }
    
    @Test
    void testValidazioneStato() {

        assertTrue(validator.isValidStato("bozza"));
        assertTrue(validator.isValidStato("pubblicata"));
        
        assertFalse(validator.isValidStato("invalido"));
        assertFalse(validator.isValidStato(""));
        assertFalse(validator.isValidStato(null));
    }
   
    @Test
    void testValidazioneMenuCompleto() {

        Menu menu = new Menu("M001", "Menu Test", "Descrizione valida", "Note");
        SezioniMenu sezione = new SezioniMenu("S001", "Antipasti", 1);
        menu.getSezioni().add(sezione);
        
        assertTrue(validator.isValidMenu(menu));

        Menu menuSenzaSezioni = new Menu("M002", "Menu Vuoto", "Descrizione", "Note");
        assertFalse(validator.hasValidSections(menuSenzaSezioni));
    }
}