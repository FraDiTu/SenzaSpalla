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
    void testCostruttoreVuoto() {
        Dose doseVuota = new Dose();
        assertEquals(0.0, doseVuota.getQuantitativo());
        assertNull(doseVuota.getUnitaMisura());
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
    
    @Test
    void testQuantitativoZero() {
        dose.setQuantitativo(0.0);
        assertEquals(0.0, dose.getQuantitativo());
    }
    
    @Test
    void testQuantitativoNegativo() {

        dose.setQuantitativo(-100.0);
        assertEquals(-100.0, dose.getQuantitativo());
    }
    
    @Test
    void testModificaUnitaMisura() {
        dose.setUnitaMisura("kg");
        assertEquals("kg", dose.getUnitaMisura());
        
        dose.setUnitaMisura("litri");
        assertEquals("litri", dose.getUnitaMisura());
        
        dose.setUnitaMisura("cucchiai");
        assertEquals("cucchiai", dose.getUnitaMisura());
    }
    
    @Test
    void testUnitaMisuraVuota() {
        dose.setUnitaMisura("");
        assertEquals("", dose.getUnitaMisura());
        
        dose.setUnitaMisura(null);
        assertNull(dose.getUnitaMisura());
    }
    
    @Test
    void testDiverseUnitaMisura() {

        String[] unitaComuni = {"grammi", "kg", "litri", "ml", "cucchiai", "cucchiaini", "tazze", "pizzico", "qb", "pezzi", "spicchi"};
        
        for (String unita : unitaComuni) {
            dose.setUnitaMisura(unita);
            assertEquals(unita, dose.getUnitaMisura());
        }
    }
    
    @Test
    void testQuantitaPrecise() {

        dose.setQuantitativo(123.456789);
        assertEquals(123.456789, dose.getQuantitativo(), 0.000001);
    }
    
    @Test
    void testDoseCompleta() {

        Dose doseCompleta = new Dose(750.5, "ml");
        
        assertEquals(750.5, doseCompleta.getQuantitativo());
        assertEquals("ml", doseCompleta.getUnitaMisura());

        doseCompleta.setQuantitativo(1.5);
        doseCompleta.setUnitaMisura("litri");
        
        assertEquals(1.5, doseCompleta.getQuantitativo());
        assertEquals("litri", doseCompleta.getUnitaMisura());
    }
    
    @Test
    void testCreazioneDiverseDosi() {

        Dose dosePomodoro = new Dose(600, "grammi");
        Dose doseOlio = new Dose(4, "cucchiai");
        Dose doseAglio = new Dose(2, "spicchi");
        Dose doseSale = new Dose(1, "pizzico");
        
        assertEquals(600, dosePomodoro.getQuantitativo());
        assertEquals("grammi", dosePomodoro.getUnitaMisura());
        
        assertEquals(4, doseOlio.getQuantitativo());
        assertEquals("cucchiai", doseOlio.getUnitaMisura());
        
        assertEquals(2, doseAglio.getQuantitativo());
        assertEquals("spicchi", doseAglio.getUnitaMisura());
        
        assertEquals(1, doseSale.getQuantitativo());
        assertEquals("pizzico", doseSale.getUnitaMisura());
    }
}