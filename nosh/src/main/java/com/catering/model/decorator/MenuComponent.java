package com.catering.model.decorator;

import com.catering.model.domain.Menu;

/**
 * Decorator pattern - componente base per decoratori del menu
 */
public interface MenuComponent {
    String getDisplayText();
    Menu getMenu();
}