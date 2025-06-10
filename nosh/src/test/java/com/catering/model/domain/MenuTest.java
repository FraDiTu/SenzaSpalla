package com.catering.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * TDD Test per la classe Menu
 * Test della logica domain con pattern Information Expert
 */
@DisplayName("Menu Domain Tests")
class MenuTest {
    
    private Menu menu;
    private String menuId;
    private String titolo;
    private String eventoId;
    private LocalDateTime dataCreazione;
    
    @BeforeEach
    void setUp() {
        menuId = "menu-1";
        titolo = "Menu Matrimonio";
        eventoId = "evento-1";
        dataCreazione = LocalDateTime.now();
        
        Map<String, List<VoceMenu>> sezioni = new LinkedHashMap<>();
        sezioni.put("Antipasti", Arrays.asList(
            new VoceMenu("r1", "Bruschette", "Antipasti"),
            new VoceMenu("r2", "Vitello tonnato", "Antipasti")
        ));
        sezioni.put("Primi", Arrays.asList(
            new VoceMenu("r3", "Risotto ai funghi", "Primi")
        ));
        
        menu = new Menu(menuId, titolo, eventoId, dataCreazione, sezioni, null, false);
    }
    
    @Nested
    @DisplayName("Information Expert Tests")
    class InformationExpertTests {
        
        @Test
        @DisplayName("Menu conosce il proprio numero totale di voci")
        void shouldCalculateTotalVoci() {
            // Given: menu con 2 antipasti e 1 primo
            
            // When: richiedo il totale voci
            int totalVoci = menu.getTotalVoci();
            
            // Then: dovrebbe essere 3
            assertEquals(3, totalVoci, "Il menu dovrebbe avere 3 voci totali");
        }
        
        @Test
        @DisplayName("Menu sa se contiene una ricetta specifica")
        void shouldKnowIfContainsRecipe() {
            // Given: menu con ricette specifiche
            
            // When/Then: verifica presenza ricette
            assertTrue(menu.contieneRicetta("r1"), "Menu dovrebbe contenere ricetta r1");
            assertTrue(menu.contieneRicetta("r2"), "Menu dovrebbe contenere ricetta r2");
            assertTrue(menu.contieneRicetta("r3"), "Menu dovrebbe contenere ricetta r3");
            assertFalse(menu.contieneRicetta("r999"), "Menu non dovrebbe contenere ricetta inesistente");
        }
        
        @Test
        @DisplayName("Menu fornisce accesso immutabile alle sezioni")
        void shouldProvideImmutableSections() {
            // Given: menu con sezioni
            Map<String, List<VoceMenu>> sezioni = menu.getSezioni();
            
            // When/Then: tentativo di modifica dovrebbe fallire
            assertThrows(UnsupportedOperationException.class, () -> {
                sezioni.put("Nuova Sezione", new ArrayList<>());
            }, "Le sezioni dovrebbero essere immutabili");
        }
    }
    
    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {
        
        @Test
        @DisplayName("Menu richiede ID non null")
        void shouldRequireNonNullId() {
            assertThrows(NullPointerException.class, () -> {
                new Menu(null, titolo, eventoId, dataCreazione, new HashMap<>(), null, false);
            }, "Menu dovrebbe richiedere ID non null");
        }
        
        @Test
        @DisplayName("Menu richiede titolo non null")
        void shouldRequireNonNullTitle() {
            assertThrows(NullPointerException.class, () -> {
                new Menu(menuId, null, eventoId, dataCreazione, new HashMap<>(), null, false);
            }, "Menu dovrebbe richiedere titolo non null");
        }
        
        @Test
        @DisplayName("Menu può essere creato con sezioni vuote")
        void shouldAllowEmptySections() {
            // Given/When: creazione menu senza sezioni
            Menu emptyMenu = new Menu(menuId, titolo, eventoId, dataCreazione, null, null, false);
            
            // Then: dovrebbe essere valido
            assertNotNull(emptyMenu.getSezioni());
            assertTrue(emptyMenu.getSezioni().isEmpty());
            assertEquals(0, emptyMenu.getTotalVoci());
        }
    }
    
    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Menu con stesso ID sono uguali")
        void menusWithSameIdShouldBeEqual() {
            // Given: due menu con stesso ID ma contenuto diverso
            Menu menu1 = new Menu(menuId, "Titolo 1", eventoId, dataCreazione, new HashMap<>(), null, false);
            Menu menu2 = new Menu(menuId, "Titolo 2", eventoId, dataCreazione, new HashMap<>(), null, true);
            
            // When/Then: dovrebbero essere uguali
            assertEquals(menu1, menu2, "Menu con stesso ID dovrebbero essere uguali");
            assertEquals(menu1.hashCode(), menu2.hashCode(), "Hash code dovrebbe essere uguale");
        }
        
        @Test
        @DisplayName("Menu con ID diversi non sono uguali")
        void menusWithDifferentIdShouldNotBeEqual() {
            // Given: due menu con ID diversi
            Menu menu1 = new Menu("id1", titolo, eventoId, dataCreazione, new HashMap<>(), null, false);
            Menu menu2 = new Menu("id2", titolo, eventoId, dataCreazione, new HashMap<>(), null, false);
            
            // When/Then: non dovrebbero essere uguali
            assertNotEquals(menu1, menu2, "Menu con ID diversi non dovrebbero essere uguali");
        }
    }
}
