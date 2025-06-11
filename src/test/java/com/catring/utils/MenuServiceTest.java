package com.catring.utils;

import com.catring.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MenuServiceTest {
    
    private MenuService menuService;
    
    @BeforeEach
    void setUp() {
        menuService = MenuService.getInstance();
    }
    
    @Test
    void testSingleton() {
        MenuService instance1 = MenuService.getInstance();
        MenuService instance2 = MenuService.getInstance();
        assertSame(instance1, instance2);
    }
    
    @Test
    void testCreaMenu() {
        String id = "TEST001";
        String nome = "Menu Test";
        String descrizione = "Descrizione test";
        String note = "Note test";
        
        Menu menu = menuService.creaMenu(id, nome, descrizione, note);
        
        assertNotNull(menu);
        assertEquals(id, menu.getId());
        assertEquals(nome, menu.getNome());
        assertEquals(descrizione, menu.getDescrizione());
        assertEquals(note, menu.getNote());
        
        assertTrue(menuService.getMenus().contains(menu));
    }
    
    @Test
    void testAggiungiSezione() {
        Menu menu = menuService.creaMenu("TEST002", "Menu per test sezioni", "Desc", "Note");
        int sezioniIniziali = menu.getSezioni().size();
        
        menuService.aggiungiSezione(menu, "Antipasti");
        
        assertEquals(sezioniIniziali + 1, menu.getSezioni().size());
        assertEquals("Antipasti", menu.getSezioni().get(sezioniIniziali).getTitolo());
    }
    
    @Test
    void testAggiungiRicettaASezione() {
        Menu menu = menuService.creaMenu("TEST003", "Menu per test ricette", "Desc", "Note");
        menuService.aggiungiSezione(menu, "Primi");
        
        Ricetta ricetta = new Ricetta("R999", "Pasta test", "Desc", 30, "pubblicata", "Chef Test");
        menuService.aggiungiRicettaASezione(menu, "Primi", ricetta);
        
        SezioniMenu sezione = menu.getSezioni().stream()
                .filter(s -> s.getTitolo().equals("Primi"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(sezione);
        assertEquals(1, sezione.getVoci().size());
        assertEquals("Pasta test", sezione.getVoci().get(0).getNomeVisuale());
    }
    
    @Test
    void testEliminaMenu() {
        Menu menu = menuService.creaMenu("TEST004", "Menu da eliminare", "Desc", "Note");
        assertTrue(menuService.getMenus().contains(menu));
        
        menuService.eliminaMenu(menu);
        assertFalse(menuService.getMenus().contains(menu));
    }
    
    @Test
    void testGetMenuById() {
        Menu menu = menuService.creaMenu("TEST005", "Menu da cercare", "Desc", "Note");
        
        Menu trovato = menuService.getMenuById("TEST005");
        assertNotNull(trovato);
        assertEquals(menu.getId(), trovato.getId());
        assertEquals(menu.getNome(), trovato.getNome());
        
        Menu nonTrovato = menuService.getMenuById("INESISTENTE");
        assertNull(nonTrovato);
    }
    
    @Test
    void testGetRicette() {
        assertNotNull(menuService.getRicette());
        assertFalse(menuService.getRicette().isEmpty());
    }
    
    @Test
    void testGeneraPDF() {
        Menu menu = menuService.creaMenu("TEST006", "Menu PDF", "Desc", "Note");
        String risultato = menuService.generaPDF(menu);
        
        assertNotNull(risultato);
        assertTrue(risultato.contains("PDF generato"));
        assertTrue(risultato.contains(menu.getNome()));
    }
    
    @Test
    void testPubblicaSuBacheca() {
        Menu menu = menuService.creaMenu("TEST007", "Menu Bacheca", "Desc", "Note");
        String risultato = menuService.pubblicaSuBacheca(menu);
        
        assertNotNull(risultato);
        assertTrue(risultato.contains("pubblicato su bacheca"));
        assertTrue(risultato.contains(menu.getNome()));
    }
}