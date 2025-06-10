// File: src/main/java/it/catering/catring/model/entities/Ingrediente.java
package it.catering.catring.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Ingrediente {
    private String nome;
    private String unitaMisura;
    
    // Costruttore di default per Jackson
    public Ingrediente() {}
    
    @JsonCreator
    public Ingrediente(@JsonProperty("nome") String nome, @JsonProperty("unitaMisura") String unitaMisura) {
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