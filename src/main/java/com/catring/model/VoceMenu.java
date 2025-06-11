package com.catring.model;

public class VoceMenu {
    private String id;
    private String nomeVisuale;
    private String riferimento;
    private String modificheTesto;
    private Ricetta ricetta;
    
    public VoceMenu() {}
    
    public VoceMenu(String id, String nomeVisuale, String riferimento, String modificheTesto) {
        this.id = id;
        this.nomeVisuale = nomeVisuale;
        this.riferimento = riferimento;
        this.modificheTesto = modificheTesto;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNomeVisuale() { return nomeVisuale; }
    public void setNomeVisuale(String nomeVisuale) { this.nomeVisuale = nomeVisuale; }
    
    public String getRiferimento() { return riferimento; }
    public void setRiferimento(String riferimento) { this.riferimento = riferimento; }
    
    public String getModificheTesto() { return modificheTesto; }
    public void setModificheTesto(String modificheTesto) { this.modificheTesto = modificheTesto; }
    
    public Ricetta getRicetta() { return ricetta; }
    public void setRicetta(Ricetta ricetta) { this.ricetta = ricetta; }
}