package com.catring.model;

public class Servizio {
    private String id;
    private String fasciaOraria;
    private String tipo;
    private String note;
    private Menu menu;
    
    public Servizio() {}
    
    public Servizio(String id, String fasciaOraria, String tipo, String note) {
        this.id = id;
        this.fasciaOraria = fasciaOraria;
        this.tipo = tipo;
        this.note = note;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFasciaOraria() { return fasciaOraria; }
    public void setFasciaOraria(String fasciaOraria) { this.fasciaOraria = fasciaOraria; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }
}