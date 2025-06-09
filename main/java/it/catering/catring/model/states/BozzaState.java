package it.catering.catring.model.states;

import it.catering.catring.model.entities.Preparazione;

public class BozzaState implements RicettaState {
    
    @Override
    public void pubblica(Preparazione ricetta) {
        ricetta.setState(new PubblicataState());
        ricetta.setPubblicata(true);
    }
    
    @Override
    public void ritiraDallaPubblicazione(Preparazione ricetta) {
        throw new IllegalStateException("Una ricetta in bozza non può essere ritirata dalla pubblicazione");
    }
    
    @Override
    public void modifica(Preparazione ricetta) {
        // Le ricette in bozza possono sempre essere modificate
    }
    
    @Override
    public boolean canModify() {
        return true;
    }
    
    @Override
    public boolean canDelete() {
        return true;
    }
    
    @Override
    public boolean isVisible() {
        return false; // Solo il proprietario può vedere le bozze
    }
    
    @Override
    public String getStateName() {
        return "Bozza";
    }
}
