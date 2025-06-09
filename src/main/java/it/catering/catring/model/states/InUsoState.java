package it.catering.catring.model.states;

import it.catering.catring.model.entities.Preparazione;

public class InUsoState implements RicettaState {
    
    @Override
    public void pubblica(Preparazione ricetta) {
        throw new IllegalStateException("La ricetta è già in uso");
    }
    
    @Override
    public void ritiraDallaPubblicazione(Preparazione ricetta) {
        throw new IllegalStateException("Non è possibile ritirare una ricetta in uso");
    }
    
    @Override
    public void modifica(Preparazione ricetta) {
        throw new IllegalStateException("Non è possibile modificare una ricetta in uso");
    }
    
    @Override
    public boolean canModify() {
        return false;
    }
    
    @Override
    public boolean canDelete() {
        return false;
    }
    
    @Override
    public boolean isVisible() {
        return true;
    }
    
    @Override
    public String getStateName() {
        return "In Uso";
    }
}