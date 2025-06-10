package com.catering.model.visitor;

import com.catering.model.domain.Menu;
import com.catering.model.domain.VoceMenu;

/**
 * Visitor pattern - per operazioni su menu
 */
public interface MenuVisitor {
    void visitMenu(Menu menu);
    void visitSezione(String nomeSezione);
    void visitVoce(VoceMenu voce);
    Object getResult();
}