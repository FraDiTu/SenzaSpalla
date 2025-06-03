package com.saslab;

import com.saslab.model.Preparation;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Classe che gestisce il ricettario del sistema Cat & Ring
 * CORRETTA: Aggiunge dataUltimoAggiornamento e validazione tag rigorosa
 */
public class RecipeBook {
    
    private static volatile RecipeBook instance;
    private static final Object lock = new Object();
    
    private final Map<String, Recipe> recipes;
    private final Map<String, Preparation> preparations;
    private final Set<String> recipesInUse; 
    private final Set<String> preparationsInUse; 
    private final ReadWriteLock rwLock;
    private String version;
    private LocalDateTime lastUpdate;
    private LocalDateTime dataUltimoAggiornamento; // NUOVO: Campo richiesto
    
    // Cache per relazioni Recipe-Preparation e tag validation
    private final Map<String, Set<String>> recipeToPreparations; 
    private final Map<String, Set<String>> preparationToRecipes; 
    private final Set<String> allowedTags; // Dizionario tag predefiniti con validazione rigorosa
    private final Map<String, TagCategory> tagCategories; // NUOVO: Categorie tag strutturate
    
    // Private constructor per Singleton
    private RecipeBook() {
        this.recipes = new ConcurrentHashMap<>();
        this.preparations = new ConcurrentHashMap<>();
        this.recipesInUse = ConcurrentHashMap.newKeySet();
        this.preparationsInUse = ConcurrentHashMap.newKeySet(); 
        this.rwLock = new ReentrantReadWriteLock();
        this.recipeToPreparations = new ConcurrentHashMap<>(); 
        this.preparationToRecipes = new ConcurrentHashMap<>(); 
        this.allowedTags = ConcurrentHashMap.newKeySet();
        this.tagCategories = new ConcurrentHashMap<>();
        this.version = "1.0.0";
        this.lastUpdate = LocalDateTime.now();
        this.dataUltimoAggiornamento = LocalDateTime.now(); // NUOVO
        
        initializeDefaultTags();
    }
    
    // NUOVO: Inizializza tag predefiniti con categorie strutturate
    private void initializeDefaultTags() {
        // Categoria Dieta
        addTagToCategory("vegetariano", "Dieta", "Tag per piatti vegetariani");
        addTagToCategory("vegano", "Dieta", "Tag per piatti vegani");
        addTagToCategory("senza glutine", "Dieta", "Tag per piatti senza glutine");
        addTagToCategory("senza lattosio", "Dieta", "Tag per piatti senza lattosio");
        addTagToCategory("biologico", "Dieta", "Tag per ingredienti biologici");
        
        // Categoria Portata
        addTagToCategory("antipasto", "Portata", "Piatto di apertura");
        addTagToCategory("primo", "Portata", "Primo piatto");
        addTagToCategory("secondo", "Portata", "Secondo piatto");
        addTagToCategory("contorno", "Portata", "Accompagnamento");
        addTagToCategory("dessert", "Portata", "Dolce");
        
        // Categoria Servizio
        addTagToCategory("finger food", "Servizio", "Cibo da mangiare con le mani");
        addTagToCategory("buffet", "Servizio", "Adatto per buffet");
        addTagToCategory("piatto caldo", "Servizio", "Deve essere servito caldo");
        addTagToCategory("piatto freddo", "Servizio", "Può essere servito freddo");
        
        // Categoria Origine
        addTagToCategory("italiano", "Origine", "Cucina italiana");
        addTagToCategory("mediterraneo", "Origine", "Cucina mediterranea");
        addTagToCategory("orientale", "Origine", "Cucina orientale");
        addTagToCategory("francese", "Origine", "Cucina francese");
        addTagToCategory("internazionale", "Origine", "Cucina internazionale");
    }
    
    private void addTagToCategory(String tagName, String categoryName, String description) {
        String normalizedTag = normalizeTagName(tagName);
        allowedTags.add(normalizedTag);
        tagCategories.put(normalizedTag, new TagCategory(categoryName, description));
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
    
    // NUOVO: Gestione rigorosa dei tag
    public Set<String> getAllowedTags() {
        return new HashSet<>(allowedTags);
    }
    
    public Set<String> getTagsByCategory(String category) {
        return tagCategories.entrySet().stream()
                .filter(entry -> entry.getValue().getCategoryName().equals(category))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
    
    public boolean addAllowedTag(String tag, String category, String description) {
        String normalizedTag = normalizeTagName(tag);
        
        // Validazione rigorosa
        if (!isValidTagName(normalizedTag)) {
            throw new IllegalArgumentException("Nome tag non valido: " + tag);
        }
        
        if (allowedTags.contains(normalizedTag)) {
            return false; // Tag già esistente
        }
        
        allowedTags.add(normalizedTag);
        tagCategories.put(normalizedTag, new TagCategory(category, description));
        updateDataUltimoAggiornamento();
        return true;
    }
    
    public boolean removeTag(String tag) {
        String normalizedTag = normalizeTagName(tag);
        
        // Verifica se il tag è in uso
        boolean tagInUse = recipes.values().stream()
                .anyMatch(recipe -> recipe.hasTag(normalizedTag)) ||
                preparations.values().stream()
                .anyMatch(prep -> prep.hasTag(normalizedTag));
        
        if (tagInUse) {
            throw new IllegalStateException("Impossibile rimuovere un tag in uso: " + tag);
        }
        
        boolean removed = allowedTags.remove(normalizedTag);
        if (removed) {
            tagCategories.remove(normalizedTag);
            updateDataUltimoAggiornamento();
        }
        return removed;
    }
    
    public boolean isTagAllowed(String tag) {
        return tag != null && allowedTags.contains(normalizeTagName(tag));
    }
    
    private String normalizeTagName(String tag) {
        return tag.toLowerCase().trim();
    }
    
    private boolean isValidTagName(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return false;
        }
        
        // Validazione caratteri: solo lettere, numeri, spazi e alcuni caratteri speciali
        return tag.matches("^[a-zA-Z0-9\\s\\-_àáâäèéêëìíîïòóôöùúûü]+$") && 
               tag.length() >= 2 && 
               tag.length() <= 30;
    }
    
    // CORRETTA: Validazione tag rigorosa nei metodi di aggiunta ricette
    public void addRecipe(Recipe recipe) {
        Objects.requireNonNull(recipe, "La ricetta non può essere null");
        rwLock.writeLock().lock();
        try {
            // Valida tag prima di aggiungere
            validateRecipeTags(recipe);
            
            recipes.put(recipe.getId(), recipe);
            updateRecipePreparationRelations(recipe);
            updateVersion();
            updateDataUltimoAggiornamento(); // NUOVO
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // NUOVO: Validazione rigorosa tag di una ricetta
    private void validateRecipeTags(Recipe recipe) {
        for (String tagName : recipe.getTagNames()) {
            String normalizedTag = normalizeTagName(tagName);
            
            if (!isValidTagName(normalizedTag)) {
                throw new IllegalArgumentException("Nome tag non valido: '" + tagName + "'. " +
                    "I tag devono contenere solo lettere, numeri e spazi, lunghezza 2-30 caratteri.");
            }
            
            if (!isTagAllowed(normalizedTag)) {
                throw new IllegalArgumentException("Tag non autorizzato: '" + tagName + "'. " +
                    "Utilizzare solo tag predefiniti o richiedere l'aggiunta di nuovi tag.");
            }
        }
    }
    
    // Metodo per aggiornare relazioni Recipe-Preparation
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
                
                // Aggiorna anche la preparazione
                Preparation prep = preparations.get(prepId);
                if (prep != null) {
                    prep.removeUsageInRecipe(recipeId);
                }
            }
        }
        
        // Aggiungi nuove relazioni
        Set<String> newPreparations = recipe.getPreparazioniRichieste().stream()
                .map(Recipe.PreparationUsage::getPreparationId)
                .collect(Collectors.toSet());
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
    
    // CORRETTA: Ricerca per tag ora con validazione rigorosa
    public List<Recipe> searchRecipesByTag(String tag) {
        rwLock.readLock().lock();
        try {
            String normalizedTag = normalizeTagName(tag);
            
            if (!isTagAllowed(normalizedTag)) {
                return new ArrayList<>(); // Tag non valido, nessun risultato
            }
            
            return recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .filter(recipe -> recipe.hasTag(normalizedTag))
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
            
            // Rimuovi relazioni con preparazioni
            removeRecipePreparationRelations(recipeId);
            
            recipes.remove(recipeId);
            updateVersion();
            updateDataUltimoAggiornamento(); // NUOVO
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // Rimuove relazioni Recipe-Preparation
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
        updateDataUltimoAggiornamento();
    }
    
    public void markRecipeAsNotInUse(String recipeId) {
        recipesInUse.remove(recipeId);
        updateDataUltimoAggiornamento();
    }
    
    public boolean isRecipeInUse(String recipeId) {
        return recipesInUse.contains(recipeId);
    }
    
    // Metodi per preparazioni in uso
    public void markPreparationAsInUse(String preparationId) {
        preparationsInUse.add(preparationId);
        updateDataUltimoAggiornamento();
    }
    
    public void markPreparationAsNotInUse(String preparationId) {
        preparationsInUse.remove(preparationId);
        updateDataUltimoAggiornamento();
    }
    
    // Metodi per le preparazioni
    public void addPreparation(Preparation preparation) {
        Objects.requireNonNull(preparation, "La preparazione non può essere null");
        rwLock.writeLock().lock();
        try {
            // Valida tag della preparazione
            validatePreparationTags(preparation);
            
            preparations.put(preparation.getId(), preparation);
            updateVersion();
            updateDataUltimoAggiornamento(); // NUOVO
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // NUOVO: Validazione rigorosa tag di una preparazione
    private void validatePreparationTags(Preparation preparation) {
        for (String tagName : preparation.getTagNames()) {
            String normalizedTag = normalizeTagName(tagName);
            
            if (!isValidTagName(normalizedTag)) {
                throw new IllegalArgumentException("Nome tag non valido: '" + tagName + "'. " +
                    "I tag devono contenere solo lettere, numeri e spazi, lunghezza 2-30 caratteri.");
            }
            
            if (!isTagAllowed(normalizedTag)) {
                throw new IllegalArgumentException("Tag non autorizzato: '" + tagName + "'. " +
                    "Utilizzare solo tag predefiniti o richiedere l'aggiunta di nuovi tag.");
            }
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
            
            // Verifica se è in uso in ricette
            if (isPreparationInUse(preparationId)) {
                throw new IllegalStateException("Non è possibile eliminare una preparazione utilizzata in ricette");
            }
            
            // Verifica se è in uso in menu
            if (preparationsInUse.contains(preparationId)) {
                throw new IllegalStateException("Non è possibile eliminare una preparazione in uso in menu");
            }
            
            preparations.remove(preparationId);
            preparationToRecipes.remove(preparationId); 
            updateVersion();
            updateDataUltimoAggiornamento(); // NUOVO
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // CORRETTI: Metodi di utilità per tag unificati
    public Set<String> getAllTags() {
        rwLock.readLock().lock();
        try {
            Set<String> allTags = new HashSet<>(allowedTags);
            
            // Aggiungi tag dalle ricette pubblicate (solo se già autorizzati)
            recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .forEach(recipe -> {
                        recipe.getTagNames().forEach(tag -> {
                            String normalizedTag = normalizeTagName(tag);
                            if (isTagAllowed(normalizedTag)) {
                                allTags.add(normalizedTag);
                            }
                        });
                    });
                    
            // Aggiungi tag dalle preparazioni pubblicate (solo se già autorizzati)
            preparations.values().stream()
                    .filter(Preparation::isPublished)
                    .forEach(prep -> {
                        prep.getTagNames().forEach(tag -> {
                            String normalizedTag = normalizeTagName(tag);
                            if (isTagAllowed(normalizedTag)) {
                                allTags.add(normalizedTag);
                            }
                        });
                    });
                    
            return allTags;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    // NUOVO: Metodo per ottenere statistiche sui tag
    public Map<String, Integer> getTagUsageStatistics() {
        rwLock.readLock().lock();
        try {
            Map<String, Integer> tagUsage = new HashMap<>();
            
            // Conta utilizzi nei ricette
            recipes.values().stream()
                    .filter(Recipe::isPublished)
                    .forEach(recipe -> {
                        recipe.getTagNames().forEach(tag -> {
                            String normalizedTag = normalizeTagName(tag);
                            tagUsage.merge(normalizedTag, 1, Integer::sum);
                        });
                    });
            
            // Conta utilizzi nelle preparazioni
            preparations.values().stream()
                    .filter(Preparation::isPublished)
                    .forEach(prep -> {
                        prep.getTagNames().forEach(tag -> {
                            String normalizedTag = normalizeTagName(tag);
                            tagUsage.merge(normalizedTag, 1, Integer::sum);
                        });
                    });
            
            return tagUsage;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    // Metodi per relazioni Recipe-Preparation
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
    
    // NUOVO: Aggiorna data ultimo aggiornamento
    private void updateDataUltimoAggiornamento() {
        this.dataUltimoAggiornamento = LocalDateTime.now();
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
    
    // NUOVO: Getter per data ultimo aggiornamento
    public LocalDateTime getDataUltimoAggiornamento() {
        rwLock.readLock().lock();
        try {
            return dataUltimoAggiornamento;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    // Metodo migliorato per verificare uso preparazioni
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
            return String.format("RecipeBook{version='%s', recipes=%d, preparations=%d, allowedTags=%d, lastUpdate=%s, dataUltimoAggiornamento=%s}", 
                               version, recipes.size(), preparations.size(), allowedTags.size(), lastUpdate, dataUltimoAggiornamento);
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    // NUOVO: Classe interna per categorie tag
    public static class TagCategory {
        private final String categoryName;
        private final String description;
        
        public TagCategory(String categoryName, String description) {
            this.categoryName = categoryName;
            this.description = description;
        }
        
        public String getCategoryName() { return categoryName; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return String.format("TagCategory{name='%s', description='%s'}", categoryName, description);
        }
    }
}