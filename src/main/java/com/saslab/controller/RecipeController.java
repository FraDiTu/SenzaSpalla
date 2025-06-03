package com.saslab.controller;

import com.saslab.Recipe;
import com.saslab.RecipeBook;
import com.saslab.model.Preparation;
import java.util.List;
import java.util.UUID;

/**
 * Controller per la gestione delle ricette
 * Implementa pattern Controller (GRASP) per coordinare le operazioni sulle ricette
 * Implementa pattern Information Expert delegando al RecipeBook
 */
public class RecipeController {
    
    private final RecipeBook recipeBook;
    
    public RecipeController() {
        this.recipeBook = RecipeBook.getInstance();
    }
    
    /**
     * Crea una nuova ricetta
     * Implementa pattern Creator
     */
    public String createRecipe(String ownerId, String name, String description, int preparationTime) {
        validateRecipeCreationParameters(ownerId, name, preparationTime);
        
        String recipeId = generateRecipeId();
        Recipe recipe = new Recipe(recipeId, name, ownerId, preparationTime);
        
        if (description != null && !description.trim().isEmpty()) {
            recipe.setDescription(description);
        }
        
        recipeBook.addRecipe(recipe);
        return recipeId;
    }
    
    /**
     * Crea una copia di una ricetta esistente
     */
    public String copyRecipe(String originalRecipeId, String newOwnerId) {
        Recipe originalRecipe = recipeBook.getRecipe(originalRecipeId);
        if (originalRecipe == null) {
            throw new IllegalArgumentException("Ricetta originale non trovata");
        }
        
        if (!originalRecipe.isPublished()) {
            throw new IllegalStateException("È possibile copiare solo ricette pubblicate");
        }
        
        String newRecipeId = generateRecipeId();
        Recipe copiedRecipe = originalRecipe.createCopy(newRecipeId, newOwnerId);
        
        recipeBook.addRecipe(copiedRecipe);
        return newRecipeId;
    }
    
    /**
     * Aggiunge un ingrediente alla ricetta
     */
    public void addIngredient(String recipeId, String ingredientName, double quantity, String unit) {
        Recipe recipe = getRecipeOrThrow(recipeId);
        recipe.addIngredient(ingredientName, quantity, unit);
    }
    
    /**
     * Aggiunge un tag alla ricetta
     */
    public void addTag(String recipeId, String tag) {
        Recipe recipe = getRecipeOrThrow(recipeId);
        recipe.addTag(tag);
    }
    
    /**
     * Aggiunge istruzioni di preparazione in anticipo
     */
    public void addAdvanceInstruction(String recipeId, String instruction) {
        Recipe recipe = getRecipeOrThrow(recipeId);
        recipe.addAdvanceInstruction(instruction);
    }
    
    /**
     * Aggiunge istruzioni dell'ultimo minuto
     */
    public void addLastMinuteInstruction(String recipeId, String instruction) {
        Recipe recipe = getRecipeOrThrow(recipeId);
        recipe.addLastMinuteInstruction(instruction);
    }
    
    /**
     * Pubblica una ricetta
     */
    public void publishRecipe(String recipeId) {
        Recipe recipe = getRecipeOrThrow(recipeId);
        recipe.publish();
    }
    
    /**
     * Ritira una ricetta dalla pubblicazione
     */
    public void unpublishRecipe(String recipeId, String userId) {
        Recipe recipe = getRecipeOrThrow(recipeId);
        
        if (!recipe.getOwnerId().equals(userId)) {
            throw new SecurityException("Solo il proprietario può ritirare la ricetta dalla pubblicazione");
        }
        
        // Verifica che non sia in uso (dovrebbe essere implementato con controlli sui menù)
        if (recipeBook.isRecipeInUse(recipeId)) {
            throw new IllegalStateException("Non è possibile ritirare una ricetta in uso");
        }
        
        recipe.unpublish();
    }
    
    /**
     * Elimina una ricetta
     */
    public boolean deleteRecipe(String recipeId, String userId) {
        Recipe recipe = recipeBook.getRecipe(recipeId);
        if (recipe == null) {
            return false;
        }
        
        if (!recipe.getOwnerId().equals(userId)) {
            throw new SecurityException("Solo il proprietario può eliminare la ricetta");
        }
        
        if (recipeBook.isRecipeInUse(recipeId)) {
            throw new IllegalStateException("Non è possibile eliminare una ricetta in uso");
        }
        
        return recipeBook.removeRecipe(recipeId, userId);
    }
    
    /**
     * Ottiene una ricetta per ID
     */
    public Recipe getRecipe(String recipeId) {
        return recipeBook.getRecipe(recipeId);
    }
    
    /**
     * Ottiene tutte le ricette pubblicate
     */
    public List<Recipe> getPublishedRecipes() {
        return recipeBook.getPublishedRecipes();
    }
    
    /**
     * Ottiene le ricette di un proprietario
     */
    public List<Recipe> getRecipesByOwner(String ownerId) {
        return recipeBook.getRecipesByOwner(ownerId);
    }
    
    /**
     * Cerca ricette per tag
     */
    public List<Recipe> searchRecipesByTag(String tag) {
        return recipeBook.searchRecipesByTag(tag);
    }
    
    /**
     * Cerca ricette per nome
     */
    public List<Recipe> searchRecipesByName(String namePattern) {
        return recipeBook.searchRecipesByName(namePattern);
    }
    
    /**
     * Ottiene tutti i tag disponibili
     */
    public List<String> getAllTags() {
        return recipeBook.getAllTags().stream()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Verifica se un utente può modificare una ricetta
     */
    public boolean canUserModifyRecipe(String recipeId, String userId) {
        Recipe recipe = recipeBook.getRecipe(recipeId);
        return recipe != null && recipe.canBeModified(userId);
    }
    
    /**
     * Crea una preparazione
     */
    public String createPreparation(String ownerId, String name, String description, int executionTime) {
        validatePreparationCreationParameters(ownerId, name, executionTime);
        
        String preparationId = generatePreparationId();
        Preparation preparation = new Preparation(preparationId, name, ownerId, executionTime);
        
        if (description != null && !description.trim().isEmpty()) {
            preparation.setDescription(description);
        }
        
        recipeBook.addPreparation(preparation);
        return preparationId;
    }
    
    /**
     * Ottiene una preparazione per ID
     */
    public Preparation getPreparation(String preparationId) {
        return recipeBook.getPreparation(preparationId);
    }
    
    /**
     * Ottiene tutte le preparazioni pubblicate
     */
    public List<Preparation> getPublishedPreparations() {
        return recipeBook.getPublishedPreparations();
    }
    
    // Metodi di utilità privati
    private Recipe getRecipeOrThrow(String recipeId) {
        Recipe recipe = recipeBook.getRecipe(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Ricetta non trovata: " + recipeId);
        }
        return recipe;
    }
    
    private String generateRecipeId() {
        return "RCP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generatePreparationId() {
        return "PRP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void validateRecipeCreationParameters(String ownerId, String name, int preparationTime) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID del proprietario è obbligatorio");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della ricetta è obbligatorio");
        }
        if (preparationTime <= 0) {
            throw new IllegalArgumentException("Il tempo di preparazione deve essere positivo");
        }
    }
    
    private void validatePreparationCreationParameters(String ownerId, String name, int executionTime) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID del proprietario è obbligatorio");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della preparazione è obbligatorio");
        }
        if (executionTime <= 0) {
            throw new IllegalArgumentException("Il tempo di esecuzione deve essere positivo");
        }
    }
}