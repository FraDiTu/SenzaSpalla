package it.catering.catring.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Dose {
    private Ingrediente ingrediente;
    private double quantita;

    // 1) no-arg ctor for Jackson
    public Dose() { }

    // 2) keep your existing constructor, but annotate it so Jackson knows how to bind
    @JsonCreator
    public Dose(
        @JsonProperty("ingrediente") Ingrediente ingrediente,
        @JsonProperty("quantita") double quantita
    ) {
        this.ingrediente = ingrediente;
        this.quantita   = quantita;
    }

    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    public double getQuantita() { return quantita; }
    public void setQuantita(double quantita) { this.quantita = quantita; }
}
