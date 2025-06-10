package it.catering.catring.model.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TurnoPreparatorio extends Turno {
    private List<Cuoco> cuochiAssegnati;
    private int tempoTotaleStimato; // minuti
    private String note;
    
    public TurnoPreparatorio() {
        super();
        this.cuochiAssegnati = new ArrayList<>();
        this.luogo = "Cucina"; // Default
    }
    
    public TurnoPreparatorio(LocalDate data, LocalTime orarioInizio, LocalTime orarioFine) {
        super(data, orarioInizio, orarioFine, "Cucina");
        this.cuochiAssegnati = new ArrayList<>();
    }
    
    public List<Cuoco> getCuochiAssegnati() { return cuochiAssegnati; }
    public void setCuochiAssegnati(List<Cuoco> cuochiAssegnati) { this.cuochiAssegnati = cuochiAssegnati; }
    public int getTempoTotaleStimato() { return tempoTotaleStimato; }
    public void setTempoTotaleStimato(int tempoTotaleStimato) { this.tempoTotaleStimato = tempoTotaleStimato; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public void aggiungiCuoco(Cuoco cuoco) {
        if (!cuochiAssegnati.contains(cuoco)) {
            cuochiAssegnati.add(cuoco);
        }
    }
    
    public void rimuoviCuoco(Cuoco cuoco) {
        cuochiAssegnati.remove(cuoco);
    }
    
    public boolean haSpazioPerCompito(int minutiRichiesti) {
        return tempoTotaleStimato + minutiRichiesti <= getDurataMinuti();
    }
    
    @Override
    public String toString() {
        return "Turno Preparatorio " + data + " " + orarioInizio + "-" + orarioFine;
    }
}