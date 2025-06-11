package com.catring.model;

public class Dose {
    private double quantitativo;
    private String unitaMisura;
    
    public Dose() {}
    
    public Dose(double quantitativo, String unitaMisura) {
        this.quantitativo = quantitativo;
        this.unitaMisura = unitaMisura;
    }
    
    // Getters e Setters
    public double getQuantitativo() { return quantitativo; }
    public void setQuantitativo(double quantitativo) { this.quantitativo = quantitativo; }
    
    public String getUnitaMisura() { return unitaMisura; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }
}