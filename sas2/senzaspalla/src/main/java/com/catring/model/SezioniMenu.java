package com.catring.model;

import java.util.ArrayList;
import java.util.List;

public class SezioniMenu {
    private String id;
    private String titolo;
    private int ordine;
    private List<VoceMenu> voci;
    
    public SezioniMenu() {
        this.voci = new ArrayList<>();
    }
    
    public SezioniMenu(String id, String titolo, int ordine) {
        this();
        this.id = id;
        this.titolo = titolo;
        this.ordine = ordine;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    
    public int getOrdine() { return ordine; }
    public void setOrdine(int ordine) { this.ordine = ordine; }
    
    public List<VoceMenu> getVoci() { return voci; }
    public void setVoci(List<VoceMenu> voci) { this.voci = voci; }
}