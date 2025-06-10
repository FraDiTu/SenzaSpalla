package com.catering.model.state;

import com.catering.model.domain.VoceMenu;

/**
 * State pattern - stato bozza del menu
 */
public class DraftMenuState implements MenuState {
    
    @Override
    public boolean canEdit() {
        return true;
    }
    
    @Override
    public boolean canPublish() {
        return true;
    }
    
    @Override
    public boolean canDelete() {
        return true;
    }
    
    @Override
    public MenuState addSection(String sectionName) {
        return this; // rimane in draft
    }
    
    @Override
    public MenuState addItem(VoceMenu item) {
        return this; // rimane in draft
    }
    
    @Override
    public MenuState publish() {
        return new PublishedMenuState();
    }
    
    @Override
    public MenuState unpublish() {
        return this; // già in draft
    }
    
    @Override
    public String getStateName() {
        return "BOZZA";
    }
}