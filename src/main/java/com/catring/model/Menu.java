package com.catring.model;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private String id;
    private String nome;
    private String descrizione;
    private String note;
    private List<SezioniMenu> sezioni;
    
    public Menu() {
        this.sezioni = new ArrayList<>();
    }
    
    public Menu(String id, String nome, String descrizione, String note) {
        this();
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.note = note;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public List<SezioniMenu> getSezioni() { return sezioni; }
    public void setSezioni(List<SezioniMenu> sezioni) { this.sezioni = sezioni; }
}