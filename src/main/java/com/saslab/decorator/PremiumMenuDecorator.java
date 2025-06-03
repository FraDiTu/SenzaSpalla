package com.saslab.decorator;

import com.saslab.model.Menu;

/**
 * Concrete Decorator per menù premium
 * Aggiunge informazioni e funzionalità premium ai menù
 */
public class PremiumMenuDecorator extends MenuDecorator {
    
    private String chefSignature;
    private String wineRecommendations;
    private double premiumCostMultiplier = 1.5;
    
    public PremiumMenuDecorator(Menu menu, String chefSignature) {
        super(menu);
        this.chefSignature = chefSignature;
    }
    
    @Override
    public String getEnhancedDescription() {
        StringBuilder enhanced = new StringBuilder();
        enhanced.append("*** MENÙ PREMIUM ***\n");
        enhanced.append("Chef Signature: ").append(chefSignature).append("\n\n");
        
        if (decoratedMenu.getDescription() != null) {
            enhanced.append("Descrizione: ").append(decoratedMenu.getDescription()).append("\n\n");
        }
        
        enhanced.append("Caratteristiche Premium:\n");
        enhanced.append("- Ingredienti di prima scelta selezionati personalmente dallo chef\n");
        enhanced.append("- Presentazione artistica dei piatti\n");
        enhanced.append("- Servizio dedicato con personale specializzato\n");
        
        if (wineRecommendations != null) {
            enhanced.append("\nAbbinamenti Vini Consigliati:\n");
            enhanced.append(wineRecommendations);
        }
        
        return enhanced.toString();
    }
    
    public void setWineRecommendations(String wineRecommendations) {
        this.wineRecommendations = wineRecommendations;
    }
    
    public String getWineRecommendations() {
        return wineRecommendations;
    }
    
    public double calculatePremiumPrice(double basePrice) {
        return basePrice * premiumCostMultiplier;
    }
    
    public String getChefSignature() {
        return chefSignature;
    }
    
    public void setPremiumCostMultiplier(double multiplier) {
        if (multiplier > 1.0) {
            this.premiumCostMultiplier = multiplier;
        }
    }
}