package com.catering.model.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Information Expert pattern - gestisce le proprie informazioni
 */
public class Evento {
    private final String id;
    private final String nome;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final String luogo;
    private final String tipo;
    private final String clienteId;

    @JsonCreator
    public Evento(@JsonProperty("id") String id,
                  @JsonProperty("nome") String nome,
                  @JsonProperty("dataInizio") LocalDate dataInizio,
                  @JsonProperty("dataFine") LocalDate dataFine,
                  @JsonProperty("luogo") String luogo,
                  @JsonProperty("tipo") String tipo,
                  @JsonProperty("clienteId") String clienteId) {

        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.dataInizio = Objects.requireNonNull(dataInizio);
        this.dataFine = dataFine != null ? dataFine : dataInizio;
        this.luogo = luogo;
        this.tipo = tipo;
        this.clienteId = clienteId;

    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public String getLuogo() { return luogo; }
    public String getTipo() { return tipo; }
    public String getClienteId() { return clienteId; }


    // Information Expert - sa calcolare la durata
    public long getDurataGiorni() {
        return dataInizio.until(dataFine).getDays() + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento evento)) return false;
        return Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome + " - " + dataInizio;
    }
}