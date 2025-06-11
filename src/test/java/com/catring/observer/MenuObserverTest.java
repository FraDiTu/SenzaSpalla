package com.catring.observer;

import com.catring.model.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals(0, notifier.getObserverCount());
        
        notifier.addObserver(observer1);
        assertEquals(1, notifier.getObserverCount());
        
        notifier.addObserver(observer2);
        assertEquals(2, notifier.getObserverCount());

        notifier.addObserver(observer1);
        assertEquals(2, notifier.getObserverCount());
    }
    
    @Test
    void testRimuoviObserver() {

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

        notifier.addObserver(observer1);
        
        Menu menu = new Menu("M001", "Test Menu", "Descrizione", "Note");
        notifier.notifyMenuUpdated(menu);
        
        assertTrue(observer1.menuUpdatedCalled);
        assertEquals(menu, observer1.ultimoMenuModificato);
    }
    
    @Test
    void testNotificaEliminazione() {

        notifier.addObserver(observer1);
        
        Menu menu = new Menu("M001", "Test Menu", "Descrizione", "Note");
        notifier.notifyMenuDeleted(menu);
        
        assertTrue(observer1.menuDeletedCalled);
        assertEquals(menu, observer1.ultimoMenuEliminato);
    }

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