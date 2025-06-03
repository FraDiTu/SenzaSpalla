package com.saslab.decorator;

import com.saslab.model.Menu;
import com.saslab.model.MenuSection;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Decorator pattern (GoF) per aggiungere funzionalità ai menù
 * Permette di estendere dinamicamente le funzionalità dei menù
 */
public abstract class MenuDecorator {
    
    protected Menu decoratedMenu;
    
    public MenuDecorator(Menu menu) {
        this.decoratedMenu = menu;
    }
    
    // Delegazione metodi base
    public String getId() { return decoratedMenu.getId(); }
    public String getName() { return decoratedMenu.getName(); }
    public String getDescription() { return decoratedMenu.getDescription(); }
    public String getChefId() { return decoratedMenu.getChefId(); }
    public Menu.MenuState getState() { return decoratedMenu.getState(); }
    public List<MenuSection> getSections() { return decoratedMenu.getSections(); }
    public LocalDateTime getCreatedAt() { return decoratedMenu.getCreatedAt(); }
    public LocalDateTime getLastModified() { return decoratedMenu.getLastModified(); }
    
    // Metodo astratto per funzionalità aggiuntive
    public abstract String getEnhancedDescription();
}