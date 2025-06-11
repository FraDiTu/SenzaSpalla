package com.catring.observer;

import com.catring.model.Menu;
import java.util.ArrayList;
import java.util.List;

public class MenuEventNotifier {
    
    private List<MenuObserver> observers;
    
    public MenuEventNotifier() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(MenuObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(MenuObserver observer) {
        observers.remove(observer);
    }

    public void notifyMenuCreated(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuCreated(menu);
        }
    }

    public void notifyMenuUpdated(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuUpdated(menu);
        }
    }

    public void notifyMenuDeleted(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuDeleted(menu);
        }
    }

    public int getObserverCount() {
        return observers.size();
    }
}