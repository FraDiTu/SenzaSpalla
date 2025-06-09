// File: src/main/java/it/catering/catring/model/managers/RicettarioManager.java
package it.catering.catring.model.managers;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.persistence.JsonPersistenceManager;
import it.catering.catring.model.states.*;
import java.util.*;
import java.util.stream.Collectors;

public class RicettarioManager {
    private static RicettarioManager instance;
    private List<Preparazione> preparazioni;
    private List<Ricetta> ricette;
    private List<Ingrediente> ingredientiBase;
    private JsonPersistenceManager persistenceManager;
    
    private RicettarioManager() {
        this.preparazioni = new ArrayList<>();
        this.ricette = new ArrayList<>();
        this.ingredientiBase = new ArrayList<>();
        this.persistenceManager = JsonPersistenceManager.getInstance();
        
        initializeIngredientiBase();
        loadData();
    }
    
    public static synchronized RicettarioManager getInstance() {
        if (instance == null) {
            instance = new RicettarioManager();
        }
        return instance;
    }
    
    private void initializeIngredientiBase() {
        ingredientiBase.addAll(Arrays.asList(
            new Ingrediente("Farina 00", "g"),
            new Ingrediente("Uova", "pz"),
            new Ingrediente("Latte", "ml"),
            new Ingrediente("Burro", "g"),
            new Ingrediente("Olio EVO", "ml"),
            new Ingrediente("Sale", "g"),
            new Ingrediente("Pepe", "g"),
            new Ingrediente("Pomodoro", "g"),
            new Ingrediente("Mozzarella", "g"),
            new Ingrediente("Parmigiano", "g"),
            new Ingrediente("Basilico", "g"),
            new Ingrediente("Aglio", "spicchi"),
            new Ingrediente("Cipolla", "g"),
            new Ingrediente("Carote", "g"),
            new Ingrediente("Sedano", "g"),
            new Ingrediente("Funghi Porcini", "g"),
            new Ingrediente("Mascarpone", "g"),
            new Ingrediente("Caffè", "ml"),
            new Ingrediente("Cacao", "g"),
            new Ingrediente("Zucchero", "g")
        ));
    }
    
    private void loadData() {
        try {
            List<Ricetta> loadedRicette = persistenceManager.loadRicette();
            List<Preparazione> loadedPreparazioni = persistenceManager.loadPreparazioni();
            
            ricette.addAll(loadedRicette);
            preparazioni.addAll(loadedPreparazioni);
            
            // Ripristina gli stati corretti
            for (Preparazione prep : preparazioni) {
                if (prep.isPubblicata()) {
                    prep.setState(new PubblicataState());
                } else {
                    prep.setState(new BozzaState());
                }
            }
            
            for (Ricetta ricetta : ricette) {
                if (ricetta.isPubblicata()) {
                    ricetta.setState(new PubblicataState());
                } else {
                    ricetta.setState(new BozzaState());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei dati del ricettario: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try {
            persistenceManager.saveRicette(ricette);
            persistenceManager.savePreparazioni(preparazioni);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio dei dati del ricettario: " + e.getMessage());
        }
    }
    
    // CRUD Ricette
    public Ricetta createRicetta(String nome, String descrizione, User proprietario, int numeroPortate) {
        // Verifica che non esista già una ricetta con lo stesso nome dello stesso proprietario
        boolean exists = ricette.stream()
            .anyMatch(r -> r.getNome().equalsIgnoreCase(nome) && r.getProprietario().equals(proprietario));
        
        if (exists) {
            throw new IllegalArgumentException("Esiste già una ricetta con questo nome");
        }
        
        Ricetta ricetta = new Ricetta(nome, descrizione, proprietario, numeroPortate);
        ricette.add(ricetta);
        saveData();
        return ricetta;
    }
    
    public void updateRicetta(Ricetta ricetta) {
        if (!ricetta.canModify()) {
            throw new IllegalStateException("La ricetta non può essere modificata nel suo stato attuale");
        }
        saveData();
    }
    
    public void deleteRicetta(Ricetta ricetta) {
        if (!ricetta.canDelete()) {
            throw new IllegalStateException("La ricetta non può essere eliminata nel suo stato attuale");
        }
        
        if (isRicettaInUso(ricetta)) {
            throw new IllegalStateException("La ricetta è utilizzata in uno o più menu e non può essere eliminata");
        }
        
        ricette.remove(ricetta);
        saveData();
    }
    
    public void pubblicaRicetta(Ricetta ricetta) {
        ricetta.pubblica();
        saveData();
    }
    
    public void ritiraRicettaDallaPubblicazione(Ricetta ricetta) {
        if (isRicettaInUso(ricetta)) {
            throw new IllegalStateException("La ricetta è utilizzata in uno o più menu e non può essere ritirata");
        }
        
        ricetta.ritiraDallaPubblicazione();
        saveData();
    }
    
    // CRUD Preparazioni
    public Preparazione createPreparazione(String nome, String descrizione, User proprietario) {
        // Verifica che non esista già una preparazione con lo stesso nome dello stesso proprietario
        boolean exists = preparazioni.stream()
            .anyMatch(p -> p.getNome().equalsIgnoreCase(nome) && p.getProprietario().equals(proprietario));
        
        if (exists) {
            throw new IllegalArgumentException("Esiste già una preparazione con questo nome");
        }
        
        Preparazione preparazione = new Preparazione(nome, descrizione, proprietario);
        preparazioni.add(preparazione);
        saveData();
        return preparazione;
    }
    
    public void updatePreparazione(Preparazione preparazione) {
        if (!preparazione.canModify()) {
            throw new IllegalStateException("La preparazione non può essere modificata nel suo stato attuale");
        }
        saveData();
    }
    
    public void deletePreparazione(Preparazione preparazione) {
        if (!preparazione.canDelete()) {
            throw new IllegalStateException("La preparazione non può essere eliminata nel suo stato attuale");
        }
        
        if (isPreparazioneInUso(preparazione)) {
            throw new IllegalStateException("La preparazione è utilizzata in altre ricette e non può essere eliminata");
        }
        
        preparazioni.remove(preparazione);
        saveData();
    }
    
    public void pubblicaPreparazione(Preparazione preparazione) {
        preparazione.pubblica();
        saveData();
    }
    
    public void ritiraPreparazioneDallaPubblicazione(Preparazione preparazione) {
        if (isPreparazioneInUso(preparazione)) {
            throw new IllegalStateException("La preparazione è utilizzata in altre ricette e non può essere ritirata");
        }
        
        preparazione.ritiraDallaPubblicazione();
        saveData();
    }
    
    // Query methods
    public List<Ricetta> getRicettePubblicate() {
        return ricette.stream()
                .filter(Preparazione::isPubblicata)
                .collect(Collectors.toList());
    }
    
    public List<Preparazione> getPreparazioniPubblicate() {
        return preparazioni.stream()
                .filter(Preparazione::isPubblicata)
                .collect(Collectors.toList());
    }
    
    public List<Ricetta> getRicetteByProprietario(User proprietario) {
        return ricette.stream()
                .filter(r -> r.getProprietario().equals(proprietario))
                .collect(Collectors.toList());
    }
    
    public List<Preparazione> getPreparazioniByProprietario(User proprietario) {
        return preparazioni.stream()
                .filter(p -> p.getProprietario().equals(proprietario))
                .collect(Collectors.toList());
    }
    
    public List<Ricetta> getRicetteVisibili(User user) {
        return ricette.stream()
                .filter(r -> r.isVisible() || r.getProprietario().equals(user))
                .collect(Collectors.toList());
    }
    
    public List<Preparazione> getPreparazioniVisibili(User user) {
        return preparazioni.stream()
                .filter(p -> p.isVisible() || p.getProprietario().equals(user))
                .collect(Collectors.toList());
    }
    
    // Ricerca per tag
    public List<Ricetta> cercaRicettePerTag(String tag) {
        return ricette.stream()
                .filter(r -> r.isPubblicata() && r.hasTag(tag))
                .collect(Collectors.toList());
    }
    
    public List<Preparazione> cercaPreparazioniPerTag(String tag) {
        return preparazioni.stream()
                .filter(p -> p.isPubblicata() && p.hasTag(tag))
                .collect(Collectors.toList());
    }
    
    // Ricerca per nome
    public List<Ricetta> cercaRicettePerNome(String nome) {
        return ricette.stream()
                .filter(r -> r.isPubblicata() && 
                        r.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Preparazione> cercaPreparazioniPerNome(String nome) {
        return preparazioni.stream()
                .filter(p -> p.isPubblicata() && 
                        p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // Gestione ingredienti
    public List<Ingrediente> getIngredientiBase() {
        return new ArrayList<>(ingredientiBase);
    }
    
    public void aggiungiIngredienteBase(Ingrediente ingrediente) {
        if (!ingredientiBase.contains(ingrediente)) {
            ingredientiBase.add(ingrediente);
        }
    }
    
    public List<Ingrediente> getTuttiGliIngredienti() {
        List<Ingrediente> tuttiIngredienti = new ArrayList<>(ingredientiBase);
        
        // Aggiungi le preparazioni che possono essere usate come ingredienti
        preparazioni.stream()
            .filter(p -> p.canBeUsedAsIngredient())
            .forEach(p -> tuttiIngredienti.add(
                new Ingrediente(p.getNome(), p.getUnitaMisuraRisultato())
            ));
        
        return tuttiIngredienti;
    }
    
    // Tag management
    public Set<String> getAllTags() {
        Set<String> allTags = new HashSet<>();
        ricette.forEach(r -> allTags.addAll(r.getTags()));
        preparazioni.forEach(p -> allTags.addAll(p.getTags()));
        return allTags;
    }
    
    public Set<String> getPopularTags(int limit) {
    Map<String, Long> tagCount = new HashMap<>();
    
    ricette.stream()
        .filter(Preparazione::isPubblicata)
        .flatMap(r -> r.getTags().stream())
        .forEach(tag -> tagCount.merge(tag, 1L, Long::sum));
    
    preparazioni.stream()
        .filter(Preparazione::isPubblicata)
        .flatMap(p -> p.getTags().stream())
        .forEach(tag -> tagCount.merge(tag, 1L, Long::sum));
    
    // FIX: Usare LinkedHashSet constructor invece del metodo inesistente
    return tagCount.entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .limit(limit)
        .map(Map.Entry::getKey)
        .collect(Collectors.toCollection(LinkedHashSet::new));
}
    
    // Verifica utilizzo
    private boolean isRicettaInUso(Ricetta ricetta) {
        // Qui dovresti verificare se la ricetta è utilizzata in qualche menu
        // Per ora restituiamo false, ma potrebbe essere implementato consultando il MenuManager
        return false;
    }
    
    private boolean isPreparazioneInUso(Preparazione preparazione) {
        // Verifica se la preparazione è utilizzata come ingrediente in altre ricette
        return ricette.stream()
            .anyMatch(r -> r.getIngredienti().stream()
                .anyMatch(dose -> dose.getIngrediente().getNome().equals(preparazione.getNome()))) ||
               preparazioni.stream()
                .anyMatch(p -> p.getIngredienti().stream()
                    .anyMatch(dose -> dose.getIngrediente().getNome().equals(preparazione.getNome())));
    }
    
    // Metodi di utilità
    public boolean canDelete(Preparazione preparazione, User user) {
        return preparazione.getProprietario().equals(user) && 
               preparazione.canDelete() && 
               !isPreparazioneInUso(preparazione);
    }
    
    public boolean canModify(Preparazione preparazione, User user) {
        return preparazione.getProprietario().equals(user) && preparazione.canModify();
    }
    
    public Preparazione findById(Long id) {
        return preparazioni.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public Ricetta findRicettaById(Long id) {
        return ricette.stream()
            .filter(r -> r.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    // Copia ricetta/preparazione
    public Ricetta copyRicetta(Ricetta original, User newOwner, String newName) {
        Ricetta copy = new Ricetta(newName, original.getDescrizione(), newOwner, original.getNumeroPortate());
        copy.setAutore(original.getAutore());
        copy.setTempoPreparazione(original.getTempoPreparazione());
        copy.setTags(new HashSet<>(original.getTags()));
        copy.setIngredienti(new ArrayList<>(original.getIngredienti()));
        copy.setIstruzioniAnticipo(new ArrayList<>(original.getIstruzioniAnticipo()));
        copy.setIstruzioniUltimo(new ArrayList<>(original.getIstruzioniUltimo()));
        
        ricette.add(copy);
        saveData();
        return copy;
    }
    
    public Preparazione copyPreparazione(Preparazione original, User newOwner, String newName) {
        Preparazione copy = new Preparazione(newName, original.getDescrizione(), newOwner);
        copy.setAutore(original.getAutore());
        copy.setTempoPreparazione(original.getTempoPreparazione());
        copy.setQuantitaRisultante(original.getQuantitaRisultante());
        copy.setUnitaMisuraRisultato(original.getUnitaMisuraRisultato());
        copy.setTags(new HashSet<>(original.getTags()));
        copy.setIngredienti(new ArrayList<>(original.getIngredienti()));
        copy.setIstruzioniAnticipo(new ArrayList<>(original.getIstruzioniAnticipo()));
        copy.setIstruzioniUltimo(new ArrayList<>(original.getIstruzioniUltimo()));
        
        preparazioni.add(copy);
        saveData();
        return copy;
    }
    
    // Statistiche
    public Map<String, Object> getStatistiche() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totaleRicette", ricette.size());
        stats.put("ricettePubblicate", getRicettePubblicate().size());
        stats.put("totalePreparazioni", preparazioni.size());
        stats.put("preparazioniPubblicate", getPreparazioniPubblicate().size());
        stats.put("totaleTag", getAllTags().size());
        stats.put("ingredientiBase", ingredientiBase.size());
        
        return stats;
    }
}