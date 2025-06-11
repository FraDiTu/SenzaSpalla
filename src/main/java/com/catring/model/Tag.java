package com.catring.model;

public class Tag {
    private String nome;
    
    public Tag() {}
    
    public Tag(String nome) {
        this.nome = nome;
    }
    
    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}