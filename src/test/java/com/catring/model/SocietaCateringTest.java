package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SocietaCateringTest {
    
    private SocietaCatering societa;
    private Evento evento;
    private Chef chef;
    
    @BeforeEach
    void setUp() {
        societa = new SocietaCatering("SC001", "Cat & Ring Catering", "Via Roma 123", "12345678901", "info@catring.com");
        evento = new Evento("E001", 
                java.time.LocalDate.of(2024, 6, 15), 
                java.time.LocalDate.of(2024, 6, 15), 
                "Villa Reale", "matrimonio", "Evento elegante");
        chef = new Chef("U001", "Mario", "mario@catring.com", "password123", "Rossi", "Cucina italiana");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("SC001", societa.getId());
        assertEquals("Cat & Ring Catering", societa.getNome());
        assertEquals("Via Roma 123", societa.getIndirizzo());
        assertEquals("12345678901", societa.getPartitaIVA());
        assertEquals("info@catring.com", societa.getContatti());
        
        // Verifica che le liste siano inizializzate
        assertNotNull(societa.getEventi());
        assertNotNull(societa.getUtenti());
        assertTrue(societa.getEventi().isEmpty());
        assertTrue(societa.getUtenti().isEmpty());
    }
    
    @Test
    void testCostruttoreVuoto() {
        SocietaCatering societaVuota = new SocietaCatering();
        
        assertNull(societaVuota.getId());
        assertNull(societaVuota.getNome());
        assertNull(societaVuota.getIndirizzo());
        assertNull(societaVuota.getPartitaIVA());
        assertNull(societaVuota.getContatti());
        
        // Le liste dovrebbero essere comunque inizializzate
        assertNotNull(societaVuota.getEventi());
        assertNotNull(societaVuota.getUtenti());
        assertTrue(societaVuota.getEventi().isEmpty());
        assertTrue(societaVuota.getUtenti().isEmpty());
    }
    
    @Test
    void testAggiungiEvento() {
        societa.getEventi().add(evento);
        
        assertEquals(1, societa.getEventi().size());
        assertEquals("E001", societa.getEventi().get(0).getId());
        assertEquals("Villa Reale", societa.getEventi().get(0).getLuogo());
    }
    
    @Test
    void testAggiungiUtente() {
        societa.getUtenti().add(chef);
        
        assertEquals(1, societa.getUtenti().size());
        assertEquals("U001", societa.getUtenti().get(0).getId());
        assertEquals("Mario", societa.getUtenti().get(0).getNome());
    }
    
    @Test
    void testAggiungiMultipliEventi() {
        Evento evento2 = new Evento("E002", 
                java.time.LocalDate.of(2024, 7, 20), 
                java.time.LocalDate.of(2024, 7, 20), 
                "Hotel Central", "conferenza", "Evento aziendale");
        
        societa.getEventi().add(evento);
        societa.getEventi().add(evento2);
        
        assertEquals(2, societa.getEventi().size());
        assertEquals("Villa Reale", societa.getEventi().get(0).getLuogo());
        assertEquals("Hotel Central", societa.getEventi().get(1).getLuogo());
    }
    
    @Test
    void testAggiungiMultipliUtenti() {
        Organizzatore organizzatore = new Organizzatore("U002", "Luigi", "luigi@catring.com", "password456", "Verdi", "123456789");
        
        societa.getUtenti().add(chef);
        societa.getUtenti().add(organizzatore);
        
        assertEquals(2, societa.getUtenti().size());
        assertEquals("Mario", societa.getUtenti().get(0).getNome());
        assertEquals("Luigi", societa.getUtenti().get(1).getNome());
    }
    
    @Test
    void testModificaDatiSocieta() {
        societa.setNome("Nuovo Nome Catering");
        societa.setIndirizzo("Via Nuova 456");
        societa.setPartitaIVA("98765432100");
        societa.setContatti("nuovo@email.com");
        
        assertEquals("Nuovo Nome Catering", societa.getNome());
        assertEquals("Via Nuova 456", societa.getIndirizzo());
        assertEquals("98765432100", societa.getPartitaIVA());
        assertEquals("nuovo@email.com", societa.getContatti());
    }
    
    @Test
    void testRimuoviEvento() {
        societa.getEventi().add(evento);
        assertEquals(1, societa.getEventi().size());
        
        societa.getEventi().remove(evento);
        assertEquals(0, societa.getEventi().size());
    }
    
    @Test
    void testRimuoviUtente() {
        societa.getUtenti().add(chef);
        assertEquals(1, societa.getUtenti().size());
        
        societa.getUtenti().remove(chef);
        assertEquals(0, societa.getUtenti().size());
    }
    
    @Test
    void testSetListaEventi() {
        java.util.List<Evento> nuovaListaEventi = new java.util.ArrayList<>();
        nuovaListaEventi.add(evento);
        
        societa.setEventi(nuovaListaEventi);
        
        assertEquals(1, societa.getEventi().size());
        assertSame(nuovaListaEventi, societa.getEventi());
    }
    
    @Test
    void testSetListaUtenti() {
        java.util.List<Utente> nuovaListaUtenti = new java.util.ArrayList<>();
        nuovaListaUtenti.add(chef);
        
        societa.setUtenti(nuovaListaUtenti);
        
        assertEquals(1, societa.getUtenti().size());
        assertSame(nuovaListaUtenti, societa.getUtenti());
    }
    
    @Test
    void testDatiCompleti() {
        // Test con una società completa con eventi e utenti
        societa.getEventi().add(evento);
        societa.getUtenti().add(chef);
        
        // Verifica tutti i dati
        assertEquals("SC001", societa.getId());
        assertEquals("Cat & Ring Catering", societa.getNome());
        assertEquals("Via Roma 123", societa.getIndirizzo());
        assertEquals("12345678901", societa.getPartitaIVA());
        assertEquals("info@catring.com", societa.getContatti());
        
        assertEquals(1, societa.getEventi().size());
        assertEquals(1, societa.getUtenti().size());
        
        assertEquals("E001", societa.getEventi().get(0).getId());
        assertEquals("U001", societa.getUtenti().get(0).getId());
    }
}