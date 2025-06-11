package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServizioTest {
    
    private Servizio servizio;
    private Menu menu;
    
    @BeforeEach
    void setUp() {
        servizio = new Servizio("S001", "12:30-14:00", "pranzo", "Servizio per matrimonio");
        menu = new Menu("M001", "Menu Matrimonio", "Menu elegante", "Note speciali");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("S001", servizio.getId());
        assertEquals("12:30-14:00", servizio.getFasciaOraria());
        assertEquals("pranzo", servizio.getTipo());
        assertEquals("Servizio per matrimonio", servizio.getNote());
        assertNull(servizio.getMenu()); // Menu non ancora assegnato
    }
    
    @Test
    void testCostruttoreVuoto() {
        Servizio servizioVuoto = new Servizio();
        
        assertNull(servizioVuoto.getId());
        assertNull(servizioVuoto.getFasciaOraria());
        assertNull(servizioVuoto.getTipo());
        assertNull(servizioVuoto.getNote());
        assertNull(servizioVuoto.getMenu());
    }
    
    @Test
    void testAssegnaMenu() {
        servizio.setMenu(menu);
        
        assertNotNull(servizio.getMenu());
        assertEquals("M001", servizio.getMenu().getId());
        assertEquals("Menu Matrimonio", servizio.getMenu().getNome());
    }
    
    @Test
    void testModificaFasciaOraria() {
        servizio.setFasciaOraria("19:00-22:00");
        assertEquals("19:00-22:00", servizio.getFasciaOraria());
    }
    
    @Test
    void testModificaTipo() {
        servizio.setTipo("cena");
        assertEquals("cena", servizio.getTipo());
        
        servizio.setTipo("aperitivo");
        assertEquals("aperitivo", servizio.getTipo());
        
        servizio.setTipo("coffee break");
        assertEquals("coffee break", servizio.getTipo());
    }
    
    @Test
    void testModificaNote() {
        String nuoveNote = "Servizio con allestimento speciale per cerimonia";
        servizio.setNote(nuoveNote);
        assertEquals(nuoveNote, servizio.getNote());
    }
    
    @Test
    void testNoteVuote() {
        servizio.setNote("");
        assertEquals("", servizio.getNote());
        
        servizio.setNote(null);
        assertNull(servizio.getNote());
    }
    
    @Test
    void testDiversiTipiServizio() {
        String[] tipiServizio = {
            "colazione", "pranzo", "cena", "aperitivo", 
            "coffee break", "buffet", "cocktail", "brunch"
        };
        
        for (String tipo : tipiServizio) {
            servizio.setTipo(tipo);
            assertEquals(tipo, servizio.getTipo());
        }
    }
    
    @Test
    void testDiverseFasceOrarie() {
        String[] fasceOrarie = {
            "08:00-10:00",  // colazione
            "12:30-14:30",  // pranzo
            "19:00-22:00",  // cena
            "18:00-20:00",  // aperitivo
            "10:00-10:30",  // coffee break mattino
            "15:30-16:00"   // coffee break pomeriggio
        };
        
        for (String fascia : fasceOrarie) {
            servizio.setFasciaOraria(fascia);
            assertEquals(fascia, servizio.getFasciaOraria());
        }
    }
    
    @Test
    void testCambiaMenu() {
        // Assegna primo menu
        servizio.setMenu(menu);
        assertEquals("Menu Matrimonio", servizio.getMenu().getNome());
        
        // Cambia con un nuovo menu
        Menu nuovoMenu = new Menu("M002", "Menu Aziendale", "Menu per eventi business", "Note aziendali");
        servizio.setMenu(nuovoMenu);
        
        assertEquals("M002", servizio.getMenu().getId());
        assertEquals("Menu Aziendale", servizio.getMenu().getNome());
    }
    
    @Test
    void testRimuoviMenu() {
        servizio.setMenu(menu);
        assertNotNull(servizio.getMenu());
        
        servizio.setMenu(null);
        assertNull(servizio.getMenu());
    }
    
    @Test
    void testServizioCompleto() {
        // Test di un servizio completo con tutti i campi
        servizio.setMenu(menu);
        
        assertEquals("S001", servizio.getId());
        assertEquals("12:30-14:00", servizio.getFasciaOraria());
        assertEquals("pranzo", servizio.getTipo());
        assertEquals("Servizio per matrimonio", servizio.getNote());
        assertNotNull(servizio.getMenu());
        assertEquals("Menu Matrimonio", servizio.getMenu().getNome());
    }
    
    @Test
    void testServiziMultipli() {
        // Test per verificare la gestione di più servizi
        Servizio aperitivo = new Servizio("S002", "18:00-20:00", "aperitivo", "Aperitivo di benvenuto");
        Servizio cena = new Servizio("S003", "20:30-23:00", "cena", "Cena principale");
        
        Menu menuAperitivo = new Menu("M002", "Menu Aperitivo", "Finger food e cocktail", "");
        Menu menuCena = new Menu("M003", "Menu Cena", "Menu principale matrimonio", "");
        
        aperitivo.setMenu(menuAperitivo);
        cena.setMenu(menuCena);
        
        assertEquals("aperitivo", aperitivo.getTipo());
        assertEquals("Menu Aperitivo", aperitivo.getMenu().getNome());
        
        assertEquals("cena", cena.getTipo());
        assertEquals("Menu Cena", cena.getMenu().getNome());
    }
    
    @Test
    void testModificaId() {
        servizio.setId("S999");
        assertEquals("S999", servizio.getId());
    }
    
    @Test
    void testServizioConNoteDettagliate() {
        String noteDettagliate = "SERVIZIO PRANZO MATRIMONIO\n" +
                                "- Allestimento tavoli ore 11:30\n" +
                                "- Servizio inizia ore 12:30\n" +
                                "- Menu a 4 portate\n" +
                                "- Staff: 2 camerieri + 1 sommelier\n" +
                                "- Chiusura prevista ore 14:00";
        
        servizio.setNote(noteDettagliate);
        assertEquals(noteDettagliate, servizio.getNote());
        assertTrue(servizio.getNote().contains("Staff:"));
        assertTrue(servizio.getNote().contains("Allestimento"));
    }
    
    @Test
    void testFasceOrarieComplesse() {
        // Test con fasce orarie più complesse
        String fasciaEstesa = "19:00-01:00"; // Servizio che attraversa la mezzanotte
        servizio.setFasciaOraria(fasciaEstesa);
        assertEquals(fasciaEstesa, servizio.getFasciaOraria());
        
        String fasciaConMinuti = "12:15-14:45";
        servizio.setFasciaOraria(fasciaConMinuti);
        assertEquals(fasciaConMinuti, servizio.getFasciaOraria());
    }
}