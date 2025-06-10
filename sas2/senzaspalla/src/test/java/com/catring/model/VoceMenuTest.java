package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VoceMenuTest {
    
    private VoceMenu voce;
    
    @BeforeEach
    void setUp() {
        voce = new VoceMenu("V001", "Vitello tonnato", "R002", "Specialita della casa");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("V001", voce.getId());
        assertEquals("Vitello tonnato", voce.getNomeVisuale());
        assertEquals("R002", voce.getRiferimento());
        assertEquals("Specialita della casa", voce.getModificheTesto());
    }
    
    @Test
    void testAssociazioneRicetta() {
        Ricetta ricetta = new Ricetta("R002", "Vitello tonnato", "Vitello con salsa tonnata", 60, "pubblicata", "Chef Mario");
        voce.setRicetta(ricetta);
        
        assertNotNull(voce.getRicetta());
        assertEquals("Vitello tonnato", voce.getRicetta().getNome());
        assertEquals("R002", voce.getRicetta().getId());
    }
    
    @Test
    void testModificaNomeVisuale() {
        voce.setNomeVisuale("Girello di fassone con salsa tonnata");
        assertEquals("Girello di fassone con salsa tonnata", voce.getNomeVisuale());
    }
}