package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RicettaTest {
    
    private Ricetta ricetta;
    private Ingrediente ingrediente1;
    private Ingrediente ingrediente2;
    private Dose dose1;
    private Dose dose2;
    
    @BeforeEach
    void setUp() {
        ricetta = new Ricetta("R001", "Pasta al pomodoro", "Pasta semplice", 20, "pubblicata", "Chef Mario");
        
        ingrediente1 = new Ingrediente("I001", "Pomodoro", "verdura", "kg");
        ingrediente2 = new Ingrediente("I002", "Pasta", "cereale", "kg");
        
        dose1 = new Dose(500, "grammi");
        dose2 = new Dose(400, "grammi");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("R001", ricetta.getId());
        assertEquals("Pasta al pomodoro", ricetta.getNome());
        assertEquals("Pasta semplice", ricetta.getDescrizione());
        assertEquals(20, ricetta.getTempoPreparazione());
        assertEquals("pubblicata", ricetta.getStato());
        assertEquals("Chef Mario", ricetta.getAutore());
        assertEquals(4, ricetta.getNumeroPorte()); // valore di default
    }
    
    @Test
    void testListeVuote() {
        assertNotNull(ricetta.getIngredienti());
        assertNotNull(ricetta.getDosi());
        assertNotNull(ricetta.getPreparazioni());
        assertNotNull(ricetta.getTags());
        assertTrue(ricetta.getIngredienti().isEmpty());
        assertTrue(ricetta.getDosi().isEmpty());
        assertTrue(ricetta.getPreparazioni().isEmpty());
        assertTrue(ricetta.getTags().isEmpty());
    }
    
    @Test
    void testAggiungiIngrediente() {
        ricetta.aggiungiIngrediente(ingrediente1, dose1);
        
        assertEquals(1, ricetta.getIngredienti().size());
        assertEquals(1, ricetta.getDosi().size());
        assertEquals("Pomodoro", ricetta.getIngredienti().get(0).getNome());
        assertEquals(500, ricetta.getDosi().get(0).getQuantitativo());
        assertEquals("grammi", ricetta.getDosi().get(0).getUnitaMisura());
    }
    
    @Test
    void testAggiungiMultipliIngredienti() {
        ricetta.aggiungiIngrediente(ingrediente1, dose1);
        ricetta.aggiungiIngrediente(ingrediente2, dose2);
        
        assertEquals(2, ricetta.getIngredienti().size());
        assertEquals(2, ricetta.getDosi().size());
        
        assertEquals("Pomodoro", ricetta.getIngredienti().get(0).getNome());
        assertEquals("Pasta", ricetta.getIngredienti().get(1).getNome());
        
        assertEquals(500, ricetta.getDosi().get(0).getQuantitativo());
        assertEquals(400, ricetta.getDosi().get(1).getQuantitativo());
    }
    
    @Test
    void testGetDosePerIngrediente() {
        ricetta.aggiungiIngrediente(ingrediente1, dose1);
        ricetta.aggiungiIngrediente(ingrediente2, dose2);
        
        Dose dosePomodoro = ricetta.getDosePerIngrediente(ingrediente1);
        Dose dosePasta = ricetta.getDosePerIngrediente(ingrediente2);
        
        assertNotNull(dosePomodoro);
        assertNotNull(dosePasta);
        assertEquals(500, dosePomodoro.getQuantitativo());
        assertEquals(400, dosePasta.getQuantitativo());
        
        // Test con ingrediente non presente
        Ingrediente ingredienteNonPresente = new Ingrediente("I999", "Sale", "condimento", "grammi");
        Dose doseNonTrovata = ricetta.getDosePerIngrediente(ingredienteNonPresente);
        assertNull(doseNonTrovata);
    }
    
    @Test
    void testRimuoviIngrediente() {
        ricetta.aggiungiIngrediente(ingrediente1, dose1);
        ricetta.aggiungiIngrediente(ingrediente2, dose2);
        
        assertEquals(2, ricetta.getIngredienti().size());
        assertEquals(2, ricetta.getDosi().size());
        
        ricetta.rimuoviIngrediente(ingrediente1);
        
        assertEquals(1, ricetta.getIngredienti().size());
        assertEquals(1, ricetta.getDosi().size());
        assertEquals("Pasta", ricetta.getIngredienti().get(0).getNome());
        assertEquals(400, ricetta.getDosi().get(0).getQuantitativo());
    }
    
    @Test
    void testRimuoviIngredienteNonPresente() {
        ricetta.aggiungiIngrediente(ingrediente1, dose1);
        
        assertEquals(1, ricetta.getIngredienti().size());
        
        Ingrediente ingredienteNonPresente = new Ingrediente("I999", "Sale", "condimento", "grammi");
        ricetta.rimuoviIngrediente(ingredienteNonPresente);
        
        // Non dovrebbe cambiare nulla
        assertEquals(1, ricetta.getIngredienti().size());
        assertEquals("Pomodoro", ricetta.getIngredienti().get(0).getNome());
    }
    
    @Test
    void testAggiungiTag() {
        Tag tag1 = new Tag("vegetariano");
        Tag tag2 = new Tag("veloce");
        
        ricetta.getTags().add(tag1);
        ricetta.getTags().add(tag2);
        
        assertEquals(2, ricetta.getTags().size());
        assertEquals("vegetariano", ricetta.getTags().get(0).getNome());
        assertEquals("veloce", ricetta.getTags().get(1).getNome());
    }
    
    @Test
    void testAggiungiPreparazione() {
        Preparazione prep = new Preparazione("P001", "Bollire pasta", "Mettere in acqua bollente", 10, "Note", "bozza", "Chef");
        ricetta.getPreparazioni().add(prep);
        
        assertEquals(1, ricetta.getPreparazioni().size());
        assertEquals("Bollire pasta", ricetta.getPreparazioni().get(0).getNome());
    }
    
    @Test
    void testNumeroPorte() {
        assertEquals(4, ricetta.getNumeroPorte()); // default
        
        ricetta.setNumeroPorte(6);
        assertEquals(6, ricetta.getNumeroPorte());
        
        ricetta.setNumeroPorte(2);
        assertEquals(2, ricetta.getNumeroPorte());
    }
    
    @Test
    void testRicettaCompleta() {
        // Test di una ricetta completa con tutti gli elementi
        ricetta.setNumeroPorte(6);
        
        // Aggiungi ingredienti
        ricetta.aggiungiIngrediente(ingrediente1, dose1);
        ricetta.aggiungiIngrediente(ingrediente2, dose2);
        
        // Aggiungi tag
        ricetta.getTags().add(new Tag("primo"));
        ricetta.getTags().add(new Tag("vegetariano"));
        
        // Aggiungi preparazione
        Preparazione prep = new Preparazione("P001", "Preparazione", "Istruzioni", 15, "Note", "pubblicata", "Chef");
        ricetta.getPreparazioni().add(prep);
        
        // Verifica tutti i componenti
        assertEquals(6, ricetta.getNumeroPorte());
        assertEquals(2, ricetta.getIngredienti().size());
        assertEquals(2, ricetta.getDosi().size());
        assertEquals(2, ricetta.getTags().size());
        assertEquals(1, ricetta.getPreparazioni().size());
        
        // Verifica che le dosi corrispondano agli ingredienti
        assertEquals("Pomodoro", ricetta.getIngredienti().get(0).getNome());
        assertEquals(500, ricetta.getDosi().get(0).getQuantitativo());
        
        assertEquals("Pasta", ricetta.getIngredienti().get(1).getNome());
        assertEquals(400, ricetta.getDosi().get(1).getQuantitativo());
    }
}