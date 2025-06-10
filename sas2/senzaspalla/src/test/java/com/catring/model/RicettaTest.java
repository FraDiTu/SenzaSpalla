package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RicettaTest {
    
    private Ricetta ricetta;
    
    @BeforeEach
    void setUp() {
        ricetta = new Ricetta("R001", "Pasta al pomodoro", "Pasta semplice", 20, "pubblicata", "Chef Mario");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("R001", ricetta.getId());
        assertEquals("Pasta al pomodoro", ricetta.getNome());
        assertEquals("Pasta semplice", ricetta.getDescrizione());
        assertEquals(20, ricetta.getTempoPreparazione());
        assertEquals("pubblicata", ricetta.getStato());
        assertEquals("Chef Mario", ricetta.getAutore());
    }
    
    @Test
    void testListeVuote() {
        assertNotNull(ricetta.getIngredienti());
        assertNotNull(ricetta.getPreparazioni());
        assertNotNull(ricetta.getTags());
        assertTrue(ricetta.getIngredienti().isEmpty());
        assertTrue(ricetta.getPreparazioni().isEmpty());
        assertTrue(ricetta.getTags().isEmpty());
    }
    
    @Test
    void testAggiungiIngrediente() {
        Ingrediente ingrediente = new Ingrediente("I001", "Pomodoro", "base", "kg");
        ricetta.getIngredienti().add(ingrediente);
        
        assertEquals(1, ricetta.getIngredienti().size());
        assertEquals("Pomodoro", ricetta.getIngredienti().get(0).getNome());
    }
    
    @Test
    void testAggiungiTag() {
        Tag tag = new Tag("vegetariano");
        ricetta.getTags().add(tag);
        
        assertEquals(1, ricetta.getTags().size());
        assertEquals("vegetariano", ricetta.getTags().get(0).getNome());
    }
}