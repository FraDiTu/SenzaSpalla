package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IngredienteTest {
    
    private Ingrediente ingrediente;
    
    @BeforeEach
    void setUp() {
        ingrediente = new Ingrediente("I001", "Pomodoro", "base", "kg");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("I001", ingrediente.getId());
        assertEquals("Pomodoro", ingrediente.getNome());
        assertEquals("base", ingrediente.getTipo());
        assertEquals("kg", ingrediente.getUnitaMisura());
    }
    
    @Test
    void testTipoIngrediente() {
        ingrediente.setTipo("preparato");
        assertEquals("preparato", ingrediente.getTipo());
    }
    
    @Test
    void testUnitaMisura() {
        ingrediente.setUnitaMisura("grammi");
        assertEquals("grammi", ingrediente.getUnitaMisura());
    }
}