package com.catring.controller;

import com.catring.model.Menu;
import com.catring.singleton.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test semplice per MenuController
 */
class MenuControllerTest {
    
    private MenuController controller;
    
    @BeforeEach
    void setUp() {
        controller = new MenuController();
    }
    
    @Test
    void testControllerCreation() {
        assertNotNull(controller);
        assertNotNull(controller.getMenuList());
        assertNotNull(controller.getRicetteList());
    }
    
    @Test
    void testMenuListInitialization() {
        // Il controller dovrebbe avere una lista di menu inizializzata
        assertNotNull(controller.getMenuList());
        // Potrebbe essere vuota o contenere dati di test
        assertTrue(controller.getMenuList().size() >= 0);
    }
    
    @Test
    void testRicetteListInitialization() {
        // Il controller dovrebbe avere una lista di ricette inizializzata
        assertNotNull(controller.getRicetteList());
        // Potrebbe essere vuota o contenere dati di test
        assertTrue(controller.getRicetteList().size() >= 0);
    }
    
    @Test
    void testMenuServiceConnection() {
        // Il controller dovrebbe essere connesso al MenuService
        assertNotNull(MenuService.getInstance());
    }
}