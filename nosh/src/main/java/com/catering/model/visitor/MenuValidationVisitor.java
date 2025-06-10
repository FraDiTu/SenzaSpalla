package com.catering.model.visitor;

import com.catering.model.domain.Menu;
import com.catering.model.domain.VoceMenu;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor pattern - validazione del menu
 */
public class MenuValidationVisitor implements MenuVisitor {
    private final List<String> errors = new ArrayList<>();
    
    @Override
    public void visitMenu(Menu menu) {
        if (menu.getTitolo() == null || menu.getTitolo().trim().isEmpty()) {
            errors.add("Il menu deve avere un titolo");
        }
        if (menu.getSezioni().isEmpty()) {
            errors.add("Il menu deve avere almeno una sezione");
        }
    }
    
    @Override
    public void visitSezione(String nomeSezione) {
        if (nomeSezione == null || nomeSezione.trim().isEmpty()) {
            errors.add("Le sezioni devono avere un nome");
        }
    }
    
    @Override
    public void visitVoce(VoceMenu voce) {
        if (voce.getNomeVisualizzato() == null || voce.getNomeVisualizzato().trim().isEmpty()) {
            errors.add("Le voci menu devono avere un nome");
        }
        if (voce.getRicettaId() == null || voce.getRicettaId().trim().isEmpty()) {
            errors.add("Le voci menu devono riferirsi a una ricetta");
        }
    }
    
    @Override
    public List<String> getResult() {
        return new ArrayList<>(errors);
    }
    
    public boolean isValid() {
        return errors.isEmpty();
    }
}