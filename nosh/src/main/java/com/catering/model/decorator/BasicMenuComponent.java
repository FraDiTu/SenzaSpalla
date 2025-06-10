package com.catering.model.decorator;

import com.catering.model.domain.Menu;

/**
 * Decorator pattern - componente menu base
 */
public class BasicMenuComponent implements MenuComponent {
    private final Menu menu;
    
    public BasicMenuComponent(Menu menu) {
        this.menu = menu;
    }
    
    @Override
    public String getDisplayText() {
        return menu.getTitolo();
    }
    
    @Override
    public Menu getMenu() {
        return menu;
    }
}