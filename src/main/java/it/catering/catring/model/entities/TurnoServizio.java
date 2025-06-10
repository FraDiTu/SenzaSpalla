package it.catering.catring.model.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TurnoServizio extends Turno {
    private Evento evento;
    private Servizio servizio;
    private List<PersonaleServizio> personaleAssegnato;
    private int tempoPreparazione; // minuti prima del servizio
    private int tempoRigoverno; // minuti dopo il servizio
    
    public TurnoServizio() {
        super();
        this.personaleAssegnato = new ArrayList<>();
    }
    
    public TurnoServizio(LocalDate data, LocalTime orarioInizio, LocalTime orarioFine, 
                        String luogo, Evento evento, Servizio servizio) {
        super(data, orarioInizio, orarioFine, luogo);
        this.evento = evento;
        this.servizio = servizio;
        this.personaleAssegnato = new ArrayList<>();
    }
    
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public Servizio getServizio() { return servizio; }
    public void setServizio(Servizio servizio) { this.servizio = servizio; }
    public List<PersonaleServizio> getPersonaleAssegnato() { return personaleAssegnato; }
    public void setPersonaleAssegnato(List<PersonaleServizio> personaleAssegnato) { this.personaleAssegnato = personaleAssegnato; }
    public int getTempoPreparazione() { return tempoPreparazione; }
    public void setTempoPreparazione(int tempoPreparazione) { this.tempoPreparazione = tempoPreparazione; }
    public int getTempoRigoverno() { return tempoRigoverno; }
    public void setTempoRigoverno(int tempoRigoverno) { this.tempoRigoverno = tempoRigoverno; }
    
    public void aggiungiPersonale(PersonaleServizio personale) {
        if (!personaleAssegnato.contains(personale)) {
            personaleAssegnato.add(personale);
        }
    }
    
    public void rimuoviPersonale(PersonaleServizio personale) {
        personaleAssegnato.remove(personale);
    }
    
    @Override
    public String toString() {
        return "Turno Servizio " + data + " " + orarioInizio + "-" + orarioFine + 
               (evento != null ? " (" + evento.getNome() + ")" : "");
    }
}