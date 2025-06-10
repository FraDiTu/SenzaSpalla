package it.catering.catring.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.catering.catring.model.states.*;
import java.util.*;

public class Preparazione {
    private String nome;
    private String descrizione;
    private User proprietario;
    private String autore;
    private List<Dose> ingredienti;
    private List<String> istruzioniAnticipo;
    private List<String> istruzioniUltimo;
    private int tempoPreparazione; // in minuti
    private double quantitaRisultante;
    private String unitaMisuraRisultato;
    private Set<String> tags;
    private boolean pubblicata;
    private Long id;
    
    @JsonIgnore
    private RicettaState state;
    
    // Costruttore per Jackson
    public Preparazione() {
        this.ingredienti = new ArrayList<>();
        this.istruzioniAnticipo = new ArrayList<>();
        this.istruzioniUltimo = new ArrayList<>();
        this.tags = new HashSet<>();
        this.state = new BozzaState();
        this.pubblicata = false;
        this.id = System.currentTimeMillis();
    }
    
    public Preparazione(String nome, String descrizione, User proprietario) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.proprietario = proprietario;
        this.ingredienti = new ArrayList<>();
        this.istruzioniAnticipo = new ArrayList<>();
        this.istruzioniUltimo = new ArrayList<>();
        this.tags = new HashSet<>();
        this.state = new BozzaState();
        this.pubblicata = false;
        this.id = System.currentTimeMillis();
    }
    
    // Getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { 
        if (state.canModify()) {
            this.nome = nome; 
        } else {
            throw new IllegalStateException("Non è possibile modificare il nome nello stato corrente");
        }
    }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) {
        if (state.canModify()) {
            this.descrizione = descrizione;
        } else {
            throw new IllegalStateException("Non è possibile modificare la descrizione nello stato corrente");
        }
    }
    
    public User getProprietario() { return proprietario; }
    public void setProprietario(User proprietario) { this.proprietario = proprietario; }
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
    public List<Dose> getIngredienti() { return ingredienti; }
    public void setIngredienti(List<Dose> ingredienti) { this.ingredienti = ingredienti; }
    public List<String> getIstruzioniAnticipo() { return istruzioniAnticipo; }
    public void setIstruzioniAnticipo(List<String> istruzioniAnticipo) { this.istruzioniAnticipo = istruzioniAnticipo; }
    public List<String> getIstruzioniUltimo() { return istruzioniUltimo; }
    public void setIstruzioniUltimo(List<String> istruzioniUltimo) { this.istruzioniUltimo = istruzioniUltimo; }
    public int getTempoPreparazione() { return tempoPreparazione; }
    public void setTempoPreparazione(int tempoPreparazione) { this.tempoPreparazione = tempoPreparazione; }
    public double getQuantitaRisultante() { return quantitaRisultante; }
    public void setQuantitaRisultante(double quantitaRisultante) { this.quantitaRisultante = quantitaRisultante; }
    public String getUnitaMisuraRisultato() { return unitaMisuraRisultato; }
    public void setUnitaMisuraRisultato(String unitaMisuraRisultato) { this.unitaMisuraRisultato = unitaMisuraRisultato; }
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    public boolean isPubblicata() { return pubblicata; }
    public void setPubblicata(boolean pubblicata) { this.pubblicata = pubblicata; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // Metodi per gestione stato
    @JsonIgnore
    public RicettaState getState() { 
        if (state == null) {
            state = pubblicata ? new PubblicataState() : new BozzaState();
        }
        return state; 
    }
    
    public void setState(RicettaState state) { 
        this.state = state; 
    }
    
    // Metodi delegati al pattern State
    public void pubblica() {
        getState().pubblica(this);
    }
    
    public void ritiraDallaPubblicazione() {
        getState().ritiraDallaPubblicazione(this);
    }
    
    public boolean canModify() {
        return getState().canModify();
    }
    
    public boolean canDelete() {
        return getState().canDelete();
    }
    
    public boolean isVisible() {
        return getState().isVisible();
    }
    
    @JsonIgnore
    public String getStateName() {
        return getState().getStateName();
    }
    
    // Metodi di modifica che rispettano lo stato
    public void aggiungiIngrediente(Dose dose) {
        if (canModify()) {
            ingredienti.add(dose);
        } else {
            throw new IllegalStateException("Non è possibile modificare gli ingredienti nello stato corrente");
        }
    }
    
    public void rimuoviIngrediente(Dose dose) {
        if (canModify()) {
            ingredienti.remove(dose);
        } else {
            throw new IllegalStateException("Non è possibile modificare gli ingredienti nello stato corrente");
        }
    }
    
    public void aggiungiTag(String tag) {
        if (canModify()) {
            tags.add(tag);
        } else {
            throw new IllegalStateException("Non è possibile modificare i tag nello stato corrente");
        }
    }
    
    public void rimuoviTag(String tag) {
        if (canModify()) {
            tags.remove(tag);
        } else {
            throw new IllegalStateException("Non è possibile modificare i tag nello stato corrente");
        }
    }
    
    public void aggiungiIstruzioneAnticipo(String istruzione) {
        if (canModify()) {
            istruzioniAnticipo.add(istruzione);
        } else {
            throw new IllegalStateException("Non è possibile modificare le istruzioni nello stato corrente");
        }
    }
    
    public void aggiungiIstruzioneUltimo(String istruzione) {
        if (canModify()) {
            istruzioniUltimo.add(istruzione);
        } else {
            throw new IllegalStateException("Non è possibile modificare le istruzioni nello stato corrente");
        }
    }
    
    // Metodo per cercare per tag
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    
    // Metodo per verificare se può essere usata come ingrediente
    public boolean canBeUsedAsIngredient() {
        return isPubblicata() && quantitaRisultante > 0;
    }
    
    @Override
    public String toString() {
        return nome + " (" + getStateName() + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Preparazione that = (Preparazione) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}