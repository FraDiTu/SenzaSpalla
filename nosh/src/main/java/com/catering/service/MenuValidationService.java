package com.catering.service;

import com.catering.model.domain.Menu;
import com.catering.model.visitor.MenuValidationVisitor;
import java.util.List;

/**
 * High Cohesion - tutte le responsabilità riguardano la validazione
 * Information Expert - sa come validare un menu
 */
public class MenuValidationService {
    
    // Information Expert - sa come validare completamente un menu
    public ValidationResult validateMenu(Menu menu) {
        MenuValidationVisitor validator = new MenuValidationVisitor();
        
        validator.visitMenu(menu);
        
        // Valida sezioni
        menu.getSezioni().keySet().forEach(validator::visitSezione);
        
        // Valida voci
        menu.getSezioni().values().stream()
            .flatMap(List::stream)
            .forEach(validator::visitVoce);
        
        return new ValidationResult(validator.isValid(), validator.getResult());
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return List.copyOf(errors); }
    }
}