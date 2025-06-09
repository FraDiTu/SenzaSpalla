package it.catering.catring.model.visitors;

import it.catering.catring.model.entities.Menu;
import it.catering.catring.model.entities.SezioneMenu;
import it.catering.catring.model.entities.VoceMenu;

public class MenuStatsVisitor implements MenuVisitor {
    private int totalSezioni = 0;
    private int totalVoci = 0;
    private int totalPortate = 0;
    
    @Override
    public void visitMenu(Menu menu) {
        // Reset contatori
        totalSezioni = 0;
        totalVoci = 0;
        totalPortate = 0;
        
        // Visita tutte le sezioni
        for (SezioneMenu sezione : menu.getSezioni()) {
            sezione.accept(this);
        }
    }
    
    @Override
    public void visitSezione(SezioneMenu sezione) {
        totalSezioni++;
        
        // Visita tutte le voci
        for (VoceMenu voce : sezione.getVoci()) {
            voce.accept(this);
        }
    }
    
    @Override
    public void visitVoce(VoceMenu voce) {
        totalVoci++;
        totalPortate += voce.getRicetta().getNumeroPortate();
    }
    
    public int getTotalSezioni() { return totalSezioni; }
    public int getTotalVoci() { return totalVoci; }
    public int getTotalPortate() { return totalPortate; }
    
    public String getStatsReport() {
        return String.format("Statistiche Menu:\n" +
                           "- Sezioni: %d\n" +
                           "- Voci: %d\n" +
                           "- Portate totali: %d", 
                           totalSezioni, totalVoci, totalPortate);
    }
}