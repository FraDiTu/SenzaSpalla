package com.saslab;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.saslab.model.Preparation;

/**
 * Classe che gestisce il ricettario del sistema Cat &amp; Ring
 * Implementa pattern Singleton per garantire un'unica istanza del ricettario
 * Implementa pattern Information Expert per la gestione delle ricette
 */
public class RecipeBook {
    
    private static RecipeBook instance;
    private final Map<String, Recipe> recipes;
    private final Map<String, Preparation> preparations;
    private String version;
    private LocalDateTime lastUpdate;
    
    // Private constructor per Singleton
    private RecipeBook() {
        this.recipes = new HashMap<>();
        this.preparations = new HashMap<>();
        this.version = "1.0.0";
        this.lastUpdate = LocalDateTime.now();
    }
    
    // Singleton getInstance
    public static synchronized RecipeBook getInstance() {
        if (instance == null) {
            instance = new RecipeBook();
        }
        return instance;
    }
    
    // Metodi per le ricette
    public void addRecipe(Recipe recipe) {
        Objects.requireNonNull(recipe, "La ricetta non può essere null");
        recipes.put(recipe.getId(), recipe);
        updateVersion();
    }
    
    public Recipe getRecipe(String recipeId) {
        return recipes.get(recipeId);
    }
    
    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes.values());
    }
    
    public List<Recipe> getPublishedRecipes() {
        return recipes.values().stream()
                .filter(Recipe::isPublished)
                .collect(Collectors.toList());
    }
    
    public List<Recipe> getRecipesByOwner(String ownerId) {
        return recipes.values().stream()
                .filter(recipe -> recipe.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }
    
    public List<Recipe> searchRecipesByTag(String tag) {
        return recipes.values().stream()
                .filter(Recipe::isPublished)
                .filter(recipe -> recipe.getTags().contains(tag))
                .collect(Collectors.toList());
    }
    
    public List<Recipe> searchRecipesByName(String namePattern) {
        String pattern = namePattern.toLowerCase();
        return recipes.values().stream()
                .filter(Recipe::isPublished)
                .filter(recipe -> recipe.getName().toLowerCase().contains(pattern))
                .collect(Collectors.toList());
    }
    
    public boolean removeRecipe(String recipeId, String userId) {
        Recipe recipe = recipes.get(recipeId);
        if (recipe == null) {
            return false;
        }
        
        // Solo il proprietario può eliminare la ricetta
        if (!recipe.getOwnerId().equals(userId)) {
            throw new SecurityException("Solo il proprietario può eliminare la ricetta");
        }
        
        // Non è possibile eliminare ricette in uso (questo controllo dovrebbe essere fatto dal controller)
        recipes.remove(recipeId);
        updateVersion();
        return true;
    }
    
    // Metodi per le preparazioni
    public void addPreparation(Preparation preparation) {
        Objects.requireNonNull(preparation, "La preparazione non può essere null");
        preparations.put(preparation.getId(), preparation);
        updateVersion();
    }
    
    public Preparation getPreparation(String preparationId) {
        return preparations.get(preparationId);
    }
    
    public List<Preparation> getAllPreparations() {
        return new ArrayList<>(preparations.values());
    }
    
    public List<Preparation> getPublishedPreparations() {
        return preparations.values().stream()
                .filter(Preparation::isPublished)
                .collect(Collectors.toList());
    }
    
    public List<Preparation> getPreparationsByOwner(String ownerId) {
        return preparations.values().stream()
                .filter(prep -> prep.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }
    
    public boolean removePreparation(String preparationId, String userId) {
        Preparation preparation = preparations.get(preparationId);
        if (preparation == null) {
            return false;
        }
        
        // Solo il proprietario può eliminare la preparazione
        if (!preparation.getOwnerId().equals(userId)) {
            throw new SecurityException("Solo il proprietario può eliminare la preparazione");
        }
        
        preparations.remove(preparationId);
        updateVersion();
        return true;
    }
    
    // Metodi di utilità
    public Set<String> getAllTags() {
        Set<String> allTags = new HashSet<>();
        
        recipes.values().stream()
                .filter(Recipe::isPublished)
                .forEach(recipe -> allTags.addAll(recipe.getTags()));
                
        preparations.values().stream()
                .filter(Preparation::isPublished)
                .forEach(prep -> allTags.addAll(prep.getTags()));
                
        return allTags;
    }
    
    public int getTotalRecipeCount() {
        return recipes.size();
    }
    
    public int getPublishedRecipeCount() {
        return (int) recipes.values().stream()
                .filter(Recipe::isPublished)
                .count();
    }
    
    public int getTotalPreparationCount() {
        return preparations.size();
    }
    
    private void updateVersion() {
        this.lastUpdate = LocalDateTime.now();
        // Incrementa la versione (semplificato)
        String[] parts = version.split("\\.");
        int patch = Integer.parseInt(parts[2]) + 1;
        this.version = parts[0] + "." + parts[1] + "." + patch;
    }
    
    // Getters
    public String getVersion() {
        return version;
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    // Metodo per verificare se una ricetta è in uso
    public boolean isRecipeInUse(String recipeId) {
        // Questo metodo dovrebbe verificare se la ricetta è utilizzata in qualche menù
        // Per ora restituisce false, ma dovrebbe essere implementato con il sistema dei menù
        return false;
    }
    
    public boolean isPreparationInUse(String preparationId) {
        // Verifica se la preparazione è utilizzata in qualche ricetta
        return recipes.values().stream()
                .filter(Recipe::isPublished)
                .anyMatch(recipe -> recipe.getIngredients().stream()
                        .anyMatch(ing -> ing.getName().equals(preparationId)));
    }
    
    @Override
    public String toString() {
        return String.format("RecipeBook{version='%s', recipes=%d, preparations=%d, lastUpdate=%s}", 
                           version, recipes.size(), preparations.size(), lastUpdate);
    }
}