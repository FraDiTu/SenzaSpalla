package com.catering.model.visitor;

import com.catering.model.domain.Menu;
import com.catering.model.domain.VoceMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * TDD Test per Visitor pattern
 * Test della validazione del menu
 */
@DisplayName("Menu Validation Visitor Tests")
class MenuValidationVisitorTest {
    
    private MenuValidationVisitor visitor;
    
    @BeforeEach
    void setUp() {
        visitor = new MenuValidationVisitor();
    }
    
    @Nested
    @DisplayName("Menu Validation Tests")
    class MenuValidationTests {
        
        @Test
        @DisplayName("Visitor valida menu completo correttamente")
        void shouldValidateCompleteMenuCorrectly() {
            // Given: menu valido
            Map<String, List<VoceMenu>> sezioni = new LinkedHashMap<>();
            sezioni.put("Antipasti", Arrays.asList(
                new VoceMenu("r1", "Bruschette", "Antipasti")
            ));
            
            Menu menu = new Menu("menu-1", "Menu Valido", "evento-1",
                LocalDateTime.now(), sezioni, null, false);
            
            // When: visito menu
            visitor.visitMenu(menu);
            visitor.visitSezione("Antipasti");
            visitor.visitVoce(new VoceMenu("r1", "Bruschette", "Antipasti"));
            
            // Then: dovrebbe essere valido
            assertTrue(visitor.isValid(), "Menu completo dovrebbe essere valido");
            assertTrue(visitor.getResult().isEmpty(), "Non dovrebbero esserci errori");
        }
        
        @Test
        @DisplayName("Visitor rileva menu senza titolo")
        void shouldDetectMenuWithoutTitle() {
            // Given: menu senza titolo
            Menu menu = new Menu("menu-1", null, "evento-1",
                LocalDateTime.now(), new HashMap<>(), null, false);
            
            // When: visito menu
            visitor.visitMenu(menu);
            
            // Then: dovrebbe essere invalido
            assertFalse(visitor.isValid(), "Menu senza titolo dovrebbe essere invalido");
            assertTrue(visitor.getResult().contains("Il menu deve avere un titolo"),
                "Dovrebbe contenere errore per titolo mancante");
        }
        
        @Test
        @DisplayName("Visitor rileva menu con titolo vuoto")
        void shouldDetectMenuWithEmptyTitle() {
            // Given: menu con titolo vuoto
            Menu menu = new Menu("menu-1", "   ", "evento-1",
                LocalDateTime.now(), new HashMap<>(), null, false);
            
            // When: visito menu
            visitor.visitMenu(menu);
            
            // Then: dovrebbe essere invalido
            assertFalse(visitor.isValid(), "Menu con titolo vuoto dovrebbe essere invalido");
            assertTrue(visitor.getResult().contains("Il menu deve avere un titolo"),
                "Dovrebbe contenere errore per titolo vuoto");
        }
        
        @Test
        @DisplayName("Visitor rileva menu senza sezioni")
        void shouldDetectMenuWithoutSections() {
            // Given: menu senza sezioni
            Menu menu = new Menu("menu-1", "Menu Test", "evento-1",
                LocalDateTime.now(), new HashMap<>(), null, false);
            
            // When: visito menu
            visitor.visitMenu(menu);
            
            // Then: dovrebbe essere invalido
            assertFalse(visitor.isValid(), "Menu senza sezioni dovrebbe essere invalido");
            assertTrue(visitor.getResult().contains("Il menu deve avere almeno una sezione"),
                "Dovrebbe contenere errore per sezioni mancanti");
        }
    }
    
    @Nested
    @DisplayName("Section Validation Tests")
    class SectionValidationTests {
        
        @Test
        @DisplayName("Visitor rileva sezione senza nome")
        void shouldDetectSectionWithoutName() {
            // When: visito sezione senza nome
            visitor.visitSezione(null);
            
            // Then: dovrebbe essere invalido
            assertFalse(visitor.isValid(), "Sezione senza nome dovrebbe essere invalida");
            assertTrue(visitor.getResult().contains("Le sezioni devono avere un nome"),
                "Dovrebbe contenere errore per nome sezione mancante");
        }
        
        @Test
        @DisplayName("Visitor rileva sezione con nome vuoto")
        void shouldDetectSectionWithEmptyName() {
            // When: visito sezione con nome vuoto
            visitor.visitSezione("   ");
            
            // Then: dovrebbe essere invalido
            assertFalse(visitor.isValid(), "Sezione con nome vuoto dovrebbe essere invalida");
            assertTrue(visitor.getResult().contains("Le sezioni devono avere un nome"),
                "Dovrebbe contenere errore per nome sezione vuoto");
        }
    }
    
    @Nested
    @DisplayName("Menu Item Validation Tests")
    class MenuItemValidationTests {
        
        @Test
        @DisplayName("Visitor rileva voce menu senza nome")
        void shouldDetectMenuItemWithoutName() {
            // Given: voce menu senza nome
            VoceMenu voce = new VoceMenu("r1", null, "Antipasti");
            
            // When: visito voce
            visitor.visitVoce(voce);
            
            // Then: dovrebbe essere invalido
            assertFalse(visitor.isValid(), "Voce senza nome dovrebbe essere invalida");
            assertTrue(visitor.getResult().contains("Le voci menu devono avere un nome"),
                "Dovrebbe contenere errore per nome voce mancante");
        }
        
        @Test
        @DisplayName("Visitor rileva voce menu senza ricetta")
        void shouldDetectMenuItemWithoutRecipe() {
            // Given: voce menu senza ricetta
            VoceMenu voce = new VoceMenu(null, "Bruschette", "Antipasti");
            
            // When: visito voce
            visitor.visitVoce(voce);
            
            // Then: dovrebbe essere invalido
            assertFalse(visitor.isValid(), "Voce senza ricetta dovrebbe essere invalida");
            assertTrue(visitor.getResult().contains("Le voci menu devono riferirsi a una ricetta"),
                "Dovrebbe contenere errore per ricetta mancante");
        }
    }
    
    @Test
    @DisplayName("Visitor accumula errori multipli")
    void shouldAccumulateMultipleErrors() {
        // Given: menu con errori multipli
        Menu menu = new Menu("menu-1", null, "evento-1",
            LocalDateTime.now(), new HashMap<>(), null, false);
        VoceMenu voceInvalida = new VoceMenu(null, null, "Antipasti");
        
        // When: visito elementi invalidi
        visitor.visitMenu(menu);
        visitor.visitSezione(null);
        visitor.visitVoce(voceInvalida);
        
        // Then: dovrebbe avere errori multipli
        assertFalse(visitor.isValid(), "Dovrebbe essere invalido");
        List<String> errors = visitor.getResult();
        assertTrue(errors.size() >= 3, "Dovrebbe avere almeno 3 errori");
        assertTrue(errors.contains("Il menu deve avere un titolo"));
        assertTrue(errors.contains("Il menu deve avere almeno una sezione"));
        assertTrue(errors.contains("Le sezioni devono avere un nome"));
    }
}