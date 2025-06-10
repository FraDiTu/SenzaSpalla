package com.catring.model;

public class Cliente {
    private String id;
    private String nome;
    private String tipo;
    private String contatti;
    
    public Cliente() {}
    
    public Cliente(String id, String nome, String tipo, String contatti) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.contatti = contatti;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getContatti() { return contatti; }
    public void setContatti(String contatti) { this.contatti = contatti; }
}