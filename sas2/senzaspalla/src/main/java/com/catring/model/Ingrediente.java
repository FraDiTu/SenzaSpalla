package com.catring.model;

public class Ingrediente {
    private String id;
    private String nome;
    private String tipo;
    private String unitaMisura;
    
    public Ingrediente() {}
    
    public Ingrediente(String id, String nome, String tipo, String unitaMisura) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.unitaMisura = unitaMisura;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getUnitaMisura() { return unitaMisura; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }
}