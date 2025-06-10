package com.catring.model;

import java.util.ArrayList;
import java.util.List;

public class SocietaCatering {
    private String id;
    private String nome;
    private String indirizzo;
    private String partitaIVA;
    private String contatti;
    private List<Evento> eventi;
    private List<Utente> utenti;
    
    public SocietaCatering() {
        this.eventi = new ArrayList<>();
        this.utenti = new ArrayList<>();
    }
    
    public SocietaCatering(String id, String nome, String indirizzo, String partitaIVA, String contatti) {
        this();
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.partitaIVA = partitaIVA;
        this.contatti = contatti;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    
    public String getPartitaIVA() { return partitaIVA; }
    public void setPartitaIVA(String partitaIVA) { this.partitaIVA = partitaIVA; }
    
    public String getContatti() { return contatti; }
    public void setContatti(String contatti) { this.contatti = contatti; }
    
    public List<Evento> getEventi() { return eventi; }
    public void setEventi(List<Evento> eventi) { this.eventi = eventi; }
    
    public List<Utente> getUtenti() { return utenti; }
    public void setUtenti(List<Utente> utenti) { this.utenti = utenti; }
}