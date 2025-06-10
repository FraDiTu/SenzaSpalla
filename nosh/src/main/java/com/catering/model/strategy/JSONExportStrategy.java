package com.catering.model.strategy;

import com.catering.model.domain.Menu;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;

/**
 * Strategy pattern - esportazione JSON
 */
public class JSONExportStrategy implements ExportStrategy {
    private final ObjectMapper objectMapper;
    
    public JSONExportStrategy() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public void export(Menu menu, String filePath) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter()
                   .writeValue(new File(filePath + ".json"), menu);
    }
    
    @Override
    public String getFileExtension() {
        return "json";
    }
    
    @Override
    public String getDescription() {
        return "Esporta in formato JSON";
    }
}