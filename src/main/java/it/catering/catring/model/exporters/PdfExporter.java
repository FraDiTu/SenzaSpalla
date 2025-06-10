// File: src/main/java/it/catering/catring/model/exporters/PdfExporter.java
package it.catering.catring.model.exporters;

import it.catering.catring.model.entities.Menu;
import it.catering.catring.model.visitors.PdfExportVisitor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        
        // Crea la directory exports nella cartella del progetto se non esiste
        Path projectDir = Paths.get(System.getProperty("user.dir"));
        Path exportsDir = projectDir.resolve("exports");
        
        if (!Files.exists(exportsDir)) {
            Files.createDirectories(exportsDir);
        }
        
        // Genera il nome del file con timestamp leggibile
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String fileName = "menu_" + menu.getTitolo().replaceAll("[^a-zA-Z0-9]", "") + "" + timestamp + ".txt";
        File file = new File(exportsDir.toFile(), fileName);
        
        // Scrive il contenuto nel file (per semplicità usiamo un file di testo)
        // In un'implementazione reale useresti iText per generare un vero PDF
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("MENU CATERING\n");
            writer.write("=============\n\n");
            writer.write("Data generazione: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n");
            writer.write("Chef: " + menu.getChef().getNomeCompleto() + "\n\n");
            writer.write(visitor.getPdfContent());
            
            // Aggiungi footer
            writer.write("\n\n---\n");
            writer.write("Generato da Cat & Ring - Sistema di Gestione Catering\n");
            writer.write("File salvato in: " + file.getAbsolutePath() + "\n");
        }
        
        return file;
    }
    
    public String getExportsDirectory() {
        Path projectDir = Paths.get(System.getProperty("user.dir"));
        Path exportsDir = projectDir.resolve("exports");
        return exportsDir.toAbsolutePath().toString();
    }
    
    public String getExportsDirectoryUserFriendly() {
        return "Cartella del progetto > exports";
    }
}