package com.catring.observer;

import com.catring.model.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per il pattern Observer
 */
class MenuObserverTest {
    
    private MenuEventNotifier notifier;
    private TestMenuObserver observer1;
    private TestMenuObserver observer2;
    
    @BeforeEach
    void setUp() {
        notifier = new MenuEventNotifier();
        observer1 = new TestMenuObserver();
        observer2 = new TestMenuObserver();
    }
    
    @Test
    void testAggiungiObserver() {
        // Test aggiunta observer
        assertEquals(0, notifier.getObserverCount());
        
        notifier.addObserver(observer1);
        assertEquals(1, notifier.getObserverCount());
        
        notifier.addObserver(observer2);
        assertEquals(2, notifier.getObserverCount());
        
        // Non dovrebbe aggiungere duplicati
        notifier.addObserver(observer1);
        assertEquals(2, notifier.getObserverCount());
    }
    
    @Test
    void testRimuoviObserver() {
        // Test rimozione observer
        notifier.addObserver(observer1);
        notifier.addObserver(observer2);
        assertEquals(2, notifier.getObserverCount());
        
        notifier.removeObserver(observer1);
        assertEquals(1, notifier.getObserverCount());
        
        notifier.removeObserver(observer2);
        assertEquals(0, notifier.getObserverCount());
    }
    
    @Test
    void testNotificaCreazione() {
        // Test notifica creazione menu
        notifier.addObserver(observer1);
        notifier.addObserver(observer2);
        
        Menu menu = new Menu("M001", "Test Menu", "Descrizione", "Note");
        notifier.notifyMenuCreated(menu);
        
        assertTrue(observer1.menuCreatedCalled);
        assertTrue(observer2.menuCreatedCalled);
        assertEquals(menu, observer1.ultimoMenuCreato);
        assertEquals(menu, observer2.ultimoMenuCreato);
    }
    
    @Test
    void testNotificaModifica() {
        // Test notifica modifica menu
        notifier.addObserver(observer1);
        
        Menu menu = new Menu("M001", "Test Menu", "Descrizione", "Note");
        notifier.notifyMenuUpdated(menu);
        
        assertTrue(observer1.menuUpdatedCalled);
        assertEquals(menu, observer1.ultimoMenuModificato);
    }
    
    @Test
    void testNotificaEliminazione() {
        // Test notifica eliminazione menu
        notifier.addObserver(observer1);
        
        Menu menu = new Menu("M001", "Test Menu", "Descrizione", "Note");
        notifier.notifyMenuDeleted(menu);
        
        assertTrue(observer1.menuDeletedCalled);
        assertEquals(menu, observer1.ultimoMenuEliminato);
    }
    
    /**
     * Classe di test per implementare MenuObserver
     */
    private static class TestMenuObserver implements MenuObserver {
        boolean menuCreatedCalled = false;
        boolean menuUpdatedCalled = false;
        boolean menuDeletedCalled = false;
        Menu ultimoMenuCreato;
        Menu ultimoMenuModificato;
        Menu ultimoMenuEliminato;
        
        @Override
        public void onMenuCreated(Menu menu) {
            menuCreatedCalled = true;
            ultimoMenuCreato = menu;
        }
        
        @Override
        public void onMenuUpdated(Menu menu) {
            menuUpdatedCalled = true;
            ultimoMenuModificato = menu;
        }
        
        @Override
        public void onMenuDeleted(Menu menu) {
            menuDeletedCalled = true;
            ultimoMenuEliminato = menu;
        }
    }
}