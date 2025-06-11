package com.catring.observer;

import com.catring.model.Menu;

/**
 * PATTERN GOF: OBSERVER
 * Interfaccia per gli observer che vogliono essere notificati
 * delle modifiche ai menu.
 */
public interface MenuObserver {
    
    /**
     * Chiamato quando un menu viene creato
     */
    void onMenuCreated(Menu menu);
    
    /**
     * Chiamato quando un menu viene modificato
     */
    void onMenuUpdated(Menu menu);
    
    /**
     * Chiamato quando un menu viene eliminato
     */
    void onMenuDeleted(Menu menu);
}
