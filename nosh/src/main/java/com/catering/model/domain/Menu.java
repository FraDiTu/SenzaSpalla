package com.catering.model.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Composite pattern - gestisce un albero di sezioni e voci
 * Information Expert - conosce la propria struttura
 */
public class Menu {
    private final String id;
    private final String titolo;
    private final String eventoId;
    private final LocalDateTime dataCreazione;
    private final Map<String, List<VoceMenu>> sezioni;
    private final String informazioniAggiuntive;
    private final boolean pubblicato;

    @JsonCreator
    public Menu(@JsonProperty("id") String id,
                @JsonProperty("titolo") String titolo,
                @JsonProperty("eventoId") String eventoId,
                @JsonProperty("dataCreazione") LocalDateTime dataCreazione,
                @JsonProperty("sezioni") Map<String, List<VoceMenu>> sezioni,
                @JsonProperty("informazioniAggiuntive") String informazioniAggiuntive,
                @JsonProperty("pubblicato") boolean pubblicato) {
        this.id = Objects.requireNonNull(id);
        this.titolo = Objects.requireNonNull(titolo);
        this.eventoId = Objects.requireNonNull(eventoId);
        this.dataCreazione = dataCreazione != null ? dataCreazione : LocalDateTime.now();
        this.sezioni = new LinkedHashMap<>(sezioni != null ? sezioni : new LinkedHashMap<>());
        this.informazioniAggiuntive = informazioniAggiuntive;
        this.pubblicato = pubblicato;
    }

    // Information Expert - fornisce accesso ai propri dati
    public String getId() { return id; }
    public String getTitolo() { return titolo; }
    public String getEventoId() { return eventoId; }
    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public Map<String, List<VoceMenu>> getSezioni() { 
        return Collections.unmodifiableMap(sezioni); 
    }
    public String getInformazioniAggiuntive() { return informazioniAggiuntive; }
    public boolean isPubblicato() { return pubblicato; }

    // Information Expert - sa calcolare il numero totale di voci
    public int getTotalVoci() {
        return sezioni.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    // Information Expert - sa se contiene una ricetta
    public boolean contieneRicetta(String ricettaId) {
        return sezioni.values().stream()
                .flatMap(List::stream)
                .anyMatch(voce -> voce.getRicettaId().equals(ricettaId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu menu)) return false;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return titolo + " (" + getTotalVoci() + " voci)";
    }
}