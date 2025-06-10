package com.catering.model.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

/**
 * Information Expert pattern - conosce i propri dati
 * Immutable object per garantire thread safety
 */
public class Ricetta {
    private final String id;
    private final String nome;
    private final String descrizione;
    private final List<String> ingredienti;
    private final String tempoPreparazione;
    private final String autore;

    @JsonCreator
    public Ricetta(@JsonProperty("id") String id,
                   @JsonProperty("nome") String nome,
                   @JsonProperty("descrizione") String descrizione,
                   @JsonProperty("ingredienti") List<String> ingredienti,
                   @JsonProperty("tempoPreparazione") String tempoPreparazione,
                   @JsonProperty("autore") String autore) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.descrizione = descrizione;
        this.ingredienti = List.copyOf(ingredienti != null ? ingredienti : List.of());
        this.tempoPreparazione = tempoPreparazione;
        this.autore = autore;
    }

    // Information Expert - sa calcolare il proprio hash
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public List<String> getIngredienti() { return List.copyOf(ingredienti); }
    public String getTempoPreparazione() { return tempoPreparazione; }
    public String getAutore() { return autore; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ricetta ricetta)) return false;
        return Objects.equals(id, ricetta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome;
    }
}