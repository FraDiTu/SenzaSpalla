package it.catering.catring.model.entities;

import it.catering.catring.model.states.StatoCompito;

import java.util.Objects;

public class Compito {
    private Long id;
    private Preparazione preparazione;
    private Cuoco cuoco;
    private int tempoStimato; // in minuti
    private double quantita;
    private StatoCompito stato;
    private String note;
    
    public Compito(Preparazione preparazione, Cuoco cuoco, int tempoStimato, double quantita) {
        this.id = System.currentTimeMillis() + (long) (Math.random() * 1000);
        this.preparazione = preparazione;
        this.cuoco = cuoco;
        this.tempoStimato = tempoStimato;
        this.quantita = quantita;
        this.stato = StatoCompito.ASSEGNATO;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Preparazione getPreparazione() { return preparazione; }
    public void setPreparazione(Preparazione preparazione) { this.preparazione = preparazione; }
    public Cuoco getCuoco() { return cuoco; }
    public void setCuoco(Cuoco cuoco) { this.cuoco = cuoco; }
    public int getTempoStimato() { return tempoStimato; }
    public void setTempoStimato(int tempoStimato) { this.tempoStimato = tempoStimato; }
    public double getQuantita() { return quantita; }
    public void setQuantita(double quantita) { this.quantita = quantita; }
    public StatoCompito getStato() { return stato; }
    public void setStato(StatoCompito stato) { this.stato = stato; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public void inizia() {
        if (stato == StatoCompito.ASSEGNATO) {
            stato = StatoCompito.IN_CORSO;
        }
    }
    
    public void completa() {
        if (stato == StatoCompito.IN_CORSO) {
            stato = StatoCompito.COMPLETATO;
        }
    }
    
    public void segnalaProblema(String note) {
        this.note = note;
        stato = StatoCompito.PROBLEMA;
    }
    
    @Override
    public String toString() {
        return preparazione.getNome() + " - " + cuoco.getNomeCompleto() + " (" + stato + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compito compito = (Compito) o;
        return Objects.equals(id, compito.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}