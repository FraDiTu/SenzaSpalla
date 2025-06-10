package com.catering.model.factory;

import com.catering.model.domain.Menu;
import com.catering.model.domain.VoceMenu;
import com.catering.model.domain.Ricetta;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Concrete Factory - implementazione standard
 * Creator pattern - sa come creare Menu e VoceMenu
 */
public class StandardMenuComponentFactory implements MenuComponentFactory {
    
    @Override
    public Menu createMenu(String titolo, String eventoId) {
        return new Menu(
            generateId(),
            titolo,
            eventoId,
            LocalDateTime.now(),
            new LinkedHashMap<>(),
            null,
            false
        );
    }
    
    @Override
    public VoceMenu createVoceMenu(Ricetta ricetta, String sezione) {
        return new VoceMenu(ricetta.getId(), ricetta.getNome(), sezione);
    }
    
    @Override
    public VoceMenu createVoceMenu(String ricettaId, String nome, String sezione) {
        return new VoceMenu(ricettaId, nome, sezione);
    }
    
    @Override
    public String generateId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
