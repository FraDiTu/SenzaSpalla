package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoseTest {
    
    private Dose dose;
    
    @BeforeEach
    void setUp() {
        dose = new Dose(500.0, "grammi");
    }
    
    @Test
    void testCostruttore() {
        assertEquals(500.0, dose.getQuantitativo());
        assertEquals("grammi", dose.getUnitaMisura());
    }
    
    @Test
    void testModificaQuantitativo() {
        dose.setQuantitativo(250.0);
        assertEquals(250.0, dose.getQuantitativo());
    }
    
    @Test
    void testQuantitativoDecimale() {
        dose.setQuantitativo(1.5);
        assertEquals(1.5, dose.getQuantitativo());
    }
}