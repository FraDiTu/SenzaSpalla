package it.catering.catring.model.exporters;

import it.catering.catring.model.entities.Menu;
import java.io.File;
import java.io.IOException;

// Pattern Decorator per aggiungere funzionalità di export
public abstract class MenuExportDecorator {
    protected PdfExporter exporter;
    
    public MenuExportDecorator(PdfExporter exporter) {
        this.exporter = exporter;
    }
    
    public abstract File exportMenuToPdf(Menu menu) throws IOException;
}
