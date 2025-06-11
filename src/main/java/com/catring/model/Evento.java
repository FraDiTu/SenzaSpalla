package com.catring.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Evento {
    private String id;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String luogo;
    private String tipo;
    private String note;
    private int numeroPersone;
    private List<Servizio> servizi;
    private Cliente cliente;
    
    public Evento() {
        this.servizi = new ArrayList<>();
        this.numeroPersone = 50; // valore di default
    }
    
    public Evento(String id, LocalDate dataInizio, LocalDate dataFine, String luogo, String tipo, String note) {
        this();
        this.id = id;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.tipo = tipo;
        this.note = note;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public LocalDate getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDate dataInizio) { this.dataInizio = dataInizio; }
    
    public LocalDate getDataFine() { return dataFine; }
    public void setDataFine(LocalDate dataFine) { this.dataFine = dataFine; }
    
    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public int getNumeroPersone() { return numeroPersone; }
    public void setNumeroPersone(int numeroPersone) { this.numeroPersone = numeroPersone; }
    
    public List<Servizio> getServizi() { return servizi; }
    public void setServizi(List<Servizio> servizi) { this.servizi = servizi; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}