package com.saslab;

import com.saslab.model.Ingredient; // CORRETTO: Import dalla classe separata
import com.saslab.model.Preparation;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Classe che gestisce il ricettario del sistema Cat & Ring
 * CORRETTA: Risolve import errato e migliora thread safety e relazioni Recipe-Preparation
 */
public class RecipeBook {
    
    // Volatile per garantire visibilità tra thread
    private static volatile RecipeBook instance;
    private static final Object lock = new Object();
    
    private final Map<String, Recipe> recipes;
    private final Map<String, Preparation> preparations;
    private final Set<String> recipesInUse; // Track ricette in uso
    private final Set<String> preparationsInUse; // NUOVO: Track preparazioni in uso
    private final ReadWriteLock rwLock;
    private String version;
    private LocalDateTime lastUpdate;
    
    // NUOVO: Cache per relazioni Recipe-Preparation
    private final Map<String, Set<String>> recipeToPreparations; // recipeId -> Set<preparationId>
    private final Map<String, Set<String>> preparationToRecipes; // preparationId -> Set<recipeId>
    
    // Private constructor per Singleton
    private RecipeBook() {
        this.recipes = new ConcurrentHashMap<>();
        this.preparations = new ConcurrentHashMap<>();
        this.recipesInUse = ConcurrentHashMap.newKeySet();
        this.preparationsInUse = ConcurrentHashMap.newKeySet(); // NUOVO
        this.rwLock = new ReentrantReadWriteLock();
        this.recipeToPreparations = new ConcurrentHashMap<>(); // NUOVO
        this.preparationToRecipes = new ConcurrentHashMap<>(); // NUOVO
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
            
            // NUOVO: Aggiorna relazioni con preparazioni
            updateRecipePreparationRelations(recipe);
            
            updateVersion();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // NUOVO: Metodo per aggiornare relazioni Recipe-Preparation
    private void updateRecipePreparationRelations(Recipe recipe) {
        String recipeId = recipe.getId();
        
        // Rimuovi vecchie relazioni
        Set<String> oldPreparations = recipeToPreparations.get(recipeId);
        if (oldPreparations != null) {
            for (String prepId : oldPreparations) {
                Set<String> recipesUsingPrep = preparationToRecipes.get(prepId);
                if (recipesUsingPrep != null) {
                    recipesUsingPrep.remove(recipeId);
                    if (recipesUsingPrep.isEmpty()) {
                        preparationToRecipes.remove(prepId);
                    }
                }
            }
        }
        
        // Aggiungi nuove relazioni
        Set<String> newPreparations = new HashSet<>(recipe.getUsedPreparationIds());
        recipeToPreparations.put(recipeId, newPreparations);
        
        for (String prepId : newPreparations) {
            preparationToRecipes.computeIfAbsent(prepId, k -> ConcurrentHashMap.newKeySet()).add(recipeId);
            
            // Aggiorna anche la preparazione se esiste
            Preparation prep = preparations.get(prepId);
            if (prep != null) {
                prep.addUsageInRecipe(recipeId);
            }
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
            
            // CORRETTO: Rimuovi relazioni con preparazioni
            removeRecipePreparationRelations(recipeId);
            
            recipes.remove(recipeId);
            updateVersion();
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // NUOVO: Rimuove relazioni Recipe-Preparation
    private void removeRecipePreparationRelations(String recipeId) {
        Set<String> usedPreparations = recipeToPreparations.remove(recipeId);
        if (usedPreparations != null) {
            for (String prepId : usedPreparations) {
                Set<String> recipesUsingPrep = preparationToRecipes.get(prepId);
                if (recipesUsingPrep != null) {
                    recipesUsingPrep.remove(recipeId);
                    if (recipesUsingPrep.isEmpty()) {
                        preparationToRecipes.remove(prepId);
                    }
                }
                
                // Aggiorna anche la preparazione se esiste
                Preparation prep = preparations.get(prepId);
                if (prep != null) {
                    prep.removeUsageInRecipe(recipeId);
                }
            }
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
    
    // NUOVO: Metodi per preparazioni in uso
    public void markPreparationAsInUse(String preparationId) {
        preparationsInUse.add(preparationId);
    }
    
    public void markPreparationAsNotInUse(String preparationId) {
        preparationsInUse.remove(preparationId);
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
            
            // CORRETTO: Verifica se è in uso in ricette
            if (isPreparationInUse(preparationId)) {
                throw new IllegalStateException("Non è possibile eliminare una preparazione utilizzata in ricette");
            }
            
            // Verifica se è in uso in menu
            if (preparationsInUse.contains(preparationId)) {
                throw new IllegalStateException("Non è possibile eliminare una preparazione in uso in menu");
            }
            
            preparations.remove(preparationId);
            preparationToRecipes.remove(preparationId); // NUOVO
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
    
    // NUOVO: Metodi per relazioni Recipe-Preparation
    public List<Recipe> getRecipesUsingPreparation(String preparationId) {
        rwLock.readLock().lock();
        try {
            Set<String> recipeIds = preparationToRecipes.get(preparationId);
            if (recipeIds == null) {
                return new ArrayList<>();
            }
            
            return recipeIds.stream()
                    .map(recipes::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public List<Preparation> getPreparationsUsedInRecipe(String recipeId) {
        rwLock.readLock().lock();
        try {
            Set<String> preparationIds = recipeToPreparations.get(recipeId);
            if (preparationIds == null) {
                return new ArrayList<>();
            }
            
            return preparationIds.stream()
                    .map(preparations::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
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
    
    // CORRETTO: Metodo migliorato per verificare uso preparazioni
    public boolean isPreparationInUse(String preparationId) {
        rwLock.readLock().lock();
        try {
            // Controlla se è utilizzata in ricette pubblicate
            Set<String> usingRecipes = preparationToRecipes.get(preparationId);
            if (usingRecipes != null && !usingRecipes.isEmpty()) {
                // Verifica se almeno una ricetta che la usa è pubblicata
                return usingRecipes.stream()
                        .map(recipes::get)
                        .filter(Objects::nonNull)
                        .anyMatch(Recipe::isPublished);
            }
            
            // Controlla se è in uso in menu
            return preparationsInUse.contains(preparationId);
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