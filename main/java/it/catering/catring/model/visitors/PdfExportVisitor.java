package it.catering.catring.model.visitors;

import it.catering.catring.model.entities.Menu;
import it.catering.catring.model.entities.SezioneMenu;
import it.catering.catring.model.entities.VoceMenu;

public class PdfExportVisitor implements MenuVisitor {
    private StringBuilder pdfContent;
    
    public PdfExportVisitor() {
        this.pdfContent = new StringBuilder();
    }
    
    @Override
    public void visitMenu(Menu menu) {
        pdfContent.append("=== ").append(menu.getTitolo()).append(" ===\n\n");
        
        if (menu.getDescrizione() != null && !menu.getDescrizione().trim().isEmpty()) {
            pdfContent.append(menu.getDescrizione()).append("\n\n");
        }
        
        // Informazioni aggiuntive
        if (menu.isCuocoRichiesto()) {
            pdfContent.append("* Cuoco richiesto durante il servizio\n");
        }
        if (menu.isSoloPiattiFreddi()) {
            pdfContent.append("* Solo piatti freddi\n");
        }
        if (menu.isCucinaRichiesta()) {
            pdfContent.append("* Cucina richiesta nella sede\n");
        }
        if (menu.isAdeguatoBuffet()) {
            pdfContent.append("* Adeguato per buffet\n");
        }
        if (menu.isFingerFood()) {
            pdfContent.append("* Finger food\n");
        }
        
        pdfContent.append("\n");
        
        // Visita le sezioni
        for (SezioneMenu sezione : menu.getSezioni()) {
            sezione.accept(this);
        }
    }
    
    @Override
    public void visitSezione(SezioneMenu sezione) {
        pdfContent.append("--- ").append(sezione.getNome()).append(" ---\n");
        
        // Visita le voci
        for (VoceMenu voce : sezione.getVoci()) {
            voce.accept(this);
        }
        
        pdfContent.append("\n");
    }
    
    @Override
    public void visitVoce(VoceMenu voce) {
        pdfContent.append("• ").append(voce.getNomeVoce()).append("\n");
    }
    
    public String getPdfContent() {
        return pdfContent.toString();
    }
}
