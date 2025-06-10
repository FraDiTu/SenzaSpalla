package it.catering.catring.model.states;

import it.catering.catring.model.entities.Preparazione;

public interface RicettaState {
    void pubblica(Preparazione ricetta);
    void ritiraDallaPubblicazione(Preparazione ricetta);
    void modifica(Preparazione ricetta);
    boolean canModify();
    boolean canDelete();
    boolean isVisible();
    String getStateName();
}