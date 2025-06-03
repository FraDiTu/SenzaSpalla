package com.saslab.model;

import java.util.Objects;

/**
 * Classe che rappresenta un'istruzione di preparazione strutturata
 * Contiene ordine, tempo stimato e note aggiuntive
 */
public class Instruction {
    
    private String text;
    private int ordine;
    private int tempoEstimato; // in minuti
    private String note;
    
    public Instruction(String text, int ordine, int tempoEstimato, String note) {
        this.text = Objects.requireNonNull(text, "Il testo dell'istruzione non può essere null");
        this.ordine = ordine;
        this.tempoEstimato = tempoEstimato;
        this.note = note;
        
        if (ordine < 1) {
            throw new IllegalArgumentException("L'ordine deve essere positivo");
        }
        if (tempoEstimato < 0) {
            throw new IllegalArgumentException("Il tempo stimato non può essere negativo");
        }
    }
    
    // Getters
    public String getText() { return text; }
    public int getOrdine() { return ordine; }
    public int getTempoEstimato() { return tempoEstimato; }
    public String getNote() { return note; }
    
    // Setters
    public void setText(String text) {
        this.text = Objects.requireNonNull(text, "Il testo dell'istruzione non può essere null");
    }
    
    public void setOrdine(int ordine) {
        if (ordine < 1) {
            throw new IllegalArgumentException("L'ordine deve essere positivo");
        }
        this.ordine = ordine;
    }
    
    public void setTempoEstimato(int tempoEstimato) {
        if (tempoEstimato < 0) {
            throw new IllegalArgumentException("Il tempo stimato non può essere negativo");
        }
        this.tempoEstimato = tempoEstimato;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public boolean hasNote() {
        return note != null && !note.trim().isEmpty();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Instruction that = (Instruction) obj;
        return ordine == that.ordine &&
               tempoEstimato == that.tempoEstimato &&
               Objects.equals(text, that.text) &&
               Objects.equals(note, that.note);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(text, ordine, tempoEstimato, note);
    }
    
    @Override
    public String toString() {
        return String.format("Instruction{ordine=%d, text='%s', tempo=%d min}", 
                           ordine, text, tempoEstimato);
    }
}