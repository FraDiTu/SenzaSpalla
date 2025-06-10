package it.catering.catring.controller;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.managers.EventoManager;
import it.catering.catring.model.states.StatoEvento;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class EventoController {
    private EventoManager eventoManager;
    private User currentUser;
    
    public EventoController() {
        this.eventoManager = EventoManager.getInstance();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    // CRUD Eventi
    public Evento createEvento(String nome, LocalDate dataInizio, LocalDate dataFine, 
                              String luogo, Cliente cliente) {
        if (!(currentUser instanceof Organizzatore organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono creare eventi");
        }
        
        return eventoManager.createEvento(nome, dataInizio, dataFine, luogo, cliente, organizzatore);
    }
    
    public void updateEvento(Evento evento) {
        if (!(currentUser instanceof Organizzatore) || 
            !evento.getOrganizzatore().equals(currentUser)) {
            throw new IllegalStateException("Solo l'organizzatore responsabile può modificare l'evento");
        }
        
        eventoManager.updateEvento(evento);
    }
    
    public void deleteEvento(Evento evento) {
        if (!(currentUser instanceof Organizzatore) || 
            !evento.getOrganizzatore().equals(currentUser)) {
            throw new IllegalStateException("Solo l'organizzatore responsabile può eliminare l'evento");
        }
        
        eventoManager.deleteEvento(evento);
    }
    
    public void assegnaChef(Evento evento, Chef chef) {
        if (!(currentUser instanceof Organizzatore) || 
            !evento.getOrganizzatore().equals(currentUser)) {
            throw new IllegalStateException("Solo l'organizzatore responsabile può assegnare lo chef");
        }
        
        eventoManager.assegnaChef(evento, chef);
    }
    
    public Servizio createServizio(String tipo, LocalTime inizio, LocalTime fine, int numeroPersone) {
        Servizio servizio = new Servizio(tipo, inizio, fine, numeroPersone);
        return servizio;
    }
    
    public void aggiungiServizio(Evento evento, Servizio servizio) {
        if (!(currentUser instanceof Organizzatore) || 
            !evento.getOrganizzatore().equals(currentUser)) {
            throw new IllegalStateException("Solo l'organizzatore responsabile può aggiungere servizi");
        }
        
        eventoManager.aggiungiServizio(evento, servizio);
    }
    
    public void rimuoviServizio(Evento evento, Servizio servizio) {
        if (!(currentUser instanceof Organizzatore) || 
            !evento.getOrganizzatore().equals(currentUser)) {
            throw new IllegalStateException("Solo l'organizzatore responsabile può rimuovere servizi");
        }
        
        eventoManager.rimuoviServizio(evento, servizio);
    }
    
    public void assegnaMenuAServizio(Servizio servizio, Menu menu) {
        if (!(currentUser instanceof Chef) && !(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo chef e organizzatori possono assegnare menu ai servizi");
        }
        
        servizio.setMenu(menu);
    }
    
    public void approvaMenuEvento(Evento evento) {
        if (!(currentUser instanceof Organizzatore) || 
            !evento.getOrganizzatore().equals(currentUser)) {
            throw new IllegalStateException("Solo l'organizzatore responsabile può approvare i menu");
        }
        
        eventoManager.approvaMenuEvento(evento);
    }
    
    public void completaEvento(Evento evento, String noteFinali) {
        if (!(currentUser instanceof Organizzatore) || 
            !evento.getOrganizzatore().equals(currentUser)) {
            throw new IllegalStateException("Solo l'organizzatore responsabile può completare l'evento");
        }
        
        eventoManager.completaEvento(evento, noteFinali);
    }
    
    public void annullaEvento(Evento evento, String motivo) {
        if (!(currentUser instanceof Organizzatore) || 
            !evento.getOrganizzatore().equals(currentUser)) {
            throw new IllegalStateException("Solo l'organizzatore responsabile può annullare l'evento");
        }
        
        eventoManager.annullaEvento(evento, motivo);
    }
    
    // CRUD Clienti
    public Cliente createCliente(String nome, String tipo, String contatti) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono creare clienti");
        }
        
        return eventoManager.createCliente(nome, tipo, contatti);
    }
    
    public void updateCliente(Cliente cliente) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono modificare clienti");
        }
        
        eventoManager.updateCliente(cliente);
    }
    
    public void deleteCliente(Cliente cliente) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono eliminare clienti");
        }
        
        eventoManager.deleteCliente(cliente);
    }
    
    // Query methods
    public List<Evento> getEventiForCurrentUser() {
        if (currentUser instanceof Organizzatore organizzatore) {
            return eventoManager.getEventiPerOrganizzatore(organizzatore);
        } else if (currentUser instanceof Chef chef) {
            return eventoManager.getEventiPerChef(chef);
        }
        return List.of();
    }
    
    public List<Evento> getEventiPerPeriodo(LocalDate inizio, LocalDate fine) {
        return eventoManager.getEventiPerPeriodo(inizio, fine);
    }
    
    public List<Evento> getEventiPerStato(StatoEvento stato) {
        return eventoManager.getEventiPerStato(stato);
    }
    
    public List<Cliente> getAllClienti() {
        return eventoManager.getAllClienti();
    }
    
    public List<Evento> getAllEventi() {
        if (!(currentUser instanceof Organizzatore)) {
            return getEventiForCurrentUser();
        }
        return eventoManager.getAllEventi();
    }
    
    public Map<String, Object> getStatisticheEventi() {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono visualizzare le statistiche");
        }
        
        return eventoManager.getStatisticheEventi();
    }
}