package com.saslab.visitor;

import com.saslab.model.Menu;
import com.saslab.model.MenuSection;
import com.saslab.model.MenuItem;
import com.saslab.Recipe;
import com.saslab.RecipeBook;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Visitor per calcolare i prezzi dei menù
 * Calcola il costo totale basandosi sui prezzi degli ingredienti
 */
public class MenuPriceCalculatorVisitor implements MenuVisitor {
    
    private double totalPrice = 0.0;
    private Map<String, Double> sectionPrices = new HashMap<>();
    private Map<String, Double> itemPrices = new HashMap<>();
    private String currentSection = "";
    private RecipeBook recipeBook = RecipeBook.getInstance();
    
    // Prezzi base per unità (esempio semplificato)
    private static final Map<String, Double> INGREDIENT_PRICES = new HashMap<>();
    static {
        INGREDIENT_PRICES.put("g", 0.005);  // 5 euro al kg
        INGREDIENT_PRICES.put("kg", 5.0);
        INGREDIENT_PRICES.put("l", 2.0);
        INGREDIENT_PRICES.put("ml", 0.002);
        INGREDIENT_PRICES.put("unità", 0.5);
    }
    
    @Override
    public void visitMenu(Menu menu) {
        totalPrice = 0.0;
        sectionPrices.clear();
        itemPrices.clear();
        
        // Aggiungi costi base per caratteristiche del menù
        if (menu.isRequiresChefPresence()) {
            totalPrice += 100.0; // Costo presenza chef
        }
        if (menu.isRequiresKitchenOnSite()) {
            totalPrice += 150.0; // Costo cucina mobile
        }
        
        // Visita tutte le sezioni
        for (MenuSection section : menu.getSections()) {
            currentSection = section.getTitle();
            visitMenuSection(section);
        }
    }
    
    @Override
    public void visitMenuSection(MenuSection section) {
        double sectionTotal = 0.0;
        
        // Visita tutti gli item della sezione
        for (MenuItem item : section.getMenuItems()) {
            visitMenuItem(item);
            Double itemPrice = itemPrices.get(item.getId());
            if (itemPrice != null) {
                sectionTotal += itemPrice;
            }
        }
        
        sectionPrices.put(section.getTitle(), sectionTotal);
        totalPrice += sectionTotal;
    }
    
    @Override
    public void visitMenuItem(MenuItem item) {
        double itemPrice = 0.0;
        
        // Se l'item ha un prezzo personalizzato, usa quello
        if (item.hasPrice()) {
            itemPrice = item.getPrice();
        } else {
            // Altrimenti calcola dal costo degli ingredienti della ricetta
            Recipe recipe = recipeBook.getRecipe(item.getRecipeId());
            if (recipe != null) {
                itemPrice = calculateRecipePrice(recipe);
            }
        }
        
        // Applica margine di profitto (es. 300%)
        itemPrice *= 3.0;
        
        itemPrices.put(item.getId(), itemPrice);
    }
    
    private double calculateRecipePrice(Recipe recipe) {
        double recipePrice = 0.0;
        
        for (Recipe.Ingredient ingredient : recipe.getIngredients()) {
            String unit = ingredient.getUnit().toLowerCase();
            Double unitPrice = INGREDIENT_PRICES.get(unit);
            
            if (unitPrice != null) {
                recipePrice += ingredient.getQuantity() * unitPrice;
            } else {
                // Prezzo default per unità sconosciute
                recipePrice += ingredient.getQuantity() * 0.5;
            }
        }
        
        // Aggiungi costo del lavoro basato sul tempo di preparazione
        recipePrice += recipe.getPreparationTime() * 0.5; // 30 euro/ora
        
        return recipePrice;
    }
    
    // Metodi per ottenere i risultati
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public Map<String, Double> getSectionPrices() {
        return new HashMap<>(sectionPrices);
    }
    
    public Map<String, Double> getItemPrices() {
        return new HashMap<>(itemPrices);
    }
    
    public String generatePriceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== ANALISI PREZZI MENÙ ===\n\n");
        
        report.append("DETTAGLIO PER SEZIONE:\n");
        for (Map.Entry<String, Double> entry : sectionPrices.entrySet()) {
            report.append(String.format("- %s: €%.2f\n", entry.getKey(), entry.getValue()));
        }
        
        report.append(String.format("\nTOTALE MENÙ: €%.2f\n", totalPrice));
        report.append(String.format("PREZZO CONSIGLIATO (con margine): €%.2f\n", totalPrice * 1.2));
        
        return report.toString();
    }
}