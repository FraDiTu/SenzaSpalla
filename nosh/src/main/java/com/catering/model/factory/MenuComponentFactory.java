package com.catering.model.factory;

import com.catering.model.domain.Menu;
import com.catering.model.domain.VoceMenu;
import com.catering.model.domain.Ricetta;

/**
 * Abstract Factory pattern - crea componenti del menu
 */
public interface MenuComponentFactory {
    Menu createMenu(String titolo, String eventoId);
    VoceMenu createVoceMenu(Ricetta ricetta, String sezione);
    VoceMenu createVoceMenu(String ricettaId, String nome, String sezione);
    String generateId();
}