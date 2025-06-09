package it.catering.catring.model.entities;

public class Ricetta extends Preparazione {
    private int numeroPortate;
    
    public Ricetta(String nome, String descrizione, User proprietario, int numeroPortate) {
        super(nome, descrizione, proprietario);
        this.numeroPortate = numeroPortate;
    }
    
    public int getNumeroPortate() { return numeroPortate; }
    public void setNumeroPortate(int numeroPortate) { this.numeroPortate = numeroPortate; }
    
    @Override
    public String toString() {
        return "Ricetta: " + getNome() + " (" + numeroPortate + " portate)" + (isPubblicata() ? " (Pubblicata)" : " (Bozza)");
    }
}