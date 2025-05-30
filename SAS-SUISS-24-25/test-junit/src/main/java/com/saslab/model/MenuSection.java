package com.saslab.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta una sezione di un menù
 * Implementa pattern Composite come componente
 */
public class MenuSection {
    
    private String id;
    private String title;
    private int order;
    private List<MenuItem> menuItems;
    
    public MenuSection(String id, String title, int order) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.title = Objects.requireNonNull(title, "Il titolo non può essere null");
        this.order = order;
        this.menuItems = new ArrayList<>();
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getOrder() { return order; }
    public List<MenuItem> getMenuItems() { return new ArrayList<>(menuItems); }
    
    // Setters
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Il titolo non può essere null");
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    // Menu item management
    public void addMenuItem(MenuItem menuItem) {
        Objects.requireNonNull(menuItem, "La voce di menù non può essere null");
        menuItems.add(menuItem);
    }
    
    public boolean removeMenuItem(MenuItem menuItem) {
        return menuItems.remove(menuItem);
    }
    
    public MenuItem removeMenuItemByRecipeId(String recipeId) {
        MenuItem item = findMenuItemByRecipeId(recipeId);
        if (item != null) {
            menuItems.remove(item);
        }
        return item;
    }
    
    public MenuItem findMenuItemByRecipeId(String recipeId) {
        return menuItems.stream()
                .filter(item -> item.getRecipeId().equals(recipeId))
                .findFirst()
                .orElse(null);
    }
    
    public boolean hasMenuItems() {
        return !menuItems.isEmpty();
    }
    
    public int getMenuItemCount() {
        return menuItems.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuSection section = (MenuSection) obj;
        return Objects.equals(id, section.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("MenuSection{id='%s', title='%s', order=%d, items=%d}", 
                           id, title, order, menuItems.size());
    }
}