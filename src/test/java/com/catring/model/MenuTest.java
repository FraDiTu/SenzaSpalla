package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MenuTest {
    
    private Menu menu;
    
    @BeforeEach
    void setUp() {
        menu = new Menu("M001", "Menu Test", "Descrizione test", "Note test");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("M001", menu.getId());
        assertEquals("Menu Test", menu.getNome());
        assertEquals("Descrizione test", menu.getDescrizione());
        assertEquals("Note test", menu.getNote());
        assertNotNull(menu.getSezioni());
        assertTrue(menu.getSezioni().isEmpty());
    }
    
    @Test
    void testAggiungiSezione() {
        SezioniMenu sezione = new SezioniMenu("S001", "Antipasti", 1);
        menu.getSezioni().add(sezione);
        
        assertEquals(1, menu.getSezioni().size());
        assertEquals("Antipasti", menu.getSezioni().get(0).getTitolo());
    }
    
    @Test
    void testSettersGetters() {
        menu.setNome("Nuovo Nome");
        menu.setDescrizione("Nuova Descrizione");
        
        assertEquals("Nuovo Nome", menu.getNome());
        assertEquals("Nuova Descrizione", menu.getDescrizione());
    }
}