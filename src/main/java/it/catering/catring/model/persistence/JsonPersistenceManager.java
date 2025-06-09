package it.catering.catring.model.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.catering.catring.model.entities.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonPersistenceManager {
    private static JsonPersistenceManager instance;
    private final ObjectMapper objectMapper;
    private final String dataDirectory = "data";
    
    private JsonPersistenceManager() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Crea la directory data se non esiste
        try {
            Files.createDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            System.err.println("Errore nella creazione della directory data: " + e.getMessage());
        }
    }
    
    public static synchronized JsonPersistenceManager getInstance() {
        if (instance == null) {
            instance = new JsonPersistenceManager();
        }
        return instance;
    }
    
    // Metodi generici per salvare e caricare
    public <T> void saveList(List<T> list, String fileName, Class<T> clazz) {
        try {
            File file = new File(dataDirectory, fileName);
            objectMapper.writeValue(file, list);
        } catch (IOException e) {
            System.err.println("Errore nel salvare " + fileName + ": " + e.getMessage());
        }
    }
    
    public <T> List<T> loadList(String fileName, Class<T> clazz) {
        try {
            File file = new File(dataDirectory, fileName);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            return objectMapper.readValue(file, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            System.err.println("Errore nel caricare " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Metodi specifici per entità
    public void saveRicette(List<Ricetta> ricette) {
        saveList(ricette, "recipes.json", Ricetta.class);
    }
    
    public List<Ricetta> loadRicette() {
        return loadList("recipes.json", Ricetta.class);
    }
    
    public void savePreparazioni(List<Preparazione> preparazioni) {
        saveList(preparazioni, "preparations.json", Preparazione.class);
    }
    
    public List<Preparazione> loadPreparazioni() {
        return loadList("preparations.json", Preparazione.class);
    }
    
    public void saveMenus(List<Menu> menus) {
        saveList(menus, "menus.json", Menu.class);
    }
    
    public List<Menu> loadMenus() {
        return loadList("menus.json", Menu.class);
    }
    
    public void saveCompiti(List<Compito> compiti) {
        saveList(compiti, "tasks.json", Compito.class);
    }
    
    public List<Compito> loadCompiti() {
        return loadList("tasks.json", Compito.class);
    }
    
    public void saveUsers(List<User> users) {
        saveList(users, "users.json", User.class);
    }
    
    public List<User> loadUsers() {
        return loadList("users.json", User.class);
    }
}