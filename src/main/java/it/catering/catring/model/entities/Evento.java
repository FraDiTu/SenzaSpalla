package it.catering.catring.model.entities;

import it.catering.catring.model.states.StatoEvento;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Evento {
    private Long id;
    private String nome;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String luogo;
    private String tipo; // singolo/complesso
    private Cliente cliente;
    private Chef chef;
    private Organizzatore organizzatore;
    private List<Servizio> servizi;
    private StatoEvento stato;
    private String note;
    private boolean ricorrente;
    
    public Evento() {
        this.id = System.currentTimeMillis();
        this.servizi = new ArrayList<>();
        this.stato = StatoEvento.BOZZA;
    }
    
    public Evento(String nome, LocalDate dataInizio, String luogo, Cliente cliente, 
                 Organizzatore organizzatore) {
        this();
        this.nome = nome;
        this.dataInizio = dataInizio;
        this.dataFine = dataInizio; // Default stesso giorno
        this.luogo = luogo;
        this.cliente = cliente;
        this.organizzatore = organizzatore;
        this.tipo = "singolo";
    }
    
    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDate dataInizio) { this.dataInizio = dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public void setDataFine(LocalDate dataFine) { this.dataFine = dataFine; }
    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Chef getChef() { return chef; }
    public void setChef(Chef chef) { this.chef = chef; }
    public Organizzatore getOrganizzatore() { return organizzatore; }
    public void setOrganizzatore(Organizzatore organizzatore) { this.organizzatore = organizzatore; }
    public List<Servizio> getServizi() { return servizi; }
    public void setServizi(List<Servizio> servizi) { this.servizi = servizi; }
    public StatoEvento getStato() { return stato; }
    public void setStato(StatoEvento stato) { this.stato = stato; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public boolean isRicorrente() { return ricorrente; }
    public void setRicorrente(boolean ricorrente) { this.ricorrente = ricorrente; }
    
    public void aggiungiServizio(Servizio servizio) {
        servizi.add(servizio);
    }
    
    public void rimuoviServizio(Servizio servizio) {
        servizi.remove(servizio);
    }
    
    public boolean hasChefAssegnato() {
        return chef != null;
    }
    
    public boolean hasMenuApprovati() {
        return servizi.stream().allMatch(s -> s.getMenu() != null && s.getMenu().isUtilizzato());
    }
    
    @Override
    public String toString() {
        return nome + " (" + dataInizio + 
               (dataFine.equals(dataInizio) ? "" : " - " + dataFine) + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}