package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
    
    private Cliente cliente;
    
    @BeforeEach
    void setUp() {
        cliente = new Cliente("C001", "Azienda ABC", "azienda", "info@abc.com");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("C001", cliente.getId());
        assertEquals("Azienda ABC", cliente.getNome());
        assertEquals("azienda", cliente.getTipo());
        assertEquals("info@abc.com", cliente.getContatti());
    }
    
    @Test
    void testTipoCliente() {
        cliente.setTipo("privato");
        assertEquals("privato", cliente.getTipo());
    }
    
    @Test
    void testClientePrivato() {
        Cliente privato = new Cliente("C002", "Mario Rossi", "privato", "mario.rossi@email.com");
        assertEquals("privato", privato.getTipo());
        assertEquals("Mario Rossi", privato.getNome());
    }
}