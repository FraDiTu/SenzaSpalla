package it.catering.catring.model.exporters;

import it.catering.catring.model.entities.Menu;
import it.catering.catring.model.entities.Ricetta;
import it.catering.catring.model.visitors.MenuStatsVisitor;
import java.io.*;
import java.nio.file.Files;

public class DetailedMenuExportDecorator extends MenuExportDecorator {
    
    public DetailedMenuExportDecorator(PdfExporter exporter) {
        super(exporter);
    }
    
    @Override
    public File exportMenuToPdf(Menu menu) throws IOException {
        // Esporta il menu base
        File baseFile = exporter.exportMenuToPdf(menu);
        
        // Aggiungi dettagli extra
        try (BufferedWriter writer = Files.newBufferedWriter(baseFile.toPath(), 
                java.nio.file.StandardOpenOption.APPEND)) {
            
            writer.write("\n\n=== DETTAGLI RICETTE ===\n");
            
            for (Ricetta ricetta : menu.getTutteLeRicette()) {
                writer.write("\n--- " + ricetta.getNome() + " ---\n");
                writer.write("Autore: " + (ricetta.getAutore() != null ? ricetta.getAutore() : "N/A") + "\n");
                writer.write("Tempo preparazione: " + ricetta.getTempoPreparazione() + " minuti\n");
                writer.write("Porzioni: " + ricetta.getNumeroPortate() + "\n");
                
                if (!ricetta.getTags().isEmpty()) {
                    writer.write("Tag: " + String.join(", ", ricetta.getTags()) + "\n");
                }
                
                if (ricetta.getDescrizione() != null && !ricetta.getDescrizione().trim().isEmpty()) {
                    writer.write("Descrizione: " + ricetta.getDescrizione() + "\n");
                }
            }
            
            // Aggiungi statistiche usando il Visitor
            MenuStatsVisitor statsVisitor = new MenuStatsVisitor();
            menu.accept(statsVisitor);
            
            writer.write("\n\n=== STATISTICHE MENU ===\n");
            writer.write(statsVisitor.getStatsReport());
        }
        
        return baseFile;
    }
}