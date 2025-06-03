package com.saslab.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe che rappresenta la Disponibilità di un membro del personale per un turno
 * Implementa pattern Information Expert per gestire lo stato della disponibilità
 */
public class Disponibilita {
    
    public enum StatoDisponibilita {
        CONFERMATA("Confermata"),
        NON_CONFERMATA("Non Confermata"),
        RITIRATA("Ritirata");
        
        private final String displayName;
        
        StatoDisponibilita(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String staffId;
    private String turnoId;
    private StatoDisponibilita stato;
    private LocalDate dataTurno;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;
    private String note;
    private boolean bloccata; // true quando il personale è stato assegnato
    
    public Disponibilita(String id, String staffId, String turnoId, LocalDate dataTurno) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.staffId = Objects.requireNonNull(staffId, "L'ID del personale non può essere null");
        this.turnoId = Objects.requireNonNull(turnoId, "L'ID del turno non può essere null");
        this.dataTurno = Objects.requireNonNull(dataTurno, "La data del turno non può essere null");
        
        this.stato = StatoDisponibilita.NON_CONFERMATA;
        this.dataCreazione = LocalDateTime.now();
        this.dataModifica = LocalDateTime.now();
        this.bloccata = false;
    }
    
    // Getters
    public String getId() { return id; }
    public String getStaffId() { return staffId; }
    public String getTurnoId() { return turnoId; }
    public StatoDisponibilita getStato() { return stato; }
    public LocalDate getDataTurno() { return dataTurno; }
    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public LocalDateTime getDataModifica() { return dataModifica; }
    public String getNote() { return note; }
    public boolean isBloccata() { return bloccata; }
    
    // Setters con validazione
    public void setNote(String note) {
        this.note = note;
        updateDataModifica();
    }
    
    // Business methods
    public void conferma() {
        if (bloccata) {
            throw new IllegalStateException("Non è possibile modificare una disponibilità bloccata");
        }
        
        if (dataTurno.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Non è possibile confermare disponibilità per turni passati");
        }
        
        this.stato = StatoDisponibilita.CONFERMATA;
        updateDataModifica();
    }
    
    public void ritira() {
        if (bloccata) {
            throw new IllegalStateException("Non è possibile ritirare una disponibilità bloccata (personale già assegnato)");
        }
        
        if (dataTurno.isBefore(LocalDate.now().plusDays(1))) {
            throw new IllegalStateException("Non è possibile ritirare disponibilità con meno di 24 ore di preavviso");
        }
        
        this.stato = StatoDisponibilita.RITIRATA;
        updateDataModifica();
    }
    
    public void blocca() {
        if (stato != StatoDisponibilita.CONFERMATA) {
            throw new IllegalStateException("Può essere bloccata solo una disponibilità confermata");
        }
        
        this.bloccata = true;
        updateDataModifica();
    }
    
    public void sblocca() {
        this.bloccata = false;
        updateDataModifica();
    }
    
    public boolean puoEssereModificata() {
        return !bloccata && !dataTurno.isBefore(LocalDate.now());
    }
    
    public boolean isValida() {
        return stato == StatoDisponibilita.CONFERMATA && !dataTurno.isBefore(LocalDate.now());
    }
    
    public boolean isScaduta() {
        return dataTurno.isBefore(LocalDate.now());
    }
    
    public int getGiorniAlTurno() {
        return (int) (dataTurno.toEpochDay() - LocalDate.now().toEpochDay());
    }
    
    public String getStatoDettagliato() {
        StringBuilder dettaglio = new StringBuilder(stato.getDisplayName());
        
        if (bloccata) {
            dettaglio.append(" (Bloccata)");
        }
        
        if (isScaduta()) {
            dettaglio.append(" (Scaduta)");
        }
        
        return dettaglio.toString();
    }
    
    private void updateDataModifica() {
        this.dataModifica = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Disponibilita that = (Disponibilita) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Disponibilita{id='%s', staff='%s', turno='%s', stato=%s, data=%s}", 
                           id, staffId, turnoId, stato, dataTurno);
    }
}