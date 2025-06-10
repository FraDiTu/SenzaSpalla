package it.catering.catring.model.entities;

import java.time.LocalTime;
import java.util.Objects;

public class Servizio {
    private Long id;
    private String tipo; // pranzo/cena/buffet/coffee break/aperitivo
    private LocalTime fasciaOrariaInizio;
    private LocalTime fasciaOrariaFine;
    private Menu menu;
    private int numeroPersone;
    private String note;
    private boolean cuocoRichiestoServizio;
    
    public Servizio() {
        this.id = System.currentTimeMillis() + (long) (Math.random() * 1000);
    }
    
    public Servizio(String tipo, LocalTime fasciaOrariaInizio, LocalTime fasciaOrariaFine, 
                   int numeroPersone) {
        this();
        this.tipo = tipo;
        this.fasciaOrariaInizio = fasciaOrariaInizio;
        this.fasciaOrariaFine = fasciaOrariaFine;
        this.numeroPersone = numeroPersone;
    }
    
    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalTime getFasciaOrariaInizio() { return fasciaOrariaInizio; }
    public void setFasciaOrariaInizio(LocalTime fasciaOrariaInizio) { this.fasciaOrariaInizio = fasciaOrariaInizio; }
    public LocalTime getFasciaOrariaFine() { return fasciaOrariaFine; }
    public void setFasciaOrariaFine(LocalTime fasciaOrariaFine) { this.fasciaOrariaFine = fasciaOrariaFine; }
    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }
    public int getNumeroPersone() { return numeroPersone; }
    public void setNumeroPersone(int numeroPersone) { this.numeroPersone = numeroPersone; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public boolean isCuocoRichiestoServizio() { return cuocoRichiestoServizio; }
    public void setCuocoRichiestoServizio(boolean cuocoRichiestoServizio) { this.cuocoRichiestoServizio = cuocoRichiestoServizio; }
    
    public int getDurataMinuti() {
        if (fasciaOrariaInizio != null && fasciaOrariaFine != null) {
            return (int) java.time.Duration.between(fasciaOrariaInizio, fasciaOrariaFine).toMinutes();
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return tipo + " " + fasciaOrariaInizio + "-" + fasciaOrariaFine;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servizio servizio = (Servizio) o;
        return Objects.equals(id, servizio.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}