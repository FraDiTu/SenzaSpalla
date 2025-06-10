package com.catering.model.strategy;

import com.catering.model.domain.Menu;
import java.io.IOException;

/**
 * Strategy pattern - strategia di esportazione
 */
public interface ExportStrategy {
    void export(Menu menu, String filePath) throws IOException;
    String getFileExtension();
    String getDescription();
}
