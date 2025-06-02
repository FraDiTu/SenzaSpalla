package com.saslab.example;

import com.saslab.*;
import com.saslab.controller.*;
import com.saslab.model.*;
import com.saslab.factory.*;
import com.saslab.decorator.*;
import com.saslab.visitor.*;
import com.saslab.strategy.*;
import com.saslab.adapter.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * Esempio di utilizzo dei pattern nell'applicazione Cat & Ring
 */
public class PatternUsageExample {
    
    public static void main(String[] args) {
        System.out.println("=== CAT & RING - ESEMPIO UTILIZZO PATTERN ===\n");
        
        // 1. SINGLETON - RecipeBook
        System.out.println("1. SINGLETON Pattern - RecipeBook");
        RecipeBook recipeBook = RecipeBook.getInstance();
        RecipeBook sameInstance = RecipeBook.getInstance();
        System.out.println("Stessa istanza: " + (recipeBook == sameInstance));
        
        // 2. FACTORY - UserFactory
        System.out.println("\n2. FACTORY Pattern - Creazione Utenti");
        UserFactory userFactory = UserFactory.getInstance();
        Chef chef = userFactory.createChef("Mario Rossi", "mario@catring.com", "Cucina Italiana", 10);
        System.out.println("Chef creato: " + chef);
        
        // 3. CONTROLLER - Gestione Eventi
        System.out.println("\n3. CONTROLLER Pattern - Gestione Eventi");
        EventController eventController = new EventController();
        String eventId = eventController.createEvent("ORG_001", "Matrimonio Bianchi", 
                                                    "Villa Rosa", LocalDate.now().plusDays(30), 100);
        System.out.println("Evento creato con ID: " + eventId);
        
        // 4. STRATEGY - Pricing
        System.out.println("\n4. STRATEGY Pattern - Calcolo Prezzi");
        Event event = eventController.getEvent(eventId);
        PricingStrategy.PricingContext pricingContext = 
            new PricingStrategy.PricingContext(new PricingStrategy.StandardPricingStrategy());
        double standardPrice = pricingContext.calculateEventPrice(event);
        System.out.println("Prezzo standard: €" + standardPrice);
        
        pricingContext.setStrategy(new PricingStrategy.PremiumPricingStrategy());
        double premiumPrice = pricingContext.calculateEventPrice(event);
        System.out.println("Prezzo premium: €" + premiumPrice);
        
        // 5. DECORATOR - Menu Enhancement
        System.out.println("\n5. DECORATOR Pattern - Menu Enhancement");
        Menu baseMenu = new Menu("MNU_001", "Menu Matrimonio", chef.getId());
        baseMenu.setDescription("Menu elegante per matrimonio");
        
        PremiumMenuDecorator premiumMenu = new PremiumMenuDecorator(baseMenu, "Chef Mario Rossi");
        premiumMenu.setWineRecommendations("Prosecco di Valdobbiadene DOCG");
        System.out.println(premiumMenu.getEnhancedDescription());
        
        // 6. VISITOR - Menu Operations
        System.out.println("\n6. VISITOR Pattern - Operazioni Menu");
        MenuController menuController = new MenuController(new RecipeController());
        menuController.createMenu(chef.getId(), "Menu Test");
        
        // Crea e pubblica una ricetta
        Recipe pasta = new Recipe("RCP_EX1", "Pasta Example", chef.getId(), 30);
        pasta.addIngredient("Pasta", 320, "g");
        pasta.publish();
        recipeBook.addRecipe(pasta);
        
        // Aggiungi al menu
        baseMenu.defineMenuSections(Arrays.asList("Primi"));
        baseMenu.addRecipeToSection(pasta.getId(), "Primi");
        
        // Usa visitor per export
        MenuExportVisitor exportVisitor = new MenuExportVisitor(MenuExportVisitor.ExportFormat.MARKDOWN);
        baseMenu.accept(exportVisitor);
        System.out.println("Menu esportato in Markdown:");
        System.out.println(exportVisitor.getExportedContent());
        
        // 7. ADAPTER - External System Integration
        System.out.println("\n7. ADAPTER Pattern - Integrazione Sistemi Esterni");
        ExternalSystemAdapter.ExportFacade.exportEvent(event, "JSON");
        ExternalSystemAdapter.ExportFacade.exportRecipe(pasta, "XML");
        
        // 8. OBSERVER - Event Updates
        System.out.println("\n8. OBSERVER Pattern - Notifiche Eventi");
        eventController.assignChefToEvent(eventId, chef.getId(), "ORG_001");
        System.out.println("Chef assegnato all'evento");
        
        // 9. STATE - Event State Management
        System.out.println("\n9. STATE Pattern - Gestione Stati");
        System.out.println("Stato iniziale evento: " + event.getState());
        eventController.addServiceToEvent(eventId, LocalDate.now().plusDays(30), 
                                         LocalTime.of(12, 0), LocalTime.of(15, 0), 
                                         Service.ServiceType.LUNCH);
        eventController.startEvent(eventId, "ORG_001");
        System.out.println("Stato dopo avvio: " + eventController.getEvent(eventId).getState());
        
        // 10. COMPOSITE - Menu Structure
        System.out.println("\n10. COMPOSITE Pattern - Struttura Menu");
        System.out.println("Sezioni nel menu: " + baseMenu.getSections().size());
        System.out.println("Items totali: " + baseMenu.getTotalMenuItems());
        
        System.out.println("\n=== ESEMPIO COMPLETATO ===");
    }
}