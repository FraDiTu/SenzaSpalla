package com.catering.model.observer;

import com.catering.model.domain.Menu;

/**
 * Observer pattern - interfaccia per osservatori del menu
 */
public interface MenuObserver {
    void onMenuChanged(Menu menu);
    void onMenuPublished(Menu menu);
    void onMenuSectionAdded(String sectionName);
    void onMenuItemAdded(String section, String itemName);
}