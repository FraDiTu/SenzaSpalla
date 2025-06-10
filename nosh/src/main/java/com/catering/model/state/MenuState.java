package com.catering.model.state;

import com.catering.model.domain.VoceMenu;

/**
 * State pattern - stato del menu durante la modifica
 */
public interface MenuState {
    boolean canEdit();
    boolean canPublish();
    boolean canDelete();
    MenuState addSection(String sectionName);
    MenuState addItem(VoceMenu item);
    MenuState publish();
    MenuState unpublish();
    String getStateName();
}