package com.catring.observer;

import com.catring.model.Menu;
import java.util.ArrayList;
import java.util.List;

/**
 * PATTERN GOF: OBSERVER
 * Classe che gestisce la notifica degli eventi relativi ai menu.
 * Implementa il pattern Observer come Subject/Observable.
 */
public class MenuEventNotifier {
    
    private List<MenuObserver> observers;
    
    public MenuEventNotifier() {
        this.observers = new ArrayList<>();
    }
    
    /**
     * Registra un observer
     */
    public void addObserver(MenuObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Rimuove un observer
     */
    public void removeObserver(MenuObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifica tutti gli observer della creazione di un menu
     */
    public void notifyMenuCreated(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuCreated(menu);
        }
    }
    
    /**
     * Notifica tutti gli observer della modifica di un menu
     */
    public void notifyMenuUpdated(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuUpdated(menu);
        }
    }
    
    /**
     * Notifica tutti gli observer dell'eliminazione di un menu
     */
    public void notifyMenuDeleted(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuDeleted(menu);
        }
    }
    
    /**
     * Restituisce il numero di observer registrati
     */
    public int getObserverCount() {
        return observers.size();
    }
}
