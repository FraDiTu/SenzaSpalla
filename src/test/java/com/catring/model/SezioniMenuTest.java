package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SezioniMenuTest {
    
    private SezioniMenu sezione;
    
    @BeforeEach
    void setUp() {
        sezione = new SezioniMenu("S001", "Antipasti", 1);
    }
    
    @Test
    void testCostruttore() {
        assertEquals("S001", sezione.getId());
        assertEquals("Antipasti", sezione.getTitolo());
        assertEquals(1, sezione.getOrdine());
        assertNotNull(sezione.getVoci());
        assertTrue(sezione.getVoci().isEmpty());
    }
    
    @Test
    void testAggiungiVoce() {
        VoceMenu voce = new VoceMenu("V001", "Bruschette", "R001", "Con pomodoro fresco");
        sezione.getVoci().add(voce);
        
        assertEquals(1, sezione.getVoci().size());
        assertEquals("Bruschette", sezione.getVoci().get(0).getNomeVisuale());
    }
    
    @Test
    void testOrdineSezione() {
        sezione.setOrdine(5);
        assertEquals(5, sezione.getOrdine());
    }
}