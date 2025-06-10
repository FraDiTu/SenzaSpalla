package com.catering.model.decorator;

/**
 * Decorator pattern - decoratore base
 */
public abstract class MenuDecorator implements MenuComponent {
    protected final MenuComponent component;
    
    public MenuDecorator(MenuComponent component) {
        this.component = component;
    }
    
    @Override
    public String getDisplayText() {
        return component.getDisplayText();
    }
    
    @Override
    public com.catering.model.domain.Menu getMenu() {
        return component.getMenu();
    }
}