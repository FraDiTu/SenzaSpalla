// src/main/java/com/catering/service/MenuService.java
package com.catering.service;

import com.catering.model.domain.*;
import com.catering.model.factory.MenuComponentFactory;
import com.catering.model.factory.StandardMenuComponentFactory;
import com.catering.model.observer.MenuObserver;
import com.catering.model.observer.MenuSubject;
import com.catering.model.singleton.MenuRepository;
import com.catering.model.state.DraftMenuState;
import com.catering.model.state.MenuState;
import com.catering.model.strategy.ExportStrategy;
import com.catering.model.visitor.MenuValidationVisitor;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controller pattern - coordina operazioni sul menu
 * Information Expert - sa gestire la logica di business dei menu
 * High Cohesion - responsabilità coese per gestione menu
 * Versione semplificata
 */
public class MenuService implements MenuSubject {
    private final MenuRepository repository;
    private final MenuComponentFactory factory;
    private final List<MenuObserver> observers;
    private MenuState currentState;
    
    // Singleton per evitare multiple istanze
    private static MenuService instance;
    
    private MenuService() {
        this.repository = MenuRepository.getInstance();
        this.factory = new StandardMenuComponentFactory();
        this.observers = new CopyOnWriteArrayList<>();
        this.currentState = new DraftMenuState();
    }
    
    public static MenuService getInstance() {
        if (instance == null) {
            synchronized (MenuService.class) {
                if (instance == null) {
                    instance = new MenuService();
                }
            }
        }
        return instance;
    }
    
    // Creator pattern - sa quando creare un nuovo menu
    public Menu createMenu(String titolo, String eventoId) {
        if (titolo == null || titolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo del menu è obbligatorio");
        }
        
        Menu menu = factory.createMenu(titolo.trim(), eventoId);
        repository.saveMenu(menu);
        notifyMenuChanged(menu);
        return menu;
    }
    
    // Information Expert - coordina l'aggiunta di sezioni
    public Menu addSezione(String menuId, String nomeSezione) {
        Menu menu = repository.getMenuById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("Menu non trovato"));
        
        if (!currentState.canEdit()) {
            throw new IllegalStateException("Impossibile modificare il menu nello stato: " + 
                                          currentState.getStateName());
        }
        
        Map<String, List<VoceMenu>> nuoveSezioni = new LinkedHashMap<>(menu.getSezioni());
        nuoveSezioni.put(nomeSezione, new ArrayList<>());
        
        Menu menuAggiornato = new Menu(
            menu.getId(),
            menu.getTitolo(),
            menu.getEventoId(),
            menu.getDataCreazione(),
            nuoveSezioni,
            menu.getInformazioniAggiuntive(),
            menu.isPubblicato()
        );
        
        repository.saveMenu(menuAggiornato);
        notifyMenuChanged(menuAggiornato);
        observers.forEach(obs -> obs.onMenuSectionAdded(nomeSezione));
        
        return menuAggiornato;
    }
    
    // Information Expert - coordina l'aggiunta di voci menu
    public Menu addVoceMenu(String menuId, String ricettaId, String nomeVisualizzato, String sezione) {
        Menu menu = repository.getMenuById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("Menu non trovato"));
        
        // Verifica che la ricetta esista (senza usare la variabile)
        repository.getRicettaById(ricettaId)
            .orElseThrow(() -> new IllegalArgumentException("Ricetta non trovata"));
        
        if (!currentState.canEdit()) {
            throw new IllegalStateException("Impossibile modificare il menu");
        }
        
        VoceMenu nuovaVoce = factory.createVoceMenu(ricettaId, nomeVisualizzato, sezione);
        
        Map<String, List<VoceMenu>> nuoveSezioni = new LinkedHashMap<>(menu.getSezioni());
        nuoveSezioni.computeIfAbsent(sezione, k -> new ArrayList<>()).add(nuovaVoce);
        
        Menu menuAggiornato = new Menu(
            menu.getId(),
            menu.getTitolo(),
            menu.getEventoId(),
            menu.getDataCreazione(),
            nuoveSezioni,
            menu.getInformazioniAggiuntive(),
            menu.isPubblicato()
        );
        
        repository.saveMenu(menuAggiornato);
        notifyMenuChanged(menuAggiornato);
        observers.forEach(obs -> obs.onMenuItemAdded(sezione, nomeVisualizzato));
        
        return menuAggiornato;
    }
    
    // Information Expert - gestisce pubblicazione
    public Menu publishMenu(String menuId) {
        Menu menu = repository.getMenuById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("Menu non trovato"));
        
        // Validazione prima della pubblicazione
        MenuValidationVisitor validator = new MenuValidationVisitor();
        validator.visitMenu(menu);
        
        if (!validator.isValid()) {
            throw new IllegalStateException("Menu non valido: " + 
                String.join(", ", validator.getResult()));
        }
        
        Menu menuPubblicato = new Menu(
            menu.getId(),
            menu.getTitolo(),
            menu.getEventoId(),
            menu.getDataCreazione(),
            menu.getSezioni(),
            menu.getInformazioniAggiuntive(),
            true
        );
        
        repository.saveMenu(menuPubblicato);
        currentState = currentState.publish();
        
        observers.forEach(obs -> obs.onMenuPublished(menuPubblicato));
        
        return menuPubblicato;
    }
    
    // Information Expert - gestisce esportazione
    public void exportMenu(String menuId, String filePath, ExportStrategy strategy) throws IOException {
        Menu menu = repository.getMenuById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("Menu non trovato"));
        
        strategy.export(menu, filePath);
    }
    
    // Information Expert - fornisce accesso ai dati
    public List<Menu> getAllMenus() {
        return repository.getAllMenus();
    }
    
    public List<Ricetta> getAllRicette() {
        return repository.getAllRicette();
    }
    
    public List<Evento> getAllEventi() {
        return repository.getAllEventi();
    }
    
    public Optional<Menu> getMenuById(String id) {
        return repository.getMenuById(id);
    }
    
    public Optional<Evento> getEventoById(String id) {
        return repository.getEventoById(id);
    }
    
    // Observer pattern implementation
    @Override
    public void addObserver(MenuObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(MenuObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        // Implementazione generica - non usata direttamente
    }
    
    @Override
    public List<MenuObserver> getObservers() {
        return new ArrayList<>(observers);
    }
    
    private void notifyMenuChanged(Menu menu) {
        observers.forEach(obs -> obs.onMenuChanged(menu));
    }
    
    public MenuState getCurrentState() {
        return currentState;
    }
}