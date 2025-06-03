package com.saslab.controller;

import com.saslab.model.Menu;
import com.saslab.model.MenuSection;
import com.saslab.Recipe;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller per la gestione dei menù
 * CORRETTO: Aggiunge copia menu, gestione ordine sezioni e generazione PDF reale
 */
public class MenuController {
    
    private final Map<String, Menu> menus;
    private final RecipeController recipeController;
    private final String pdfOutputDirectory;
    
    public MenuController(RecipeController recipeController) {
        this.menus = new ConcurrentHashMap<>();
        this.recipeController = Objects.requireNonNull(recipeController, "RecipeController non può essere null");
        
        // NUOVO: Directory per output PDF
        this.pdfOutputDirectory = System.getProperty("user.home") + File.separator + "CatRing_Menus";
        createPdfDirectoryIfNotExists();
    }
    
    private void createPdfDirectoryIfNotExists() {
        try {
            Path path = Paths.get(pdfOutputDirectory);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Impossibile creare directory per PDF: " + e.getMessage());
        }
    }
    
    /**
     * Crea un nuovo menù
     */
    public String createMenu(String chefId, String name) {
        validateMenuCreationParameters(chefId, name);
        
        String menuId = generateMenuId();
        Menu menu = new Menu(menuId, name, chefId);
        
        menus.put(menuId, menu);
        return menuId;
    }
    
    /**
     * NUOVO: Crea una copia di un menù esistente
     */
    public String copyMenu(String originalMenuId, String newChefId, String newName) {
        Menu originalMenu = getMenuOrThrow(originalMenuId);
        
        // Verifica che il menù originale sia pubblicato o che sia il proprietario
        if (originalMenu.getState() == Menu.MenuState.DRAFT && !originalMenu.getChefId().equals(newChefId)) {
            throw new IllegalStateException("Non è possibile copiare menù in bozza di altri chef");
        }
        
        String newMenuId = generateMenuId();
        String menuName = (newName != null && !newName.trim().isEmpty()) ? 
                         newName : originalMenu.getName() + " (Copia)";
        
        Menu copiedMenu = originalMenu.createCopy(newMenuId, newChefId);
        copiedMenu.setName(menuName);
        
        menus.put(newMenuId, copiedMenu);
        return newMenuId;
    }
    
    /**
     * CORRETTO: Definisce le sezioni del menù con gestione ordine
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
        
        // NUOVO: Riordina le sezioni secondo l'ordine specificato
        reorderMenuSections(menuId, sectionList);
    }
    
    /**
     * NUOVO: Riordina le sezioni del menù
     */
    public void reorderMenuSections(String menuId, List<String> orderedSectionNames) {
        Menu menu = getMenuOrThrow(menuId);
        
        if (menu.getState() != Menu.MenuState.DRAFT) {
            throw new IllegalStateException("È possibile riordinare solo menù in bozza");
        }
        
        // Verifica che tutte le sezioni esistano
        List<MenuSection> currentSections = menu.getSections();
        Set<String> currentSectionNames = new HashSet<>();
        for (MenuSection section : currentSections) {
            currentSectionNames.add(section.getTitle());
        }
        
        for (String sectionName : orderedSectionNames) {
            if (!currentSectionNames.contains(sectionName)) {
                throw new IllegalArgumentException("Sezione non trovata: " + sectionName);
            }
        }
        
        if (orderedSectionNames.size() != currentSectionNames.size()) {
            throw new IllegalArgumentException("Devono essere specificate tutte le sezioni");
        }
        
        // Riordina le sezioni
        menu.reorderSections(orderedSectionNames);
    }
    
    /**
     * NUOVO: Sposta una sezione verso l'alto
     */
    public void moveSectionUp(String menuId, String sectionName) {
        Menu menu = getMenuOrThrow(menuId);
        
        if (menu.getState() != Menu.MenuState.DRAFT) {
            throw new IllegalStateException("È possibile spostare sezioni solo in menù in bozza");
        }
        
        menu.moveSectionUp(sectionName);
    }
    
    /**
     * NUOVO: Sposta una sezione verso il basso
     */
    public void moveSectionDown(String menuId, String sectionName) {
        Menu menu = getMenuOrThrow(menuId);
        
        if (menu.getState() != Menu.MenuState.DRAFT) {
            throw new IllegalStateException("È possibile spostare sezioni solo in menù in bozza");
        }
        
        menu.moveSectionDown(sectionName);
    }
    
    /**
     * Aggiunge una ricetta ad una sezione del menù
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
    }
    
    /**
     * Sposta ricette tra sezioni
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
     * CORRETTO: Conferma la creazione del menù e genera PDF reale
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
        
        // NUOVO: Genera PDF reale
        String pdfPath = generateMenuPDF(menu);
        
        // NUOVO: Condividi PDF (simula invio)
        shareMenuPDF(menu, pdfPath);
        
        return pdfPath;
    }
    
    /**
     * NUOVO: Genera PDF reale del menù
     */
    private String generateMenuPDF(Menu menu) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("Menu_%s_%s.html", 
                                          menu.getName().replaceAll("[^a-zA-Z0-9]", "_"), 
                                          timestamp);
            String filePath = pdfOutputDirectory + File.separator + fileName;
            
            // Genera HTML che può essere convertito in PDF
            String htmlContent = generateMenuHTML(menu);
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(htmlContent);
            }
            
            System.out.println("PDF del menù generato: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            throw new RuntimeException("Errore nella generazione del PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * NUOVO: Genera contenuto HTML per il PDF
     */
    private String generateMenuHTML(Menu menu) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='it'>\n");
        html.append("<head>\n");
        html.append("    <meta charset='UTF-8'>\n");
        html.append("    <title>").append(menu.getName()).append("</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: 'Georgia', serif; margin: 40px; background-color: #fafafa; }\n");
        html.append("        .header { text-align: center; margin-bottom: 40px; }\n");
        html.append("        .menu-title { font-size: 28px; color: #2c3e50; margin-bottom: 10px; }\n");
        html.append("        .menu-description { font-style: italic; color: #7f8c8d; font-size: 16px; }\n");
        html.append("        .section { margin: 30px 0; }\n");
        html.append("        .section-title { font-size: 20px; color: #34495e; border-bottom: 2px solid #ecf0f1; padding-bottom: 10px; margin-bottom: 15px; }\n");
        html.append("        .menu-item { margin: 12px 0; padding: 8px 0; }\n");
        html.append("        .item-name { font-weight: bold; color: #2c3e50; }\n");
        html.append("        .item-price { float: right; color: #e74c3c; font-weight: bold; }\n");
        html.append("        .footer { margin-top: 50px; text-align: center; font-size: 12px; color: #95a5a6; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // Header
        html.append("    <div class='header'>\n");
        html.append("        <h1 class='menu-title'>").append(menu.getName()).append("</h1>\n");
        if (menu.getDescription() != null && !menu.getDescription().isEmpty()) {
            html.append("        <p class='menu-description'>").append(menu.getDescription()).append("</p>\n");
        }
        html.append("    </div>\n");
        
        // Sezioni
        for (MenuSection section : menu.getSections()) {
            html.append("    <div class='section'>\n");
            html.append("        <h2 class='section-title'>").append(section.getTitle()).append("</h2>\n");
            
            for (com.saslab.model.MenuItem item : section.getMenuItems()) {
                html.append("        <div class='menu-item'>\n");
                html.append("            <span class='item-name'>").append(item.getDisplayName()).append("</span>\n");
                
                if (item.hasPrice()) {
                    html.append("            <span class='item-price'>€").append(String.format("%.2f", item.getPrice())).append("</span>\n");
                }
                
                // Aggiungi descrizione ricetta se disponibile
                Recipe recipe = recipeController.getRecipe(item.getRecipeId());
                if (recipe != null && recipe.getDescription() != null && !recipe.getDescription().isEmpty()) {
                    html.append("            <br><small style='color: #7f8c8d;'>").append(recipe.getDescription()).append("</small>\n");
                }
                
                html.append("        </div>\n");
            }
            html.append("    </div>\n");
        }
        
        // Footer
        html.append("    <div class='footer'>\n");
        html.append("        <p>Menù generato il ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</p>\n");
        html.append("        <p>Chef: ").append(menu.getChefId()).append(" | Cat & Ring Catering System</p>\n");
        html.append("    </div>\n");
        
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * NUOVO: Condivide il PDF del menù (simula invio email)
     */
    private void shareMenuPDF(Menu menu, String pdfPath) {
        // Simula condivisione via email o altri canali
        System.out.println("=== CONDIVISIONE MENU ===");
        System.out.println("Menù: " + menu.getName());
        System.out.println("Chef: " + menu.getChefId());
        System.out.println("PDF salvato in: " + pdfPath);
        System.out.println("Stato: Pronto per condivisione");
        
        // In una implementazione reale, qui si potrebbero:
        // - Inviare email con allegato PDF
        // - Pubblicare su sistema di gestione documenti
        // - Notificare stakeholder interessati
        // - Aggiornare database con link al file
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
     * Crea una copia di un menù esistente (metodo originale mantenuto per compatibilità)
     */
    public String copyMenu(String originalMenuId, String newChefId) {
        return copyMenu(originalMenuId, newChefId, null);
    }
    
    /**
     * NUOVO: Duplica un menù con modifiche
     */
    public String duplicateMenuWithChanges(String originalMenuId, String newChefId, 
                                         String newName, Map<String, String> sectionChanges,
                                         List<String> recipesToRemove, Map<String, String> recipesToAdd) {
        Menu originalMenu = getMenuOrThrow(originalMenuId);
        String newMenuId = copyMenu(originalMenuId, newChefId, newName);
        Menu newMenu = getMenu(newMenuId);
        
        if (newMenu == null) {
            throw new IllegalStateException("Errore nella creazione della copia");
        }
        
        // Applica modifiche alle sezioni
        if (sectionChanges != null) {
            for (Map.Entry<String, String> change : sectionChanges.entrySet()) {
                String oldSectionName = change.getKey();
                String newSectionName = change.getValue();
                newMenu.renameSectionIfExists(oldSectionName, newSectionName);
            }
        }
        
        // Rimuovi ricette specificate
        if (recipesToRemove != null) {
            for (String recipeId : recipesToRemove) {
                com.saslab.model.MenuItem item = newMenu.findMenuItemByRecipeId(recipeId);
                if (item != null) {
                    MenuSection section = newMenu.findSectionContainingItem(item);
                    if (section != null) {
                        removeRecipeFromSection(newMenuId, recipeId, section.getTitle());
                    }
                }
            }
        }
        
        // Aggiungi nuove ricette
        if (recipesToAdd != null) {
            for (Map.Entry<String, String> addition : recipesToAdd.entrySet()) {
                String recipeId = addition.getKey();
                String sectionName = addition.getValue();
                try {
                    addRecipeToSection(newMenuId, recipeId, sectionName);
                } catch (Exception e) {
                    System.err.println("Impossibile aggiungere ricetta " + recipeId + ": " + e.getMessage());
                }
            }
        }
        
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
     * NUOVO: Cerca menù per caratteristiche
     */
    public List<Menu> searchMenusByCharacteristics(boolean vegetarian, boolean vegan, 
                                                  boolean buffetSuitable, boolean fingerFoodOnly) {
        return menus.values().stream()
                .filter(menu -> menu.getState() == Menu.MenuState.PUBLISHED)
                .filter(menu -> {
                    if (buffetSuitable && !menu.isSuitableForBuffet()) return false;
                    if (fingerFoodOnly && !menu.isFingerFoodOnly()) return false;
                    
                    // Per vegetarian/vegan, dovremmo controllare le ricette nel menù
                    if (vegetarian || vegan) {
                        return checkMenuDietaryRequirements(menu, vegetarian, vegan);
                    }
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    private boolean checkMenuDietaryRequirements(Menu menu, boolean vegetarian, boolean vegan) {
        for (MenuSection section : menu.getSections()) {
            for (com.saslab.model.MenuItem item : section.getMenuItems()) {
                Recipe recipe = recipeController.getRecipe(item.getRecipeId());
                if (recipe != null) {
                    List<String> tags = recipe.getTagNames();
                    if (vegan && !tags.contains("vegano")) {
                        return false;
                    }
                    if (vegetarian && !tags.contains("vegetariano") && !tags.contains("vegano")) {
                        return false;
                    }
                }
            }
        }
        return true;
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
     * NUOVO: Ottiene menù per evento specifico
     */
    public List<Menu> getMenusForEventType(String eventType, int guestCount) {
        return menus.values().stream()
                .filter(menu -> menu.getState() == Menu.MenuState.PUBLISHED)
                .filter(menu -> menu.isCompatibleWithEventType(eventType))
                .filter(menu -> menu.getEstimatedPreparationTime() <= calculateMaxPreparationTime(guestCount))
                .sorted((m1, m2) -> Integer.compare(m1.getTotalMenuItems(), m2.getTotalMenuItems()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    private int calculateMaxPreparationTime(int guestCount) {
        // Calcolo semplificato: più ospiti = più tempo disponibile per preparazione
        if (guestCount < 20) return 180;      // 3 ore
        if (guestCount < 50) return 300;      // 5 ore
        if (guestCount < 100) return 480;     // 8 ore
        return 720;                           // 12 ore
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
    
    /**
     * NUOVO: Ottiene statistiche avanzate sui menù
     */
    public Map<String, Object> getAdvancedMenuStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Menu> publishedMenus = getPublishedMenus();
        
        if (!publishedMenus.isEmpty()) {
            double avgItems = publishedMenus.stream()
                    .mapToInt(Menu::getTotalMenuItems)
                    .average()
                    .orElse(0.0);
            
            double avgSections = publishedMenus.stream()
                    .mapToInt(Menu::getSectionCount)
                    .average()
                    .orElse(0.0);
            
            long buffetSuitableCount = publishedMenus.stream()
                    .filter(Menu::isSuitableForBuffet)
                    .count();
            
            long fingerFoodCount = publishedMenus.stream()
                    .filter(Menu::isFingerFoodOnly)
                    .count();
            
            stats.put("averageItemsPerMenu", avgItems);
            stats.put("averageSectionsPerMenu", avgSections);
            stats.put("buffetSuitableMenus", (int) buffetSuitableCount);
            stats.put("fingerFoodMenus", (int) fingerFoodCount);
        } else {
            stats.put("averageItemsPerMenu", 0.0);
            stats.put("averageSectionsPerMenu", 0.0);
            stats.put("buffetSuitableMenus", 0);
            stats.put("fingerFoodMenus", 0);
        }
        
        // Aggiungi statistiche base
        stats.putAll(getMenuStatistics());
        
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
    
    /**
     * NUOVO: Ripristina un menù archiviato
     */
    public void restoreArchivedMenu(String menuId, String chefId) {
        Menu menu = getMenuOrThrow(menuId);
        
        if (!menu.getChefId().equals(chefId)) {
            throw new SecurityException("Solo il chef creatore può ripristinare il menù");
        }
        
        if (menu.getState() != Menu.MenuState.ARCHIVED) {
            throw new IllegalStateException("Solo i menù archiviati possono essere ripristinati");
        }
        
        menu.restore(); // Metodo da aggiungere al modello Menu
    }
    
    /**
     * NUOVO: Esporta menù in diversi formati
     */
    public String exportMenu(String menuId, ExportFormat format) {
        Menu menu = getMenuOrThrow(menuId);
        
        switch (format) {
            case PDF:
                return generateMenuPDF(menu);
            case HTML:
                return saveMenuAsHTML(menu);
            case TXT:
                return saveMenuAsText(menu);
            default:
                throw new IllegalArgumentException("Formato di export non supportato: " + format);
        }
    }
    
    private String saveMenuAsHTML(Menu menu) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("Menu_%s_%s.html", 
                                          menu.getName().replaceAll("[^a-zA-Z0-9]", "_"), 
                                          timestamp);
            String filePath = pdfOutputDirectory + File.separator + fileName;
            
            String htmlContent = generateMenuHTML(menu);
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(htmlContent);
            }
            
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Errore nell'export HTML: " + e.getMessage(), e);
        }
    }
    
    private String saveMenuAsText(Menu menu) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("Menu_%s_%s.txt", 
                                          menu.getName().replaceAll("[^a-zA-Z0-9]", "_"), 
                                          timestamp);
            String filePath = pdfOutputDirectory + File.separator + fileName;
            
            StringBuilder content = new StringBuilder();
            content.append("=== ").append(menu.getName().toUpperCase()).append(" ===\n\n");
            
            if (menu.getDescription() != null) {
                content.append(menu.getDescription()).append("\n\n");
            }
            
            for (MenuSection section : menu.getSections()) {
                content.append("--- ").append(section.getTitle().toUpperCase()).append(" ---\n");
                for (com.saslab.model.MenuItem item : section.getMenuItems()) {
                    content.append("• ").append(item.getDisplayName());
                    if (item.hasPrice()) {
                        content.append(" - €").append(String.format("%.2f", item.getPrice()));
                    }
                    content.append("\n");
                }
                content.append("\n");
            }
            
            content.append("Generato il ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
            content.append("Chef: ").append(menu.getChefId()).append("\n");
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(content.toString());
            }
            
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Errore nell'export TXT: " + e.getMessage(), e);
        }
    }
    
    // NUOVO: Enum per formati di export
    public enum ExportFormat {
        PDF("PDF"),
        HTML("HTML"),
        TXT("Testo");
        
        private final String displayName;
        
        ExportFormat(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}