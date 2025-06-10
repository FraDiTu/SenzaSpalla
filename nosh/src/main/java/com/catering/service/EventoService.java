package com.catering.service;

import com.catering.model.domain.Evento;
import com.catering.model.singleton.MenuRepository;
import java.util.List;
import java.util.Optional;

/**
 * Separation of Concerns - servizio dedicato agli eventi
 * Information Expert - gestisce le operazioni sugli eventi
 * Low Coupling - dipende solo dal repository
 */
public class EventoService {
    private final MenuRepository repository;
    
    private static EventoService instance;
    
    private EventoService() {
        this.repository = MenuRepository.getInstance();
    }
    
    public static EventoService getInstance() {
        if (instance == null) {
            synchronized (EventoService.class) {
                if (instance == null) {
                    instance = new EventoService();
                }
            }
        }
        return instance;
    }
    
    public List<Evento> getAllEventi() {
        return repository.getAllEventi();
    }
    
    public Optional<Evento> getEventoById(String id) {
        return repository.getEventoById(id);
    }
}