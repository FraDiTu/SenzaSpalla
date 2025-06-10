// src/main/java/com/catering/model/singleton/MenuRepository.java
package com.catering.model.singleton;

import com.catering.model.domain.Menu;
import com.catering.model.domain.Ricetta;
import com.catering.model.domain.Evento;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton pattern - repository unico per i dati
 * Information Expert - gestisce la persistenza
 * Versione semplificata per il progetto
 */
public class MenuRepository {
    private static volatile MenuRepository instance;
    private final Map<String, Menu> menus = new ConcurrentHashMap<>();
    private final Map<String, Ricetta> ricette = new ConcurrentHashMap<>();
    private final Map<String, Evento> eventi = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final String dataDirectory = "data";
    
    private MenuRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        initializeDataDirectory();
        loadData();
    }
    
    public static MenuRepository getInstance() {
        if (instance == null) {
            synchronized (MenuRepository.class) {
                if (instance == null) {
                    instance = new MenuRepository();
                }
            }
        }
        return instance;
    }
    
    private void initializeDataDirectory() {
        new File(dataDirectory).mkdirs();
    }
    
    // Information Expert - sa come caricare i propri dati
    private void loadData() {
        loadRicette();
        loadEventi();
        loadMenus();
    }
    
    private void loadRicette() {
        try {
            File ricetteFile = new File(dataDirectory + "/ricette.json");
            if (ricetteFile.exists()) {
                Ricetta[] ricetteArray = objectMapper.readValue(ricetteFile, Ricetta[].class);
                for (Ricetta ricetta : ricetteArray) {
                    ricette.put(ricetta.getId(), ricetta);
                }
            } else {
                createSampleRicette();
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento ricette: " + e.getMessage());
            createSampleRicette();
        }
    }
    
    private void loadEventi() {
        try {
            File eventiFile = new File(dataDirectory + "/eventi.json");
            if (eventiFile.exists()) {
                Evento[] eventiArray = objectMapper.readValue(eventiFile, Evento[].class);
                for (Evento evento : eventiArray) {
                    eventi.put(evento.getId(), evento);
                }
            } else {
                createSampleEventi();
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento eventi: " + e.getMessage());
            createSampleEventi();
        }
    }
    
    private void loadMenus() {
        try {
            File menusFile = new File(dataDirectory + "/menus.json");
            if (menusFile.exists()) {
                Menu[] menusArray = objectMapper.readValue(menusFile, Menu[].class);
                for (Menu menu : menusArray) {
                    menus.put(menu.getId(), menu);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento menu: " + e.getMessage());
        }
    }
    
    private void createSampleRicette() {
        Ricetta carbonara = new Ricetta("r1", "Spaghetti alla Carbonara", 
            "Piatto tradizionale romano", 
            Arrays.asList("Spaghetti", "Guanciale", "Uova", "Pecorino", "Pepe nero"),
            "20 minuti", "Chef Mario");
        
        Ricetta tiramisu = new Ricetta("r2", "Tiramisù", 
            "Dolce al cucchiaio classico",
            Arrays.asList("Savoiardi", "Mascarpone", "Caffè", "Uova", "Zucchero", "Cacao"),
            "30 minuti + riposo", "Chef Anna");
        
        ricette.put(carbonara.getId(), carbonara);
        ricette.put(tiramisu.getId(), tiramisu);
        saveRicette();
    }
    
    private void createSampleEventi() {
        Evento matrimonio = new Evento("e1", "Matrimonio Rossi-Bianchi", 
            LocalDate.now().plusDays(7),
            LocalDate.now().plusDays(7),
            "Villa San Marco", "Matrimonio", "c1");
        
        eventi.put(matrimonio.getId(), matrimonio);
        saveEventi();
    }
    
    // Information Expert - sa come salvare i propri dati
    public void saveMenu(Menu menu) {
        menus.put(menu.getId(), menu);
        saveMenus();
    }
    
    public void saveRicetta(Ricetta ricetta) {
        ricette.put(ricetta.getId(), ricetta);
        saveRicette();
    }
    
    private void saveMenus() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                       .writeValue(new File(dataDirectory + "/menus.json"), 
                                 menus.values().toArray(new Menu[0]));
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio menu: " + e.getMessage());
        }
    }
    
    private void saveRicette() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                       .writeValue(new File(dataDirectory + "/ricette.json"), 
                                 ricette.values().toArray(new Ricetta[0]));
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio ricette: " + e.getMessage());
        }
    }
    
    private void saveEventi() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                       .writeValue(new File(dataDirectory + "/eventi.json"), 
                                 eventi.values().toArray(new Evento[0]));
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio eventi: " + e.getMessage());
        }
    }
    
    // Information Expert - fornisce accesso ai dati
    public List<Menu> getAllMenus() {
        return new ArrayList<>(menus.values());
    }
    
    public List<Ricetta> getAllRicette() {
        return new ArrayList<>(ricette.values());
    }
    
    public List<Evento> getAllEventi() {
        return new ArrayList<>(eventi.values());
    }
    
    public Optional<Menu> getMenuById(String id) {
        return Optional.ofNullable(menus.get(id));
    }
    
    public Optional<Ricetta> getRicettaById(String id) {
        return Optional.ofNullable(ricette.get(id));
    }
    
    public Optional<Evento> getEventoById(String id) {
        return Optional.ofNullable(eventi.get(id));
    }
    
    public void deleteMenu(String id) {
        if (menus.remove(id) != null) {
            saveMenus();
        }
    }
}