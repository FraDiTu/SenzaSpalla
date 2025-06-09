package it.catering.catring.model.entities;

import it.catering.catring.model.visitors.MenuVisitor;
import java.util.Objects;

public class VoceMenu {
    private Ricetta ricetta;
    private String nomeVoce;
    private String sezione;
    
    // Costruttore per Jackson
    public VoceMenu() {}
    
    public VoceMenu(Ricetta ricetta, String nomeVoce, String sezione) {
        this.ricetta = ricetta;
        this.nomeVoce = nomeVoce != null ? nomeVoce : ricetta.getNome();
        this.sezione = sezione;
    }
    
    public Ricetta getRicetta() { return ricetta; }
    public void setRicetta(Ricetta ricetta) { this.ricetta = ricetta; }
    public String getNomeVoce() { return nomeVoce; }
    public void setNomeVoce(String nomeVoce) { this.nomeVoce = nomeVoce; }
    public String getSezione() { return sezione; }
    public void setSezione(String sezione) { this.sezione = sezione; }
    
    // Pattern Visitor
    public void accept(MenuVisitor visitor) {
        visitor.visitVoce(this);
    }
    
    @Override
    public String toString() {
        return nomeVoce;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoceMenu voceMenu = (VoceMenu) o;
        return Objects.equals(ricetta, voceMenu.ricetta) && Objects.equals(sezione, voceMenu.sezione);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ricetta, sezione);
    }
}