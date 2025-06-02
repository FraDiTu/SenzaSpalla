package com.saslab;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import com.saslab.model.Preparation;

/**
 * Classe che gestisce il ricettario del sistema Cat & Ring
 * Implementa pattern Singleton thread-safe per garantire un'unica istanza del ricettario
 * Implementa pattern Information Expert per la gestione delle ricette
 */
public class RecipeBook {
    
    // Volatile per garantire visibilità tra thread
    private static volatile RecipeBook instance;
    private static final Object lock = new Object();
    
    private final Map<String, Recipe> recipes;
    private final Map<String, Preparation> preparations;
    private final Set<String> recipesInUse; // Track ricette in uso
    private final ReadWriteLock rwLock;
    private String version;
    private LocalDateTime lastUpdate;
    
    // Private constructor per Singleton
    private RecipeBook() {
        this.recipes = new ConcurrentHashMap<>();
        this.preparations = new ConcurrentHashMap<>();
        this.recipesInUse = ConcurrentHashMap.newKeySet();
        this.rwLock = new ReentrantReadWriteLock();
        this.version = "1.0.0";
        this.lastUpdate = LocalDateTime.now();
    }
    
    // Singleton getInstance thread-safe con double-checked locking
    public static RecipeBook getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new RecipeBook();
                }
            }
        }
        return instance;
    }
    
    // Metodi per le ricette con lock appropriati
    public void addRecipe(Recipe recipe) {
        Objects.requireNonNull(recipe, "La ricetta non può essere null");
        rwLock.writeLock().lock();
        try {
            recipes.put(recipe.getId(), recipe);
            updateVersion();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    public Recipe getRecipe(String recipeId) {
        rwLock.readLock().lock();
        try {
            return recipes.get(recipeId);
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Recipe> getAllRecipes() {
        rwLock.readLock().lock();
        try {
            return new ArrayList<>(recipes.values());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Recipe> getPublishedRecipes() {
        rwLock.readLock().lock();
        try {
            return recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Recipe> getRecipesByOwner(String ownerId) {
        rwLock.readLock().lock();
        try {
            return recipes.values().stream()
                    .filter(recipe -> recipe.getOwnerId().equals(ownerId))
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Recipe> searchRecipesByTag(String tag) {
        rwLock.readLock().lock();
        try {
            return recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .filter(recipe -> recipe.getTags().contains(tag))
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Recipe> searchRecipesByName(String namePattern) {
        rwLock.readLock().lock();
        try {
            String pattern = namePattern.toLowerCase();
            return recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .filter(recipe -> recipe.getName().toLowerCase().contains(pattern))
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public boolean removeRecipe(String recipeId, String userId) {
        rwLock.writeLock().lock();
        try {
            Recipe recipe = recipes.get(recipeId);
            if (recipe == null) {
                return false;
            }
            
            // Solo il proprietario può eliminare la ricetta
            if (!recipe.getOwnerId().equals(userId)) {
                throw new SecurityException("Solo il proprietario può eliminare la ricetta");
            }
            
            // Verifica se è in uso
            if (recipesInUse.contains(recipeId)) {
                throw new IllegalStateException("Non è possibile eliminare una ricetta in uso");
            }
            
            recipes.remove(recipeId);
            updateVersion();
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // Metodi per gestire l'uso delle ricette
    public void markRecipeAsInUse(String recipeId) {
        recipesInUse.add(recipeId);
    }
    
    public void markRecipeAsNotInUse(String recipeId) {
        recipesInUse.remove(recipeId);
    }
    
    public boolean isRecipeInUse(String recipeId) {
        return recipesInUse.contains(recipeId);
    }
    
    // Metodi per le preparazioni
    public void addPreparation(Preparation preparation) {
        Objects.requireNonNull(preparation, "La preparazione non può essere null");
        rwLock.writeLock().lock();
        try {
            preparations.put(preparation.getId(), preparation);
            updateVersion();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    public Preparation getPreparation(String preparationId) {
        rwLock.readLock().lock();
        try {
            return preparations.get(preparationId);
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Preparation> getAllPreparations() {
        rwLock.readLock().lock();
        try {
            return new ArrayList<>(preparations.values());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Preparation> getPublishedPreparations() {
        rwLock.readLock().lock();
        try {
            return preparations.values().stream()
                    .filter(Preparation::isPublished)
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Preparation> getPreparationsByOwner(String ownerId) {
        rwLock.readLock().lock();
        try {
            return preparations.values().stream()
                    .filter(prep -> prep.getOwnerId().equals(ownerId))
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public boolean removePreparation(String preparationId, String userId) {
        rwLock.writeLock().lock();
        try {
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
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // Metodi di utilità
    public Set<String> getAllTags() {
        rwLock.readLock().lock();
        try {
            Set<String> allTags = new HashSet<>();
            
            recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .forEach(recipe -> allTags.addAll(recipe.getTags()));
                    
            preparations.values().stream()
                    .filter(Preparation::isPublished)
                    .forEach(prep -> allTags.addAll(prep.getTags()));
                    
            return allTags;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public int getTotalRecipeCount() {
        return recipes.size();
    }
    
    public int getPublishedRecipeCount() {
        rwLock.readLock().lock();
        try {
            return (int) recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .count();
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public int getTotalPreparationCount() {
        return preparations.size();
    }
    
    private void updateVersion() {
        this.lastUpdate = LocalDateTime.now();
        // Incrementa la versione in modo thread-safe
        String[] parts = version.split("\\.");
        int patch = Integer.parseInt(parts[2]) + 1;
        this.version = parts[0] + "." + parts[1] + "." + patch;
    }
    
    // Getters thread-safe
    public String getVersion() {
        rwLock.readLock().lock();
        try {
            return version;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public LocalDateTime getLastUpdate() {
        rwLock.readLock().lock();
        try {
            return lastUpdate;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public boolean isPreparationInUse(String preparationId) {
        rwLock.readLock().lock();
        try {
            // Verifica se la preparazione è utilizzata in qualche ricetta
            return recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .anyMatch(recipe -> recipe.getIngredients().stream()
                            .anyMatch(ing -> ing.getName().equals(preparationId)));
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    @Override
    public String toString() {
        rwLock.readLock().lock();
        try {
            return String.format("RecipeBook{version='%s', recipes=%d, preparations=%d, lastUpdate=%s}", 
                               version, recipes.size(), preparations.size(), lastUpdate);
        } finally {
            rwLock.readLock().unlock();
        }
    }
}