package it.catering.catring.model.states;

public enum StatoTurno {
    APERTO("Aperto"),
    IN_CORSO("In Corso"),
    COMPLETATO("Completato"),
    ANNULLATO("Annullato");
    
    private final String descrizione;
    
    StatoTurno(String descrizione) {
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