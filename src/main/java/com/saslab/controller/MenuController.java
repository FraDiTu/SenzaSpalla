package com.saslab.controller;

import com.saslab.model.Menu;
import com.saslab.Recipe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller per la gestione dei menù
 * Implementa pattern Controller (GRASP)
 * Mantiene una separazione delle responsabilità (Separation of Concerns)
 */
public class MenuController {
    
    private final Map<String, Menu> menus;
    private final RecipeController recipeController;
    
    public MenuController(RecipeController recipeController) {
        this.menus = new ConcurrentHashMap<>();
        this.recipeController = Objects.requireNonNull(recipeController, "RecipeController non può essere null");
    }
    
    /**
     * Crea un nuovo menù
     * Implementa contratto CO1: defineMenuSections non è ancora chiamato qui
     */
    public String createMenu(String chefId, String name) {
        validateMenuCreationParameters(chefId, name);
        
        String menuId = generateMenuId();
        Menu menu = new Menu(menuId, name, chefId);
        
        menus.put(menuId, menu);
        return menuId;
    }
    
    /**
     * Definisce le sezioni del menù
     * Implementa contratto CO1: defineMenuSections
     */
    public void defineMenuSections(String menuId, List<String> sectionList) {
        Menu menu = getMenuOrThrow(menuId);
        
        // Pre-condizioni del contratto CO1
        if (menu.getState() != Menu.MenuState.DRAFT) {
            throw new IllegalStateException("Il menù deve essere aperto in modalità bozza");
        }
        
        if (sectionList == null || sectionList.isEmpty()) {
            throw new IllegalArgumentException("L'elenco delle sezioni non può essere vuoto");
        }
        
        // Verifica nomi unici
        Set<String> uniqueNames = new HashSet<>(sectionList);
        if (uniqueNames.size() != sectionList.size()) {
            throw new IllegalArgumentException("Ogni nome deve essere unico");
        }
        
        menu.defineMenuSections(sectionList);
        
        // Post-condizioni: le nuove sezioni sono state aggiunte al menù e ordinate come indicato
    }
    
    /**
     * Aggiunge una ricetta ad una sezione del menù
     * Implementa contratto CO2: addRecipeToSection
     */
    public void addRecipeToSection(String menuId, String recipeId, String sectionName) {
        Menu menu = getMenuOrThrow(menuId);
        
        // Pre-condizioni del contratto CO2
        if (menu.getState() != Menu.MenuState.DRAFT) {
            throw new IllegalStateException("Il menù deve essere aperto in bozza");
        }
        
        if (menu.findSectionByName(sectionName) == null) {
            throw new IllegalArgumentException("Il menù deve contenere già la sezione di destinazione");
        }
        
        Recipe recipe = recipeController.getRecipe(recipeId);
        if (recipe == null || !recipe.isPublished()) {
            throw new IllegalArgumentException("La ricetta da aggiungere deve esistere nel sistema ed essere pubblicata");
        }
        
        menu.addRecipeToSection(recipeId, sectionName);
        
        // Post-condizioni: la ricetta è stata inserita nella sezione scelta del menù
    }
    
    /**
     * Sposta ricette tra sezioni
     * Implementa contratto CO3: moveRecipesBetweenSection
     */
    public void moveRecipesBetweenSection(String menuId, String recipeId, String sourceSection, String destinationSection) {
        Menu menu = getMenuOrThrow(menuId);
        
        // Pre-condizioni del contratto CO3
        if (menu.getState() != Menu.MenuState.DRAFT) {
            throw new IllegalStateException("Il menù deve essere in bozza");
        }
        
        if (menu.findSectionByName(sourceSection) == null) {
            throw new IllegalArgumentException("Il menù deve contenere la sezione di partenza");
        }
        
        if (menu.findSectionByName(destinationSection) == null) {
            throw new IllegalArgumentException("Il menù deve contenere la sezione di destinazione");
        }
        
        if (menu.findMenuItemByRecipeId(recipeId) == null) {
            throw new IllegalArgumentException("La ricetta deve essere già inclusa nella sezione di partenza");
        }
        
        menu.moveRecipesBetweenSection(recipeId, sourceSection, destinationSection);
        
        // Post-condizioni: la ricetta è stata spostata dalla sezione originale a quella di destinazione
    }
    
    /**
     * Rimuove ricette dal menù
     */
    public void removeRecipeFromSection(String menuId, String recipeId, String sectionName) {
        Menu menu = getMenuOrThrow(menuId);
        
        if (menu.getState() != Menu.MenuState.DRAFT) {
            throw new IllegalStateException("Il menù deve essere in bozza per rimuovere ricette");
        }
        
        menu.removeRecipeFromSection(recipeId, sectionName);
    }
    
    /**
     * Conferma la creazione del menù e genera PDF
     * Implementa contratto CO5: confirmMenuCreationAndGeneratePDF
     */
    public String confirmMenuCreationAndGeneratePDF(String menuId) {
        Menu menu = getMenuOrThrow(menuId);
        
        // Pre-condizioni del contratto CO5
        if (menu.getState() != Menu.MenuState.DRAFT) {
            throw new IllegalStateException("Il menù deve essere completo e pronto per la generazione del PDF");
        }
        
        if (menu.getTotalMenuItems() == 0) {
            throw new IllegalArgumentException("Il menù deve contenere almeno una voce");
        }
        
        // Pubblica il menù
        menu.publish();
        
        // Simula generazione PDF
        String pdfPath = generateMenuPDF(menu);
        
        // Post-condizioni: il PDF del menù è stato creato con successo
        return pdfPath;
    }
    
    /**
     * Ottiene un menù esistente per modifica
     */
    public Menu getMenuForEditing(String menuId, String chefId) {
        Menu menu = getMenuOrThrow(menuId);
        
        if (!menu.getChefId().equals(chefId)) {
            throw new SecurityException("Solo il chef creatore può modificare il menù");
        }
        
        if (!menu.canBeModified()) {
            throw new IllegalStateException("Il menù non può essere modificato nel suo stato attuale");
        }
        
        return menu;
    }
    
    /**
     * Crea una copia di un menù esistente
     */
    public String copyMenu(String originalMenuId, String newChefId) {
        Menu originalMenu = getMenuOrThrow(originalMenuId);
        
        if (originalMenu.getState() == Menu.MenuState.DRAFT && !originalMenu.getChefId().equals(newChefId)) {
            throw new IllegalStateException("Non è possibile copiare menù in bozza di altri chef");
        }
        
        String newMenuId = generateMenuId();
        Menu copiedMenu = originalMenu.createCopy(newMenuId, newChefId);
        
        menus.put(newMenuId, copiedMenu);
        return newMenuId;
    }
    
    /**
     * Elimina un menù
     */
    public boolean deleteMenu(String menuId, String chefId) {
        Menu menu = menus.get(menuId);
        if (menu == null) {
            return false;
        }
        
        if (!menu.getChefId().equals(chefId)) {
            throw new SecurityException("Solo il chef creatore può eliminare il menù");
        }
        
        if (!menu.canBeDeleted()) {
            throw new IllegalStateException("Il menù non può essere eliminato nel suo stato attuale");
        }
        
        menus.remove(menuId);
        return true;
    }
    
    /**
     * Ottiene un menù per ID
     */
    public Menu getMenu(String menuId) {
        return menus.get(menuId);
    }
    
    /**
     * Ottiene tutti i menù di uno chef
     */
    public List<Menu> getMenusByChef(String chefId) {
        return menus.values().stream()
                .filter(menu -> menu.getChefId().equals(chefId))
                .sorted((m1, m2) -> m2.getLastModified().compareTo(m1.getLastModified()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Ottiene tutti i menù pubblicati
     */
    public List<Menu> getPublishedMenus() {
        return menus.values().stream()
                .filter(menu -> menu.getState() == Menu.MenuState.PUBLISHED)
                .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Cerca menù per nome
     */
    public List<Menu> searchMenusByName(String namePattern) {
        String pattern = namePattern.toLowerCase();
        return menus.values().stream()
                .filter(menu -> menu.getState() == Menu.MenuState.PUBLISHED)
                .filter(menu -> menu.getName().toLowerCase().contains(pattern))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Verifica se un menù può essere utilizzato per un evento
     */
    public boolean canMenuBeUsedForEvent(String menuId) {
        Menu menu = menus.get(menuId);
        return menu != null && menu.getState() == Menu.MenuState.PUBLISHED;
    }
    
    /**
     * Marca un menù come in uso
     */
    public void markMenuAsInUse(String menuId) {
        Menu menu = getMenuOrThrow(menuId);
        menu.markAsInUse();
    }
    
    /**
     * Ottiene statistiche sui menù
     */
    public Map<String, Integer> getMenuStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        long draftCount = menus.values().stream().filter(m -> m.getState() == Menu.MenuState.DRAFT).count();
        long publishedCount = menus.values().stream().filter(m -> m.getState() == Menu.MenuState.PUBLISHED).count();
        long inUseCount = menus.values().stream().filter(m -> m.getState() == Menu.MenuState.IN_USE).count();
        long archivedCount = menus.values().stream().filter(m -> m.getState() == Menu.MenuState.ARCHIVED).count();
        
        stats.put("total", menus.size());
        stats.put("draft", (int) draftCount);
        stats.put("published", (int) publishedCount);
        stats.put("inUse", (int) inUseCount);
        stats.put("archived", (int) archivedCount);
        
        return stats;
    }
    
    // Metodi di utilità privati
    private Menu getMenuOrThrow(String menuId) {
        Menu menu = menus.get(menuId);
        if (menu == null) {
            throw new IllegalArgumentException("Menù non trovato: " + menuId);
        }
        return menu;
    }
    
    private String generateMenuId() {
        return "MNU_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void validateMenuCreationParameters(String chefId, String name) {
        if (chefId == null || chefId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID dello chef è obbligatorio");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del menù è obbligatorio");
        }
    }
    
    private String generateMenuPDF(Menu menu) {
        // Simula la generazione di un PDF
        // In una implementazione reale, questo metodo genererebbe effettivamente il PDF
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "/pdf/menu_" + menu.getId() + "_" + timestamp + ".pdf";
    }
    
    /**
     * Metodo per ottenere il numero totale di menù
     */
    public int getTotalMenuCount() {
        return menus.size();
    }
    
    /**
     * Archivia un menù
     */
    public void archiveMenu(String menuId, String chefId) {
        Menu menu = getMenuOrThrow(menuId);
        
        if (!menu.getChefId().equals(chefId)) {
            throw new SecurityException("Solo il chef creatore può archiviare il menù");
        }
        
        menu.archive();
    }
}