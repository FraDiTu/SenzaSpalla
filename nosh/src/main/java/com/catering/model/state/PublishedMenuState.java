package com.catering.model.state;

import com.catering.model.domain.VoceMenu;

/**
 * State pattern - stato pubblicato del menu
 */
public class PublishedMenuState implements MenuState {
    
    @Override
    public boolean canEdit() {
        return false;
    }
    
    @Override
    public boolean canPublish() {
        return false;
    }
    
    @Override
    public boolean canDelete() {
        return false;
    }
    
    @Override
    public MenuState addSection(String sectionName) {
        throw new IllegalStateException("Impossibile modificare un menu pubblicato");
    }
    
    @Override
    public MenuState addItem(VoceMenu item) {
        throw new IllegalStateException("Impossibile modificare un menu pubblicato");
    }
    
    @Override
    public MenuState publish() {
        return this; // già pubblicato
    }
    
    @Override
    public MenuState unpublish() {
        return new DraftMenuState();
    }
    
    @Override
    public String getStateName() {
        return "PUBBLICATO";
    }
}