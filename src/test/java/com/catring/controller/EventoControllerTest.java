package com.catring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test semplice per EventoController
 */
class EventoControllerTest {
    
    private EventoController controller;
    
    @BeforeEach
    void setUp() {
        controller = new EventoController();
    }
    
    @Test
    void testControllerCreation() {
        assertNotNull(controller);
        assertNotNull(controller.getEventiList());
    }
    
    @Test
    void testEventiListInitialization() {
        // Il controller dovrebbe avere una lista di eventi inizializzata
        assertNotNull(controller.getEventiList());
        assertTrue(controller.getEventiList().size() >= 0);
    }
}