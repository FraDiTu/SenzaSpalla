package com.catering.model.strategy;

import com.catering.model.domain.Menu;
import com.catering.model.domain.VoceMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

/**
 * TDD Test per Strategy pattern
 * Test delle diverse strategie di esportazione
 */
@DisplayName("Export Strategy Tests")
class ExportStrategyTest {
    
    @TempDir
    Path tempDir;
    
    private Menu sampleMenu;
    
    @BeforeEach
    void setUp() {
        Map<String, List<VoceMenu>> sezioni = new LinkedHashMap<>();
        sezioni.put("Antipasti", Arrays.asList(
            new VoceMenu("r1", "Bruschette", "Antipasti"),
            new VoceMenu("r2", "Vitello tonnato", "Antipasti")
        ));
        sezioni.put("Primi", Arrays.asList(
            new VoceMenu("r3", "Risotto ai funghi", "Primi")
        ));
        
        sampleMenu = new Menu("menu-1", "Menu Test", "evento-1", 
            LocalDateTime.now(), sezioni, "Note speciali", true);
    }
    
    @Test
    @DisplayName("PDF Export Strategy crea file con contenuto corretto")
    void pdfExportStrategyShouldCreateFileWithCorrectContent() throws IOException {
        // Given: strategia PDF e path di destinazione
        ExportStrategy pdfStrategy = new PDFExportStrategy();
        String basePath = tempDir.resolve("menu-test").toString();
        
        // When: esporto menu
        pdfStrategy.export(sampleMenu, basePath);
        
        // Then: file dovrebbe essere creato con contenuto corretto
        Path expectedFile = Path.of(basePath + ".pdf.txt");
        assertTrue(Files.exists(expectedFile), "File PDF dovrebbe essere creato");
        
        String content = Files.readString(expectedFile);
        assertAll("Contenuto PDF",
            () -> assertTrue(content.contains("=== MENU PDF ==="), "Dovrebbe contenere header"),
            () -> assertTrue(content.contains("Menu Test"), "Dovrebbe contenere titolo"),
            () -> assertTrue(content.contains("PUBBLICATO"), "Dovrebbe contenere stato"),
            () -> assertTrue(content.contains("=== ANTIPASTI ==="), "Dovrebbe contenere sezione antipasti"),
            () -> assertTrue(content.contains("• Bruschette"), "Dovrebbe contenere bruschette"),
            () -> assertTrue(content.contains("• Vitello tonnato"), "Dovrebbe contenere vitello tonnato"),
            () -> assertTrue(content.contains("=== PRIMI ==="), "Dovrebbe contenere sezione primi"),
            () -> assertTrue(content.contains("• Risotto ai funghi"), "Dovrebbe contenere risotto"),
            () -> assertTrue(content.contains("Note speciali"), "Dovrebbe contenere note")
        );
    }
    
    @Test
    @DisplayName("JSON Export Strategy crea file JSON valido")
    void jsonExportStrategyShouldCreateValidJsonFile() throws IOException {
        // Given: strategia JSON e path di destinazione
        ExportStrategy jsonStrategy = new JSONExportStrategy();
        String basePath = tempDir.resolve("menu-test").toString();
        
        // When: esporto menu
        jsonStrategy.export(sampleMenu, basePath);
        
        // Then: file JSON dovrebbe essere creato e leggibile
        Path expectedFile = Path.of(basePath + ".json");
        assertTrue(Files.exists(expectedFile), "File JSON dovrebbe essere creato");
        
        String content = Files.readString(expectedFile);
        assertAll("Contenuto JSON",
            () -> assertTrue(content.contains("\"titolo\" : \"Menu Test\""), 
                "JSON dovrebbe contenere titolo"),
            () -> assertTrue(content.contains("\"pubblicato\" : true"), 
                "JSON dovrebbe contenere stato pubblicato"),
            () -> assertTrue(content.contains("\"Antipasti\""), 
                "JSON dovrebbe contenere sezione antipasti"),
            () -> assertTrue(content.contains("\"Bruschette\""), 
                "JSON dovrebbe contenere bruschette")
        );
    }
    
    @Test
    @DisplayName("Strategy fornisce metadati corretti")
    void strategyShouldProvideCorrectMetadata() {
        // Given: diverse strategie
        ExportStrategy pdfStrategy = new PDFExportStrategy();
        ExportStrategy jsonStrategy = new JSONExportStrategy();
        
        // When/Then: verifico metadati
        assertEquals("pdf", pdfStrategy.getFileExtension(), 
            "PDF strategy dovrebbe avere estensione pdf");
        assertEquals("Esporta in formato PDF", pdfStrategy.getDescription(),
            "PDF strategy dovrebbe avere descrizione corretta");
        
        assertEquals("json", jsonStrategy.getFileExtension(),
            "JSON strategy dovrebbe avere estensione json");
        assertEquals("Esporta in formato JSON", jsonStrategy.getDescription(),
            "JSON strategy dovrebbe avere descrizione corretta");
    }
}