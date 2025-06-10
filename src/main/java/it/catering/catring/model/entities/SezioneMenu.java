package it.catering.catring.model.entities;

import it.catering.catring.model.visitors.MenuVisitor;
import java.util.*;

public class SezioneMenu {
    private String nome;
    private List<VoceMenu> voci;
    
    // Costruttore per Jackson
    public SezioneMenu() {
        this.voci = new ArrayList<>();
    }
    
    public SezioneMenu(String nome) {
        this.nome = nome;
        this.voci = new ArrayList<>();
    }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<VoceMenu> getVoci() { return voci; }
    public void setVoci(List<VoceMenu> voci) { this.voci = voci; }
    
    public void aggiungiVoce(VoceMenu voce) {
        voci.add(voce);
    }
    
    public void rimuoviVoce(VoceMenu voce) {
        voci.remove(voce);
    }
    
    public void spostaVoce(VoceMenu voce, int nuovaPosizione) {
        if (voci.contains(voce)) {
            voci.remove(voce);
            if (nuovaPosizione >= voci.size()) {
                voci.add(voce);
            } else {
                voci.add(nuovaPosizione, voce);
            }
        }
    }
    
    // Pattern Visitor
    public void accept(MenuVisitor visitor) {
        visitor.visitSezione(this);
    }
    
    @Override
    public String toString() {
        return nome + " (" + voci.size() + " voci)";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SezioneMenu that = (SezioneMenu) o;
        return Objects.equals(nome, that.nome);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}