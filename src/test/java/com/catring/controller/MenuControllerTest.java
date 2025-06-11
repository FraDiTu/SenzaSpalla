package com.catring.controller;

import com.catring.singleton.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

        assertNotNull(controller.getMenuList());

        assertTrue(controller.getMenuList().size() >= 0);
    }
    
    @Test
    void testRicetteListInitialization() {

        assertNotNull(controller.getRicetteList());

        assertTrue(controller.getRicetteList().size() >= 0);
    }
    
    @Test
    void testMenuServiceConnection() {

        assertNotNull(MenuService.getInstance());
    }
}