package com.catering.model.decorator;

/**
 * Decorator pattern - aggiunge conteggio voci al menu
 */
public class CountMenuDecorator extends MenuDecorator {
    
    public CountMenuDecorator(MenuComponent component) {
        super(component);
    }
    
    @Override
    public String getDisplayText() {
        int count = getMenu().getTotalVoci();
        return super.getDisplayText() + " (" + count + " voci)";
    }
}
