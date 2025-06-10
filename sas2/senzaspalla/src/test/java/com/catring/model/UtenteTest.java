package com.catring.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UtenteTest {
    
    @Test
    void testChefCreazione() {
        Chef chef = new Chef("U001", "Mario", "mario@test.com", "password123", "Rossi", "Cucina italiana");
        
        assertEquals("U001", chef.getId());
        assertEquals("Mario", chef.getNome());
        assertEquals("mario@test.com", chef.getEmail());
        assertEquals("password123", chef.getPassword());
        assertEquals("Rossi", chef.getCognome());
        assertEquals("Cucina italiana", chef.getSpecializzazione());
    }
    
    @Test
    void testOrganizzatoreCreazione() {
        Organizzatore org = new Organizzatore("U002", "Luigi", "luigi@test.com", "password456", "Verdi", "123456789");
        
        assertEquals("U002", org.getId());
        assertEquals("Luigi", org.getNome());
        assertEquals("luigi@test.com", org.getEmail());
        assertEquals("password456", org.getPassword());
        assertEquals("Verdi", org.getCognome());
        assertEquals("123456789", org.getTelefono());
    }
}