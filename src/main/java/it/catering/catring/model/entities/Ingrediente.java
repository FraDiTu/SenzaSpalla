package it.catering.catring.model.entities;

import java.util.Objects;

public class Ingrediente {
    private String nome;
    private String unitaMisura;
    
    public Ingrediente(String nome, String unitaMisura) {
        this.nome = nome;
        this.unitaMisura = unitaMisura;
    }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUnitaMisura() { return unitaMisura; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingrediente that = (Ingrediente) o;
        return Objects.equals(nome, that.nome);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
    
    @Override
    public String toString() {
        return nome + " (" + unitaMisura + ")";
    }
}