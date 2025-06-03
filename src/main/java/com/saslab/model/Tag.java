package com.saslab.model;

import java.util.Objects;

/**
 * Classe che rappresenta un Tag per classificare ricette e preparazioni
 * Implementa pattern Value Object (GoF)
 */
public class Tag {
    
    private String nome;
    private String categoria; // es. "Dieta", "Tipo Cottura", "Origine"
    private String colore; // per visualizzazione UI
    
    public Tag(String nome) {
        this.nome = Objects.requireNonNull(nome, "Il nome del tag non può essere null");
        
        if (nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del tag non può essere vuoto");
        }
        
        this.nome = nome.trim().toLowerCase(); // Normalizza per confronti
    }
    
    public Tag(String nome, String categoria) {
        this(nome);
        this.categoria = categoria;
    }
    
    public Tag(String nome, String categoria, String colore) {
        this(nome, categoria);
        this.colore = colore;
    }
    
    // Getters
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public String getColore() { return colore; }
    
    // Setters
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public void setColore(String colore) {
        this.colore = colore;
    }
    
    // Business methods
    public String getDisplayName() {
        return nome.substring(0, 1).toUpperCase() + nome.substring(1);
    }
    
    public boolean matches(String searchTerm) {
        if (searchTerm == null) return false;
        return nome.contains(searchTerm.toLowerCase()) ||
               (categoria != null && categoria.toLowerCase().contains(searchTerm.toLowerCase()));
    }
    
    // Metodi statici per tag comuni
    public static Tag vegetariano() {
        return new Tag("vegetariano", "Dieta", "#4CAF50");
    }
    
    public static Tag vegano() {
        return new Tag("vegano", "Dieta", "#8BC34A");
    }
    
    public static Tag senzaGlutine() {
        return new Tag("senza glutine", "Dieta", "#FF9800");
    }
    
    public static Tag fingerFood() {
        return new Tag("finger food", "Servizio", "#2196F3");
    }
    
    public static Tag dessert() {
        return new Tag("dessert", "Portata", "#E91E63");
    }
    
    public static Tag antipasto() {
        return new Tag("antipasto", "Portata", "#9C27B0");
    }
    
    public static Tag primo() {
        return new Tag("primo", "Portata", "#3F51B5");
    }
    
    public static Tag secondo() {
        return new Tag("secondo", "Portata", "#F44336");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return Objects.equals(nome, tag.nome);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
    
    @Override
    public String toString() {
        return nome;
    }
}