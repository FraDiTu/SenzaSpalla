package it.catering.catring.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.catering.catring.model.visitors.MenuVisitor;
import java.util.*;

public class Menu {
    private Long id;
    private String titolo;
    private String descrizione;
    private Chef chef;
    private List<SezioneMenu> sezioni;
    private boolean cuocoRichiesto;
    private boolean soloPiattiFreddi;
    private boolean cucinaRichiesta;
    private boolean adeguatoBuffet;
    private boolean fingerFood;
    private boolean utilizzato;
    private Date dataCreazione;
    
    // Costruttore per Jackson
    public Menu() {
        this.sezioni = new ArrayList<>();
        this.dataCreazione = new Date();
    }
    
    public Menu(String titolo, Chef chef) {
        this.id = System.currentTimeMillis();
        this.titolo = titolo;
        this.chef = chef;
        this.sezioni = new ArrayList<>();
        this.cuocoRichiesto = false;
        this.soloPiattiFreddi = false;
        this.cucinaRichiesta = false;
        this.adeguatoBuffet = false;
        this.fingerFood = false;
        this.utilizzato = false;
        this.dataCreazione = new Date();
    }
    
    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Chef getChef() { return chef; }
    public void setChef(Chef chef) { this.chef = chef; }
    public List<SezioneMenu> getSezioni() { return sezioni; }
    public void setSezioni(List<SezioneMenu> sezioni) { this.sezioni = sezioni; }
    public boolean isCuocoRichiesto() { return cuocoRichiesto; }
    public void setCuocoRichiesto(boolean cuocoRichiesto) { this.cuocoRichiesto = cuocoRichiesto; }
    public boolean isSoloPiattiFreddi() { return soloPiattiFreddi; }
    public void setSoloPiattiFreddi(boolean soloPiattiFreddi) { this.soloPiattiFreddi = soloPiattiFreddi; }
    public boolean isCucinaRichiesta() { return cucinaRichiesta; }
    public void setCucinaRichiesta(boolean cucinaRichiesta) { this.cucinaRichiesta = cucinaRichiesta; }
    public boolean isAdeguatoBuffet() { return adeguatoBuffet; }
    public void setAdeguatoBuffet(boolean adeguatoBuffet) { this.adeguatoBuffet = adeguatoBuffet; }
    public boolean isFingerFood() { return fingerFood; }
    public void setFingerFood(boolean fingerFood) { this.fingerFood = fingerFood; }
    public boolean isUtilizzato() { return utilizzato; }
    public void setUtilizzato(boolean utilizzato) { this.utilizzato = utilizzato; }
    public Date getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(Date dataCreazione) { this.dataCreazione = dataCreazione; }
    
    public void aggiungiSezione(SezioneMenu sezione) {
        sezioni.add(sezione);
    }
    
    public void rimuoviSezione(SezioneMenu sezione) {
        sezioni.remove(sezione);
    }
    
    public SezioneMenu trovaSezione(String nomeSezione) {
        return sezioni.stream()
                .filter(s -> s.getNome().equals(nomeSezione))
                .findFirst()
                .orElse(null);
    }
    
    public void spostaSezione(SezioneMenu sezione, int nuovaPosizione) {
        if (sezioni.contains(sezione)) {
            sezioni.remove(sezione);
            if (nuovaPosizione >= sezioni.size()) {
                sezioni.add(sezione);
            } else {
                sezioni.add(nuovaPosizione, sezione);
            }
        }
    }
    
    public List<Ricetta> getTutteLeRicette() {
        List<Ricetta> ricette = new ArrayList<>();
        for (SezioneMenu sezione : sezioni) {
            for (VoceMenu voce : sezione.getVoci()) {
                ricette.add(voce.getRicetta());
            }
        }
        return ricette;
    }
    
    public boolean contienRicetta(Ricetta ricetta) {
        return getTutteLeRicette().contains(ricetta);
    }
    
    // Pattern Visitor
    public void accept(MenuVisitor visitor) {
        visitor.visitMenu(this);
    }
    
    // Metodo per creare una copia
    @JsonIgnore
    public Menu createCopy(String nuovoTitolo, Chef nuovoChef) {
        Menu copia = new Menu(nuovoTitolo, nuovoChef);
        copia.setDescrizione(this.descrizione);
        copia.setCuocoRichiesto(this.cuocoRichiesto);
        copia.setSoloPiattiFreddi(this.soloPiattiFreddi);
        copia.setCucinaRichiesta(this.cucinaRichiesta);
        copia.setAdeguatoBuffet(this.adeguatoBuffet);
        copia.setFingerFood(this.fingerFood);
        
        // Copia le sezioni
        for (SezioneMenu sezione : this.sezioni) {
            SezioneMenu nuovaSezione = new SezioneMenu(sezione.getNome());
            for (VoceMenu voce : sezione.getVoci()) {
                VoceMenu nuovaVoce = new VoceMenu(voce.getRicetta(), voce.getNomeVoce(), voce.getSezione());
                nuovaSezione.aggiungiVoce(nuovaVoce);
            }
            copia.aggiungiSezione(nuovaSezione);
        }
        
        return copia;
    }
    
    @Override
    public String toString() {
        return titolo + " (Chef: " + chef.getNomeCompleto() + ")" + (utilizzato ? " [UTILIZZATO]" : "");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return Objects.equals(id, menu.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
