// src/test/java/com/catering/service/MenuServiceTest.java
package com.catering.service;

import com.catering.model.domain.*;
import com.catering.model.observer.MenuObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Test per MenuService
 * Test dei pattern Controller e Observer
 * Versione semplificata e corretta
 */
@DisplayName("Menu Service Tests")
class MenuServiceTest {
    
    private MenuService menuService;
    
    @Mock
    private MenuObserver observer;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Reset singleton per test isolati
        resetMenuService();
        menuService = MenuService.getInstance();
        menuService.addObserver(observer);
    }
    
    private void resetMenuService() {
        try {
            var field = MenuService.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
            // Ignora errori di reflection nei test
        }
    }
    
    @Nested
    @DisplayName("Controller Pattern Tests")
    class ControllerPatternTests {
        
        @Test
        @DisplayName("Service coordina creazione menu con validazione")
        void shouldCoordinateMenuCreationWithValidation() {
            // Given: parametri validi
            String titolo = "Menu Test";
            String eventoId = "evento-1";
            
            // When: creo menu
            Menu menu = menuService.createMenu(titolo, eventoId);
            
            // Then: menu dovrebbe essere creato correttamente
            assertNotNull(menu, "Menu dovrebbe essere creato");
            assertEquals(titolo, menu.getTitolo(), "Titolo dovrebbe corrispondere");
            assertEquals(eventoId, menu.getEventoId(), "Evento ID dovrebbe corrispondere");
            assertFalse(menu.isPubblicato(), "Menu dovrebbe essere in bozza");
        }
        
        @Test
        @DisplayName("Service valida parametri durante creazione")
        void shouldValidateParametersDuringCreation() {
            // Given/When/Then: titolo null dovrebbe fallire
            assertThrows(IllegalArgumentException.class,
                () -> menuService.createMenu(null, "evento-1"),
                "Creazione con titolo null dovrebbe fallire");
            
            // Given/When/Then: titolo vuoto dovrebbe fallire
            assertThrows(IllegalArgumentException.class,
                () -> menuService.createMenu("", "evento-1"),
                "Creazione con titolo vuoto dovrebbe fallire");
            
            // Given/When/Then: titolo solo spazi dovrebbe fallire
            assertThrows(IllegalArgumentException.class,
                () -> menuService.createMenu("   ", "evento-1"),
                "Creazione con titolo solo spazi dovrebbe fallire");
        }
        
        @Test
        @DisplayName("Service coordina aggiunta sezioni con controllo stato")
        void shouldCoordinateSectionAdditionWithStateCheck() {
            // Given: menu esistente
            final Menu menu = menuService.createMenu("Menu Test", "evento-1");
            String nomeSezione = "Antipasti";
            
            // When: aggiungo sezione
            Menu menuAggiornato = menuService.addSezione(menu.getId(), nomeSezione);
            
            // Then: sezione dovrebbe essere aggiunta
            assertTrue(menuAggiornato.getSezioni().containsKey(nomeSezione),
                "Menu dovrebbe contenere la nuova sezione");
            assertTrue(menuAggiornato.getSezioni().get(nomeSezione).isEmpty(),
                "Nuova sezione dovrebbe essere vuota");
        }
        
        @Test
        @DisplayName("Service coordina aggiunta voci menu con validazione")
        void shouldCoordinateMenuItemAdditionWithValidation() {
            // Given: menu con sezione e ricetta esistente nel repository
            final Menu menu = menuService.createMenu("Menu Test", "evento-1");
            final Menu menuWithSection = menuService.addSezione(menu.getId(), "Antipasti");
            
            // When: aggiungo voce menu (usando ricetta di esempio dal repository)
            Menu menuAggiornato = menuService.addVoceMenu(
                menuWithSection.getId(), "r1", "Bruschette Speciali", "Antipasti");
            
            // Then: voce dovrebbe essere aggiunta
            assertEquals(1, menuAggiornato.getSezioni().get("Antipasti").size(),
                "Sezione dovrebbe avere 1 voce");
            
            VoceMenu voce = menuAggiornato.getSezioni().get("Antipasti").get(0);
            assertEquals("r1", voce.getRicettaId(), "Ricetta ID dovrebbe corrispondere");
            assertEquals("Bruschette Speciali", voce.getNomeVisualizzato(),
                "Nome visualizzato dovrebbe corrispondere");
        }
    }
    
    @Nested
    @DisplayName("Observer Pattern Tests")
    class ObserverPatternTests {
        
        @Test
        @DisplayName("Service notifica observer quando menu cambia")
        void shouldNotifyObserversWhenMenuChanges() {
            // Given: observer registrato
            
            // When: creo menu
            Menu menu = menuService.createMenu("Menu Test", "evento-1");
            
            // Then: observer dovrebbe essere notificato
            verify(observer, times(1)).onMenuChanged(menu);
        }
        
        @Test
        @DisplayName("Service notifica observer quando aggiunge sezione")
        void shouldNotifyObserversWhenSectionAdded() {
            // Given: menu esistente
            final Menu menu = menuService.createMenu("Menu Test", "evento-1");
            reset(observer); // Reset mock per isolare il test
            
            // When: aggiungo sezione
            menuService.addSezione(menu.getId(), "Antipasti");
            
            // Then: observer dovrebbe essere notificato
            verify(observer, times(1)).onMenuSectionAdded("Antipasti");
            verify(observer, times(1)).onMenuChanged(any(Menu.class));
        }
        
        @Test
        @DisplayName("Service notifica observer quando aggiunge voce menu")
        void shouldNotifyObserversWhenMenuItemAdded() {
            // Given: menu con sezione
            final Menu menu = menuService.createMenu("Menu Test", "evento-1");
            final Menu menuWithSection = menuService.addSezione(menu.getId(), "Antipasti");
            reset(observer);
            
            // When: aggiungo voce menu
            menuService.addVoceMenu(menuWithSection.getId(), "r1", "Bruschette", "Antipasti");
            
            // Then: observer dovrebbe essere notificato
            verify(observer, times(1)).onMenuItemAdded("Antipasti", "Bruschette");
            verify(observer, times(1)).onMenuChanged(any(Menu.class));
        }
        
        @Test
        @DisplayName("Service gestisce multiple observer")
        void shouldHandleMultipleObservers() {
            // Given: secondo observer
            MenuObserver observer2 = mock(MenuObserver.class);
            menuService.addObserver(observer2);
            
            // When: creo menu
            Menu menu = menuService.createMenu("Menu Test", "evento-1");
            
            // Then: entrambi gli observer dovrebbero essere notificati
            verify(observer, times(1)).onMenuChanged(menu);
            verify(observer2, times(1)).onMenuChanged(menu);
        }
        
        @Test
        @DisplayName("Service rimuove observer correttamente")
        void shouldRemoveObserversCorrectly() {
            // Given: observer registrato
            assertEquals(1, menuService.getObservers().size());
            
            // When: rimuovo observer
            menuService.removeObserver(observer);
            
            // Then: observer dovrebbe essere rimosso
            assertEquals(0, menuService.getObservers().size());
            
            // When: creo menu dopo rimozione
            Menu menu = menuService.createMenu("Menu Test", "evento-1");
            
            // Then: observer non dovrebbe essere notificato
            verify(observer, never()).onMenuChanged(menu);
        }
    }
    
    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {
        
        @Test
        @DisplayName("Service gestisce pubblicazione menu")
        void shouldHandleMenuPublication() {
            // Given: menu con contenuto valido
            final Menu menu = menuService.createMenu("Menu Test", "evento-1");
            final Menu menuWithSection = menuService.addSezione(menu.getId(), "Antipasti");
            final Menu menuWithItem = menuService.addVoceMenu(menuWithSection.getId(), "r1", "Bruschette", "Antipasti");
            reset(observer);
            
            // When: pubblico menu
            Menu menuPubblicato = menuService.publishMenu(menuWithItem.getId());
            
            // Then: menu dovrebbe essere pubblicato
            assertTrue(menuPubblicato.isPubblicato(), "Menu dovrebbe essere pubblicato");
            verify(observer, times(1)).onMenuPublished(menuPubblicato);
        }
        
        @Test
        @DisplayName("Service impedisce modifica menu pubblicato")
        void shouldPreventModificationOfPublishedMenu() {
            // Given: menu pubblicato
            final Menu menu = menuService.createMenu("Menu Test", "evento-1");
            final Menu menuWithSection = menuService.addSezione(menu.getId(), "Antipasti");
            final Menu menuWithItem = menuService.addVoceMenu(menuWithSection.getId(), "r1", "Bruschette", "Antipasti");
            menuService.publishMenu(menuWithItem.getId());
            
            // When/Then: tentativo di modifica dovrebbe fallire
            assertThrows(IllegalStateException.class,
                () -> menuService.addSezione(menuWithItem.getId(), "Primi"),
                "Non dovrebbe essere possibile modificare menu pubblicato");
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Service gestisce menu inesistente")
        void shouldHandleNonExistentMenu() {
            // Given: ID menu inesistente
            String menuIdInesistente = "menu-inesistente";
            
            // When/Then: operazioni dovrebbero fallire con eccezione appropriata
            assertThrows(IllegalArgumentException.class,
                () -> menuService.addSezione(menuIdInesistente, "Sezione"),
                "Dovrebbe fallire per menu inesistente");
            
            assertThrows(IllegalArgumentException.class,
                () -> menuService.publishMenu(menuIdInesistente),
                "Dovrebbe fallire per menu inesistente");
        }
        
        @Test
        @DisplayName("Service gestisce ricetta inesistente")
        void shouldHandleNonExistentRecipe() {
            // Given: menu esistente ma ricetta inesistente
            final Menu menu = menuService.createMenu("Menu Test", "evento-1");
            final Menu menuWithSection = menuService.addSezione(menu.getId(), "Antipasti");
            
            // When/Then: aggiunta voce con ricetta inesistente dovrebbe fallire
            assertThrows(IllegalArgumentException.class,
                () -> menuService.addVoceMenu(menuWithSection.getId(), "ricetta-inesistente", "Nome", "Antipasti"),
                "Dovrebbe fallire per ricetta inesistente");
        }
    }
}