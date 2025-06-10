package it.catering.catring.model.states;

public enum StatoMenu {
    BOZZA("Bozza"),
    PROPOSTO("Proposto"),
    APPROVATO("Approvato"),
    RIFIUTATO("Rifiutato");
    
    private final String descrizione;
    
    StatoMenu(String descrizione) {
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