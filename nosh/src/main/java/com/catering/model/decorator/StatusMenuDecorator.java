package com.catering.model.decorator;

/**
 * Decorator pattern - aggiunge info di stato al menu
 */
public class StatusMenuDecorator extends MenuDecorator {
    
    public StatusMenuDecorator(MenuComponent component) {
        super(component);
    }
    
    @Override
    public String getDisplayText() {
        String status = getMenu().isPubblicato() ? "[PUBBLICATO]" : "[BOZZA]";
        return status + " " + super.getDisplayText();
    }
}