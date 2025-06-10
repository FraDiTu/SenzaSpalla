package it.catering.catring.model.entities;

public class Dose {
    private Ingrediente ingrediente;
    private double quantita;
    
    public Dose(Ingrediente ingrediente, double quantita) {
        this.ingrediente = ingrediente;
        this.quantita = quantita;
    }
    
    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    public double getQuantita() { return quantita; }
    public void setQuantita(double quantita) { this.quantita = quantita; }
    
    @Override
    public String toString() {
        return quantita + " " + ingrediente.getUnitaMisura() + " di " + ingrediente.getNome();
    }
}