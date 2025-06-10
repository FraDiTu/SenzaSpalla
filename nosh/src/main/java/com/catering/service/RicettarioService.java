package com.catering.service;

import com.catering.model.domain.Ricetta;
import com.catering.model.singleton.MenuRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Separation of Concerns - servizio dedicato al ricettario
 * Information Expert - gestisce le operazioni sulle ricette
 * Low Coupling - dipende solo dal repository
 */
public class RicettarioService {
    private final MenuRepository repository;
    
    private static RicettarioService instance;
    
    private RicettarioService() {
        this.repository = MenuRepository.getInstance();
    }
    
    public static RicettarioService getInstance() {
        if (instance == null) {
            synchronized (RicettarioService.class) {
                if (instance == null) {
                    instance = new RicettarioService();
                }
            }
        }
        return instance;
    }
    
    // Information Expert - sa come cercare ricette
    public List<Ricetta> searchRicette(String query) {
        if (query == null || query.trim().isEmpty()) {
            return repository.getAllRicette();
        }
        
        String lowerQuery = query.toLowerCase();
        return repository.getAllRicette().stream()
            .filter(r -> r.getNome().toLowerCase().contains(lowerQuery) ||
                        r.getDescrizione().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    public List<Ricetta> getAllRicette() {
        return repository.getAllRicette();
    }
    
    public Optional<Ricetta> getRicettaById(String id) {
        return repository.getRicettaById(id);
    }
    
    // Information Expert - sa come creare una nuova ricetta
    public Ricetta createRicetta(String nome, String descrizione, 
                                List<String> ingredienti, String tempoPreparazione, String autore) {
        String id = "r" + System.currentTimeMillis();
        Ricetta ricetta = new Ricetta(id, nome, descrizione, ingredienti, tempoPreparazione, autore);
        repository.saveRicetta(ricetta);
        return ricetta;
    }
}