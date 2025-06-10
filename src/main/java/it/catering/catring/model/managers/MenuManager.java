// File: src/main/java/it/catering/catring/model/managers/MenuManager.java
package it.catering.catring.model.managers;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.exporters.PdfExporter;
import it.catering.catring.model.exporters.DetailedMenuExportDecorator;
import it.catering.catring.model.observers.*;
import it.catering.catring.model.persistence.JsonPersistenceManager;
import it.catering.catring.model.visitors.MenuStatsVisitor;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MenuManager implements Subject<MenuObserver> {
    private static MenuManager instance;
    private List<Menu> menus;
    private List<MenuObserver> observers;
    private JsonPersistenceManager persistenceManager;
    private PdfExporter pdfExporter;
    
    private MenuManager() {
        this.menus = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.persistenceManager = JsonPersistenceManager.getInstance();
        this.pdfExporter = PdfExporter.getInstance();
        loadData();
    }
    
    public static synchronized MenuManager getInstance() {
        if (instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }
    
    private void loadData() {
        try {
            List<Menu> loadedMenus = persistenceManager.loadMenus();
            menus.addAll(loadedMenus);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei menu: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try {
            persistenceManager.saveMenus(menus);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio dei menu: " + e.getMessage());
        }
    }
    
    // CRUD Operations
    public Menu createMenu(String titolo, Chef chef) {
        // Verifica che non esista già un menu con lo stesso titolo dello stesso chef
        boolean exists = menus.stream()
            .anyMatch(m -> m.getTitolo().equalsIgnoreCase(titolo) && m.getChef().equals(chef));
        
        if (exists) {
            throw new IllegalArgumentException("Esiste già un menu con questo titolo");
        }
        
        Menu menu = new Menu(titolo, chef);
        menus.add(menu);
        saveData();
        notifyMenuCreated(menu);
        return menu;
    }
    
    public void updateMenu(Menu menu) {
        if (menu.isUtilizzato()) {
            throw new IllegalStateException("Non è possibile modificare un menu utilizzato");
        }
        
        saveData();
        notifyMenuUpdated(menu);
    }
    
    public void deleteMenu(Menu menu) {
        if (menu.isUtilizzato()) {
            throw new IllegalStateException("Impossibile eliminare un menu utilizzato");
        }
        
        if (menus.remove(menu)) {
            saveData();
            notifyMenuDeleted(menu);
        }
    }
    
    // Copia menu
    public Menu copyMenu(Menu originalMenu, String nuovoTitolo, Chef chef) {
        Menu copiedMenu = originalMenu.createCopy(nuovoTitolo, chef);
        menus.add(copiedMenu);
        saveData();
        notifyMenuCreated(copiedMenu);
        return copiedMenu;
    }
    
    // Approvazione menu (Pattern State potrebbe essere applicato qui)
    public void approvaMenu(Menu menu) {
        if (menu.getSezioni().isEmpty()) {
            throw new IllegalStateException("Non è possibile approvare un menu vuoto");
        }
        
        menu.setUtilizzato(true);
        updateMenu(menu);
    }
    
    // Query methods
    public List<Menu> getMenusByChef(Chef chef) {
        return menus.stream()
                .filter(m -> m.getChef().equals(chef))
                .collect(Collectors.toList());
    }
    
    public List<Menu> getMenusDisponibili() {
        return menus.stream()
                .filter(m -> !m.isUtilizzato())
                .collect(Collectors.toList());
    }
    
    public List<Menu> getMenusUtilizzati() {
        return menus.stream()
                .filter(Menu::isUtilizzato)
                .collect(Collectors.toList());
    }
    
    public Menu findMenuById(Long id) {
        return menus.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<Menu> getAllMenus() {
        return new ArrayList<>(menus);
    }
    
    // Ricerca avanzata
    public List<Menu> cercaMenuPerCaratteristiche(boolean fingerFood, boolean buffet, 
                                                 boolean soloPiattiFreddi, boolean cucinaRichiesta) {
        return menus.stream()
            .filter(m -> !m.isUtilizzato())
            .filter(m -> !fingerFood || m.isFingerFood())
            .filter(m -> !buffet || m.isAdeguatoBuffet())
            .filter(m -> !soloPiattiFreddi || m.isSoloPiattiFreddi())
            .filter(m -> !cucinaRichiesta || m.isCucinaRichiesta())
            .collect(Collectors.toList());
    }
    
    public List<Menu> cercaMenuConRicetta(Ricetta ricetta) {
        return menus.stream()
            .filter(m -> m.contienRicetta(ricetta))
            .collect(Collectors.toList());
    }
    
    public List<Menu> cercaMenuPerPeriodo(Date dataInizio, Date dataFine) {
        return menus.stream()
            .filter(m -> {
                Date dataCreazione = m.getDataCreazione();
                return dataCreazione != null && 
                       !dataCreazione.before(dataInizio) && 
                       !dataCreazione.after(dataFine);
            })
            .collect(Collectors.toList());
    }
    
    // Export functionality
    public File exportMenuToPdf(Menu menu, boolean detailed) throws IOException {
        if (detailed) {
            DetailedMenuExportDecorator decorator = new DetailedMenuExportDecorator(pdfExporter);
            return decorator.exportMenuToPdf(menu);
        } else {
            return pdfExporter.exportMenuToPdf(menu);
        }
    }
    
    // Statistiche usando il pattern Visitor
    public Map<String, Object> getMenuStatistics(Menu menu) {
        MenuStatsVisitor statsVisitor = new MenuStatsVisitor();
        menu.accept(statsVisitor);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("sezioni", statsVisitor.getTotalSezioni());
        stats.put("voci", statsVisitor.getTotalVoci());
        stats.put("portate", statsVisitor.getTotalPortate());
        stats.put("cuocoRichiesto", menu.isCuocoRichiesto());
        stats.put("fingerFood", menu.isFingerFood());
        stats.put("buffet", menu.isAdeguatoBuffet());
        
        return stats;
    }
    
    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totaleMenu", menus.size());
        stats.put("menuDisponibili", getMenusDisponibili().size());
        stats.put("menuUtilizzati", getMenusUtilizzati().size());
        
        // Conteggi per chef
        Map<Chef, Long> menuPerChef = menus.stream()
            .collect(Collectors.groupingBy(Menu::getChef, Collectors.counting()));
        stats.put("menuPerChef", menuPerChef);
        
        // Caratteristiche più comuni
        long fingerFoodCount = menus.stream().mapToLong(m -> m.isFingerFood() ? 1 : 0).sum();
        long buffetCount = menus.stream().mapToLong(m -> m.isAdeguatoBuffet() ? 1 : 0).sum();
        long piattiFreddiCount = menus.stream().mapToLong(m -> m.isSoloPiattiFreddi() ? 1 : 0).sum();
        
        stats.put("percentualeFingerFood", menus.isEmpty() ? 0 : (fingerFoodCount * 100.0 / menus.size()));
        stats.put("percentualeBuffet", menus.isEmpty() ? 0 : (buffetCount * 100.0 / menus.size()));
        stats.put("percentualePiattiFreddi", menus.isEmpty() ? 0 : (piattiFreddiCount * 100.0 / menus.size()));
        
        return stats;
    }
    
    // Validazione menu
    public List<String> validateMenu(Menu menu) {
        List<String> errori = new ArrayList<>();
        
        if (menu.getTitolo() == null || menu.getTitolo().trim().isEmpty()) {
            errori.add("Il titolo del menu è obbligatorio");
        }
        
        if (menu.getSezioni().isEmpty()) {
            errori.add("Il menu deve contenere almeno una sezione");
        }
        
        boolean hasVoci = menu.getSezioni().stream()
            .anyMatch(s -> !s.getVoci().isEmpty());
        
        if (!hasVoci) {
            errori.add("Il menu deve contenere almeno una ricetta");
        }
        
        // Verifica coerenza: se è finger food, le ricette dovrebbero essere appropriate
        if (menu.isFingerFood()) {
            boolean hasNonFingerFood = menu.getTutteLeRicette().stream()
                .anyMatch(r -> !r.hasTag("finger food"));
            
            if (hasNonFingerFood) {
                errori.add("Alcune ricette potrebbero non essere adatte per finger food");
            }
        }
        
        return errori;
    }
    
    // Suggerimenti
    public List<Ricetta> suggerisciRicettePerMenu(Menu menu, RicettarioManager ricettarioManager) {
        List<Ricetta> ricetteDisponibili = ricettarioManager.getRicettePubblicate();
        List<Ricetta> ricetteGiaPresenti = menu.getTutteLeRicette();
        
        // Filtra le ricette già presenti
        List<Ricetta> candidati = ricetteDisponibili.stream()
            .filter(r -> !ricetteGiaPresenti.contains(r))
            .collect(Collectors.toList());
        
        // Se il menu ha caratteristiche specifiche, filtra di conseguenza
        if (menu.isFingerFood()) {
            candidati = candidati.stream()
                .filter(r -> r.hasTag("finger food"))
                .collect(Collectors.toList());
        }
        
        if (menu.isSoloPiattiFreddi()) {
            candidati = candidati.stream()
                .filter(r -> r.hasTag("freddo"))
                .collect(Collectors.toList());
        }
        
        // Limita a 5 suggerimenti
        return candidati.stream()
            .limit(5)
            .collect(Collectors.toList());
    }
    
    // Observer pattern implementation
    @Override
    public void addObserver(MenuObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(MenuObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        // Implementazione generica
    }
    
    private void notifyMenuCreated(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuCreated(menu);
        }
    }
    
    private void notifyMenuUpdated(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuUpdated(menu);
        }
    }
    
    private void notifyMenuDeleted(Menu menu) {
        for (MenuObserver observer : observers) {
            observer.onMenuDeleted(menu);
        }
    }
}