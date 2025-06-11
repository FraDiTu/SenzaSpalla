package com.catring.utils;

import com.catring.model.Evento;
import com.catring.model.Cliente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventoService {
    private static EventoService instance;
    private List<Evento> eventi;
    
    private EventoService() {
        this.eventi = new ArrayList<>();
        initializeTestData();
    }
    
    public static EventoService getInstance() {
        if (instance == null) {
            instance = new EventoService();
        }
        return instance;
    }
    
    private void initializeTestData() {
        Cliente cliente1 = new Cliente("C001", "Matrimonio Rossi", "privato", "mario.rossi@email.com");
        Cliente cliente2 = new Cliente("C002", "Azienda Tech", "azienda", "info@tech.com");
        
        Evento evento1 = new Evento("E001", LocalDate.of(2024, 6, 15), LocalDate.of(2024, 6, 15), 
                                   "Villa Reale", "singolo", "Matrimonio elegante");
        evento1.setCliente(cliente1);
        
        Evento evento2 = new Evento("E002", LocalDate.of(2024, 7, 10), LocalDate.of(2024, 7, 12), 
                                   "Centro Congressi", "complesso", "Conferenza aziendale");
        evento2.setCliente(cliente2);
        
        eventi.add(evento1);
        eventi.add(evento2);
    }
    
    public List<Evento> getEventi() {
        return new ArrayList<>(eventi);
    }
    
    public Evento getEventoById(String id) {
        return eventi.stream()
                .filter(evento -> evento.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}