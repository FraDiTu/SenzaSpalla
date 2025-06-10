package it.catering.catring.model.entities;

import it.catering.catring.model.states.StatoTurno;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public abstract class Turno {
    protected Long id;
    protected LocalDate data;
    protected LocalTime orarioInizio;
    protected LocalTime orarioFine;
    protected String luogo;
    protected StatoTurno stato;
    protected boolean modificabile;
    
    public Turno() {
        this.id = System.currentTimeMillis() + (long) (Math.random() * 1000);
        this.stato = StatoTurno.APERTO;
        this.modificabile = true;
    }
    
    public Turno(LocalDate data, LocalTime orarioInizio, LocalTime orarioFine, String luogo) {
        this();
        this.data = data;
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
        this.luogo = luogo;
    }
    
    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getOrarioInizio() { return orarioInizio; }
    public void setOrarioInizio(LocalTime orarioInizio) { this.orarioInizio = orarioInizio; }
    public LocalTime getOrarioFine() { return orarioFine; }
    public void setOrarioFine(LocalTime orarioFine) { this.orarioFine = orarioFine; }
    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }
    public StatoTurno getStato() { return stato; }
    public void setStato(StatoTurno stato) { this.stato = stato; }
    public boolean isModificabile() { return modificabile; }
    public void setModificabile(boolean modificabile) { this.modificabile = modificabile; }
    
    public int getDurataMinuti() {
        if (orarioInizio != null && orarioFine != null) {
            return (int) java.time.Duration.between(orarioInizio, orarioFine).toMinutes();
        }
        return 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turno turno = (Turno) o;
        return Objects.equals(id, turno.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}