package com.catering.model.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Adapter pattern - adatta la Ricetta per l'uso nel menu
 * Information Expert - gestisce il proprio stato
 */
public class VoceMenu {
    private final String ricettaId;
    private final String nomeVisualizzato;
    private final String sezione;

    @JsonCreator
    public VoceMenu(@JsonProperty("ricettaId") String ricettaId,
                    @JsonProperty("nomeVisualizzato") String nomeVisualizzato,
                    @JsonProperty("sezione") String sezione) {
        this.ricettaId = Objects.requireNonNull(ricettaId);
        this.nomeVisualizzato = Objects.requireNonNull(nomeVisualizzato);
        this.sezione = Objects.requireNonNull(sezione);
    }

    public String getRicettaId() { return ricettaId; }
    public String getNomeVisualizzato() { return nomeVisualizzato; }
    public String getSezione() { return sezione; }

    // Creator pattern - crea nuove istanze modificate
    public VoceMenu withNome(String nuovoNome) {
        return new VoceMenu(this.ricettaId, nuovoNome, this.sezione);
    }

    public VoceMenu withSezione(String nuovaSezione) {
        return new VoceMenu(this.ricettaId, this.nomeVisualizzato, nuovaSezione);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoceMenu voceMenu)) return false;
        return Objects.equals(ricettaId, voceMenu.ricettaId) &&
               Objects.equals(sezione, voceMenu.sezione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ricettaId, sezione);
    }

    @Override
    public String toString() {
        return nomeVisualizzato;
    }
}