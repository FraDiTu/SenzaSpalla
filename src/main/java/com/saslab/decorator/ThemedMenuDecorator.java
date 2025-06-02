package com.saslab.decorator;

import com.saslab.model.Menu;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Decorator per menù a tema
 * Aggiunge elementi tematici e decorativi ai menù
 */
public class ThemedMenuDecorator extends MenuDecorator {
    
    private String theme;
    private Map<String, String> themeElements;
    private String colorScheme;
    private String tableDecoration;
    
    public ThemedMenuDecorator(Menu menu, String theme) {
        super(menu);
        this.theme = theme;
        this.themeElements = new HashMap<>();
        initializeThemeElements();
    }
    
    private void initializeThemeElements() {
        switch (theme.toLowerCase()) {
            case "romantico":
                colorScheme = "Rosso e Bianco";
                tableDecoration = "Candele, petali di rosa, tovagliato elegante";
                themeElements.put("musica", "Sottofondo jazz o classica");
                themeElements.put("illuminazione", "Soffusa con candele");
                themeElements.put("fiori", "Rose rosse e bianche");
                break;
                
            case "marinaro":
                colorScheme = "Blu e Bianco";
                tableDecoration = "Conchiglie, rete da pesca decorativa, centrotavola marino";
                themeElements.put("musica", "Suoni del mare e musica mediterranea");
                themeElements.put("illuminazione", "Lanterne nautiche");
                themeElements.put("decorazioni", "Ancore, timoni, stelle marine");
                break;
                
            case "rustico":
                colorScheme = "Marrone e Verde";
                tableDecoration = "Legno naturale, fiori di campo, tovagliato grezzo";
                themeElements.put("musica", "Folk acustico");
                themeElements.put("illuminazione", "Lanterne rustiche e lucine");
                themeElements.put("decorazioni", "Elementi naturali, spighe, fiori secchi");
                break;
                
            default:
                colorScheme = "Personalizzato";
                tableDecoration = "Da definire con il cliente";
        }
    }
    
    @Override
    public String getEnhancedDescription() {
        StringBuilder enhanced = new StringBuilder();
        enhanced.append("=== MENÙ A TEMA: ").append(theme.toUpperCase()).append(" ===\n\n");
        
        if (decoratedMenu.getDescription() != null) {
            enhanced.append(decoratedMenu.getDescription()).append("\n\n");
        }
        
        enhanced.append("AMBIENTAZIONE:\n");
        enhanced.append("- Schema Colori: ").append(colorScheme).append("\n");
        enhanced.append("- Decorazione Tavoli: ").append(tableDecoration).append("\n");
        
        if (!themeElements.isEmpty()) {
            enhanced.append("\nELEMENTI TEMATICI:\n");
            for (Map.Entry<String, String> element : themeElements.entrySet()) {
                enhanced.append("- ").append(capitalize(element.getKey()))
                       .append(": ").append(element.getValue()).append("\n");
            }
        }
        
        enhanced.append("\nNOTE: Tutti i piatti saranno presentati in armonia con il tema scelto.");
        
        return enhanced.toString();
    }
    
    public void addThemeElement(String element, String description) {
        themeElements.put(element, description);
    }
    
    public String getTheme() {
        return theme;
    }
    
    public String getColorScheme() {
        return colorScheme;
    }
    
    public void setColorScheme(String colorScheme) {
        this.colorScheme = colorScheme;
    }
    
    public String getTableDecoration() {
        return tableDecoration;
    }
    
    public void setTableDecoration(String tableDecoration) {
        this.tableDecoration = tableDecoration;
    }
    
    public Map<String, String> getThemeElements() {
        return new HashMap<>(themeElements);
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}