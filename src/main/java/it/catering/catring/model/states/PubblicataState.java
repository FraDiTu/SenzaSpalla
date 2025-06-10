package it.catering.catring.model.states;

import it.catering.catring.model.entities.Preparazione;

public class PubblicataState implements RicettaState {
    
    @Override
    public void pubblica(Preparazione ricetta) {
        throw new IllegalStateException("La ricetta è già pubblicata");
    }
    
    @Override
    public void ritiraDallaPubblicazione(Preparazione ricetta) {
        ricetta.setState(new BozzaState());
        ricetta.setPubblicata(false);
    }
    
    @Override
    public void modifica(Preparazione ricetta) {
        throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata. Ritirarla prima dalla pubblicazione.");
    }
    
    @Override
    public boolean canModify() {
        return false;
    }
    
    @Override
    public boolean canDelete() {
        return false; // Le ricette pubblicate non possono essere eliminate
    }
    
    @Override
    public boolean isVisible() {
        return true; // Visibile a tutti
    }
    
    @Override
    public String getStateName() {
        return "Pubblicata";
    }
}
