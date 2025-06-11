package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class EventoTest {
    
    private Evento evento;
    private Cliente cliente;
    
    @BeforeEach
    void setUp() {
        evento = new Evento("E001", 
                LocalDate.of(2024, 6, 15), 
                LocalDate.of(2024, 6, 15), 
                "Villa Reale", "matrimonio", "Evento elegante");
        cliente = new Cliente("C001", "Mario Rossi", "privato", "mario@email.com");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("E001", evento.getId());
        assertEquals(LocalDate.of(2024, 6, 15), evento.getDataInizio());
        assertEquals(LocalDate.of(2024, 6, 15), evento.getDataFine());
        assertEquals("Villa Reale", evento.getLuogo());
        assertEquals("matrimonio", evento.getTipo());
        assertEquals("Evento elegante", evento.getNote());
        assertEquals(50, evento.getNumeroPersone()); // valore di default
        assertNotNull(evento.getServizi());
        assertTrue(evento.getServizi().isEmpty());
    }
    
    @Test
    void testAssegnaCliente() {
        evento.setCliente(cliente);
        
        assertNotNull(evento.getCliente());
        assertEquals("Mario Rossi", evento.getCliente().getNome());
    }
    
    @Test
    void testNumeroPersone() {
        evento.setNumeroPersone(120);
        assertEquals(120, evento.getNumeroPersone());
    }
    
    @Test
    void testAggiungiServizio() {
        Servizio servizio = new Servizio("S001", "12:30-14:00", "pranzo", "Servizio principale");
        evento.getServizi().add(servizio);
        
        assertEquals(1, evento.getServizi().size());
        assertEquals("pranzo", evento.getServizi().get(0).getTipo());
    }
}