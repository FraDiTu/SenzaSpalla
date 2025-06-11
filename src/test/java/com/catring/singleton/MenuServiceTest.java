package com.catring.singleton;

import com.catring.model.Menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per il pattern Singleton
 */
class MenuServiceTest {
    
    private MenuService menuService;
    
    @BeforeEach
    void setUp() {
        menuService = MenuService.getInstance();
    }
    
    @Test
    void testSingletonPattern() {
        // Test che verifica il pattern Singleton
        MenuService instance1 = MenuService.getInstance();
        MenuService instance2 = MenuService.getInstance();
        
        assertSame(instance1, instance2, "Le istanze devono essere identiche (Singleton)");
        assertSame(menuService, instance1, "Tutte le istanze devono essere uguali");
    }
    
    @Test
    void testCloneNotSupported() {
        // Test che verifica che il clone non sia supportato
        assertThrows(CloneNotSupportedException.class, () -> {
            menuService.clone();
        }, "Il clone non deve essere supportato per un Singleton");
    }
    
    @Test
    void testCreaMenuConCreator() {
        String nome = "Menu Test Singleton";
        String descrizione = "Test per pattern Singleton";
        String note = "Note di test";
        
        Menu menu = menuService.creaMenu(nome, descrizione, note);
        
        assertNotNull(menu);
        assertEquals(nome, menu.getNome());
        assertTrue(menuService.getMenus().contains(menu));
    }
    
    @Test
    void testConsultaRicettario() {
        var ricette = menuService.consultaRicettario();
        assertNotNull(ricette);
        assertFalse(ricette.isEmpty(), "Dovrebbero esserci ricette di test");
    }
}