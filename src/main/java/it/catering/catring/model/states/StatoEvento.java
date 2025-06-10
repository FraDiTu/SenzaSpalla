package it.catering.catring.model.states;

public enum StatoEvento {
    BOZZA("Bozza"),
    IN_CORSO("In Corso"),
    MENU_PROPOSTI("Menu Proposti"),
    APPROVATO("Approvato"),
    COMPLETATO("Completato"),
    ANNULLATO("Annullato");
    
    private final String descrizione;
    
    StatoEvento(String descrizione) {
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