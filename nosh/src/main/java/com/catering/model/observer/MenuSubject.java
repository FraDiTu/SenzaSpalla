package com.catering.model.observer;

import java.util.List;

/**
 * Observer pattern - soggetto osservabile
 */
public interface MenuSubject {
    void addObserver(MenuObserver observer);
    void removeObserver(MenuObserver observer);
    void notifyObservers();
    List<MenuObserver> getObservers();
}