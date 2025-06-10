package com.catering.model.strategy;

import com.catering.model.domain.Menu;
import com.catering.model.domain.VoceMenu;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Strategy pattern - esportazione PDF (simulata con testo)
 */
public class PDFExportStrategy implements ExportStrategy {
    
    @Override
    public void export(Menu menu, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath + ".pdf.txt")) {
            writer.write("=== MENU PDF ===\n");
            writer.write("Titolo: " + menu.getTitolo() + "\n");
            writer.write("Data: " + menu.getDataCreazione() + "\n");
            writer.write("Stato: " + (menu.isPubblicato() ? "PUBBLICATO" : "BOZZA") + "\n\n");
            
            for (Map.Entry<String, List<VoceMenu>> sezione : menu.getSezioni().entrySet()) {
                writer.write("=== " + sezione.getKey().toUpperCase() + " ===\n");
                for (VoceMenu voce : sezione.getValue()) {
                    writer.write("• " + voce.getNomeVisualizzato() + "\n");
                }
                writer.write("\n");
            }
            
            if (menu.getInformazioniAggiuntive() != null) {
                writer.write("Note: " + menu.getInformazioniAggiuntive() + "\n");
            }
        }
    }
    
    @Override
    public String getFileExtension() {
        return "pdf";
    }
    
    @Override
    public String getDescription() {
        return "Esporta in formato PDF";
    }
}