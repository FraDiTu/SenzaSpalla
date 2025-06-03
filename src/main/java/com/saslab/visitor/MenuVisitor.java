package com.saslab.visitor;

import com.saslab.model.Menu;
import com.saslab.model.MenuSection;
import com.saslab.model.MenuItem;

/**
 * Visitor pattern (GoF) per operazioni sui menù
 * Permette di aggiungere nuove operazioni senza modificare le classi del modello
 */
public interface MenuVisitor {
    void visitMenu(Menu menu);
    void visitMenuSection(MenuSection section);
    void visitMenuItem(MenuItem item);
}