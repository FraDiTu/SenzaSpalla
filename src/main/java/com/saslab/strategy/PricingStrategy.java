package com.saslab.strategy;

import com.saslab.model.Event;
import com.saslab.model.Menu;
import com.saslab.model.Service;

/**
 * Strategy pattern per diversi algoritmi di calcolo prezzi
 * Implementa pattern Strategy (GoF) per permettere diversi metodi di pricing
 */
public class PricingStrategy {
    
    /**
     * Context per la strategia di pricing
     */
    public static class PricingContext {
        private IPricingStrategy strategy;
        
        public PricingContext(IPricingStrategy strategy) {
            this.strategy = strategy;
        }
        
        public void setStrategy(IPricingStrategy strategy) {
            this.strategy = strategy;
        }
        
        public double calculateEventPrice(Event event) {
            return strategy.calculatePrice(event);
        }
        
        public double calculateMenuPrice(Menu menu, int portions) {
            return strategy.calculateMenuPrice(menu, portions);
        }
        
        public double calculateServicePrice(Service service, int guests) {
            return strategy.calculateServicePrice(service, guests);
        }
    }
    
    /**
     * Interface per le strategie di pricing
     */
    public interface IPricingStrategy {
        double calculatePrice(Event event);
        double calculateMenuPrice(Menu menu, int portions);
        double calculateServicePrice(Service service, int guests);
        String getStrategyName();
    }
    
    /**
     * Strategia di pricing standard
     */
    public static class StandardPricingStrategy implements IPricingStrategy {
        
        private static final double BASE_PRICE_PER_PERSON = 25.0;
        private static final double SERVICE_MULTIPLIER = 1.2;
        private static final double COMPLEX_EVENT_MULTIPLIER = 1.5;
        
        @Override
        public double calculatePrice(Event event) {
            double basePrice = event.getExpectedGuests() * BASE_PRICE_PER_PERSON;
            
            // Moltiplicatore per eventi complessi
            if (event.getServices().size() > 1) {
                basePrice *= COMPLEX_EVENT_MULTIPLIER;
            }
            
            // Calcola prezzo per ogni servizio
            double servicesPrice = 0;
            for (Service service : event.getServices()) {
                servicesPrice += calculateServicePrice(service, event.getExpectedGuests());
            }
            
            return basePrice + servicesPrice;
        }
        
        @Override
        public double calculateMenuPrice(Menu menu, int portions) {
            double baseMenuPrice = 15.0; // Prezzo base per porzione
            
            // Aggiustamenti in base alle caratteristiche del menù
            if (menu.isRequiresChefPresence()) {
                baseMenuPrice += 5.0;
            }
            
            if (menu.isRequiresKitchenOnSite()) {
                baseMenuPrice += 3.0;
            }
            
            if (menu.isHasOnlyHotDishes()) {
                baseMenuPrice += 2.0;
            }
            
            if (menu.isFingerFoodOnly()) {
                baseMenuPrice -= 1.0; // Finger food costa meno
            }
            
            // Considera il numero di sezioni (più sezioni = più complesso)
            int sectionCount = menu.getSections().size();
            if (sectionCount > 3) {
                baseMenuPrice += (sectionCount - 3) * 1.5;
            }
            
            return baseMenuPrice * portions;
        }
        
        @Override
        public double calculateServicePrice(Service service, int guests) {
            double baseServicePrice = 8.0; // Prezzo base per servizio per persona
            
            // Aggiustamenti in base al tipo di servizio
            switch (service.getType()) {
                case BREAKFAST:
                    baseServicePrice = 6.0;
                    break;
                case LUNCH:
                    baseServicePrice = 12.0;
                    break;
                case DINNER:
                    baseServicePrice = 18.0;
                    break;
                case COCKTAIL:
                case APERITIF:
                    baseServicePrice = 15.0;
                    break;
                case BUFFET:
                    baseServicePrice = 10.0;
                    break;
                case COFFEE_BREAK:
                    baseServicePrice = 4.0;
                    break;
                case BRUNCH:
                    baseServicePrice = 14.0;
                    break;
            }
            
            // Aggiustamento per servizi che richiedono presenza chef
            if (service.isRequiresChefPresence()) {
                baseServicePrice += 3.0;
            }
            
            // Aggiustamento per durata del servizio
            int durationMinutes = service.getDurationInMinutes();
            if (durationMinutes > 180) { // Più di 3 ore
                baseServicePrice *= 1.3;
            }
            
            return baseServicePrice * guests * SERVICE_MULTIPLIER;
        }
        
        @Override
        public String getStrategyName() {
            return "Standard Pricing";
        }
    }
    
    /**
     * Strategia di pricing premium per eventi di alto livello
     */
    public static class PremiumPricingStrategy implements IPricingStrategy {
        
        private static final double PREMIUM_BASE_PRICE_PER_PERSON = 45.0;
        private static final double PREMIUM_SERVICE_MULTIPLIER = 1.8;
        private static final double LUXURY_EVENT_MULTIPLIER = 2.0;
        
        @Override
        public double calculatePrice(Event event) {
            double basePrice = event.getExpectedGuests() * PREMIUM_BASE_PRICE_PER_PERSON;
            
            // Moltiplicatore maggiore per eventi complessi
            if (event.getServices().size() > 2) {
                basePrice *= LUXURY_EVENT_MULTIPLIER;
            }
            
            // Calcola prezzo premium per ogni servizio
            double servicesPrice = 0;
            for (Service service : event.getServices()) {
                servicesPrice += calculateServicePrice(service, event.getExpectedGuests());
            }
            
            // Aggiunta per la consulenza premium
            double consultingFee = event.getExpectedGuests() * 5.0;
            
            return basePrice + servicesPrice + consultingFee;
        }
        
        @Override
        public double calculateMenuPrice(Menu menu, int portions) {
            double basePremiumPrice = 35.0; // Prezzo premium per porzione
            
            // Aggiustamenti premium
            if (menu.isRequiresChefPresence()) {
                basePremiumPrice += 10.0;
            }
            
            if (menu.isRequiresKitchenOnSite()) {
                basePremiumPrice += 8.0;
            }
            
            if (menu.isHasOnlyHotDishes()) {
                basePremiumPrice += 5.0;
            }
            
            // Premium per menù complessi
            int totalItems = menu.getTotalMenuItems();
            if (totalItems > 10) {
                basePremiumPrice += (totalItems - 10) * 2.0;
            }
            
            return basePremiumPrice * portions;
        }
        
        @Override
        public double calculateServicePrice(Service service, int guests) {
            double basePremiumServicePrice = 20.0; // Prezzo premium base
            
            // Prezzi premium per tipo di servizio
            switch (service.getType()) {
                case BREAKFAST:
                    basePremiumServicePrice = 15.0;
                    break;
                case LUNCH:
                    basePremiumServicePrice = 25.0;
                    break;
                case DINNER:
                    basePremiumServicePrice = 40.0;
                    break;
                case COCKTAIL:
                case APERITIF:
                    basePremiumServicePrice = 35.0;
                    break;
                case BUFFET:
                    basePremiumServicePrice = 22.0;
                    break;
                case COFFEE_BREAK:
                    basePremiumServicePrice = 12.0;
                    break;
                case BRUNCH:
                    basePremiumServicePrice = 28.0;
                    break;
            }
            
            // Servizio premium include sempre presenza chef
            basePremiumServicePrice += 8.0;
            
            return basePremiumServicePrice * guests * PREMIUM_SERVICE_MULTIPLIER;
        }
        
        @Override
        public String getStrategyName() {
            return "Premium Pricing";
        }
    }
    
    /**
     * Strategia di pricing budget per eventi economici
     */
    public static class BudgetPricingStrategy implements IPricingStrategy {
        
        private static final double BUDGET_BASE_PRICE_PER_PERSON = 12.0;
        private static final double BUDGET_SERVICE_MULTIPLIER = 1.0;
        
        @Override
        public double calculatePrice(Event event) {
            double basePrice = event.getExpectedGuests() * BUDGET_BASE_PRICE_PER_PERSON;
            
            // Sconto per eventi grandi
            if (event.getExpectedGuests() > 100) {
                basePrice *= 0.9; // 10% di sconto
            }
            
            // Calcola prezzo budget per ogni servizio
            double servicesPrice = 0;
            for (Service service : event.getServices()) {
                servicesPrice += calculateServicePrice(service, event.getExpectedGuests());
            }
            
            return basePrice + servicesPrice;
        }
        
        @Override
        public double calculateMenuPrice(Menu menu, int portions) {
            double baseBudgetPrice = 8.0; // Prezzo budget per porzione
            
            // Aggiustamenti minimi
            if (menu.isRequiresChefPresence()) {
                baseBudgetPrice += 2.0;
            }
            
            if (menu.isFingerFoodOnly()) {
                baseBudgetPrice -= 2.0; // Ulteriore sconto per finger food
            }
            
            // Sconto per menù semplici
            if (menu.getSections().size() <= 2) {
                baseBudgetPrice *= 0.95;
            }
            
            return baseBudgetPrice * portions;
        }
        
        @Override
        public double calculateServicePrice(Service service, int guests) {
            double baseBudgetServicePrice = 5.0; // Prezzo budget base
            
            // Prezzi ridotti per tipo di servizio
            switch (service.getType()) {
                case BREAKFAST:
                    baseBudgetServicePrice = 3.0;
                    break;
                case LUNCH:
                    baseBudgetServicePrice = 6.0;
                    break;
                case DINNER:
                    baseBudgetServicePrice = 8.0;
                    break;
                case COCKTAIL:
                case APERITIF:
                    baseBudgetServicePrice = 7.0;
                    break;
                case BUFFET:
                    baseBudgetServicePrice = 5.0;
                    break;
                case COFFEE_BREAK:
                    baseBudgetServicePrice = 2.0;
                    break;
                case BRUNCH:
                    baseBudgetServicePrice = 6.5;
                    break;
            }
            
            return baseBudgetServicePrice * guests * BUDGET_SERVICE_MULTIPLIER;
        }
        
        @Override
        public String getStrategyName() {
            return "Budget Pricing";
        }
    }
    
    /**
     * Factory per creare le strategie di pricing
     */
    public static class PricingStrategyFactory {
        
        public enum PricingType {
            STANDARD, PREMIUM, BUDGET
        }
        
        public static IPricingStrategy createStrategy(PricingType type) {
            switch (type) {
                case STANDARD:
                    return new StandardPricingStrategy();
                case PREMIUM:
                    return new PremiumPricingStrategy();
                case BUDGET:
                    return new BudgetPricingStrategy();
                default:
                    return new StandardPricingStrategy();
            }
        }
        
        public static IPricingStrategy createStrategy(String strategyName) {
            switch (strategyName.toUpperCase()) {
                case "STANDARD":
                    return new StandardPricingStrategy();
                case "PREMIUM":
                    return new PremiumPricingStrategy();
                case "BUDGET":
                    return new BudgetPricingStrategy();
                default:
                    throw new IllegalArgumentException("Strategia non supportata: " + strategyName);
            }
        }
    }
}