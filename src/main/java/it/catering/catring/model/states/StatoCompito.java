package it.catering.catring.model.states;

public enum StatoCompito {
    ASSEGNATO("Assegnato"),
    IN_CORSO("In Corso"),
    COMPLETATO("Completato"),
    PROBLEMA("Problema");
    
    private final String descrizione;
    
    StatoCompito(String descrizione) {
        this.descrizione = descrizione;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    @Override
    public String toString() {
        return descrizione;
    }
}