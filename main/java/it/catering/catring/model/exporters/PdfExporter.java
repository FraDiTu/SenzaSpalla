package it.catering.catring.model.exporters;

import it.catering.catring.model.entities.Menu;
import it.catering.catring.model.visitors.PdfExportVisitor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfExporter {
    private static PdfExporter instance;
    
    private PdfExporter() {}
    
    public static synchronized PdfExporter getInstance() {
        if (instance == null) {
            instance = new PdfExporter();
        }
        return instance;
    }
    
    public File exportMenuToPdf(Menu menu) throws IOException {
        // Utilizza il pattern Visitor per generare il contenuto
        PdfExportVisitor visitor = new PdfExportVisitor();
        menu.accept(visitor);
        
        // Crea la directory exports se non esiste
        Path exportsDir = Paths.get("exports");
        if (!Files.exists(exportsDir)) {
            Files.createDirectories(exportsDir);
        }
        
        // Genera il nome del file
        String fileName = "menu_" + menu.getId() + "_" + System.currentTimeMillis() + ".txt";
        File file = new File(exportsDir.toFile(), fileName);
        
        // Scrive il contenuto nel file (per semplicità usiamo un file di testo)
        // In un'implementazione reale useresti iText per generare un vero PDF
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("MENU CATERING\n");
            writer.write("=============\n\n");
            writer.write("Data generazione: " + new java.util.Date() + "\n");
            writer.write("Chef: " + menu.getChef().getNomeCompleto() + "\n\n");
            writer.write(visitor.getPdfContent());
            
            // Aggiungi footer
            writer.write("\n\n---\n");
            writer.write("Generato da Cat & Ring - Sistema di Gestione Catering\n");
        }
        
        return file;
    }
    
    public String getExportsDirectory() {
        return "exports";
    }
}