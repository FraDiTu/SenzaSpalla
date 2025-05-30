package com.saslab.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe che rappresenta un Evento nel sistema Cat &amp; Ring
 * Implementa pattern State per gestire gli stati dell'evento
 * Implementa pattern Observer per notificare i cambiamenti
 */
public class Event {
    
    public enum EventType {
        SINGLE("Singolo"),
        COMPLEX("Complesso"),
        RECURRING("Ricorrente");
        
        private final String displayName;
        
        EventType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum EventState {
        DRAFT("Bozza"),
        IN_PROGRESS("In Corso"),
        COMPLETED("Completato"),
        CANCELLED("Annullato");
        
        private final String displayName;
        
        EventState(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String name;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private EventType type;
    private EventState state;
    private String organizerId;
    private String assignedChefId;
    private String clientId;
    private int expectedGuests;
    private String notes;
    private List<Service> services;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private List<EventObserver> observers;
    
    // Constructor
    public Event(String id, String name, String location, LocalDate startDate, String organizerId) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        this.location = Objects.requireNonNull(location, "La location non può essere null");
        this.startDate = Objects.requireNonNull(startDate, "La data di inizio non può essere null");
        this.organizerId = Objects.requireNonNull(organizerId, "L'organizzatore non può essere null");
        
        this.endDate = startDate; // Default: evento di un giorno
        this.type = EventType.SINGLE; // Default
        this.state = EventState.DRAFT;
        this.services = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.expectedGuests = 0;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public EventType getType() { return type; }
    public EventState getState() { return state; }
    public String getOrganizerId() { return organizerId; }
    public String getAssignedChefId() { return assignedChefId; }
    public String getClientId() { return clientId; }
    public int getExpectedGuests() { return expectedGuests; }
    public String getNotes() { return notes; }
    public List<Service> getServices() { return new ArrayList<>(services); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }
    
    // Setters con validazione
    public void setName(String name) {
        if (state != EventState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare il nome di un evento non in bozza");
        }
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        updateLastModified();
        notifyObservers();
    }
    
    public void setLocation(String location) {
        if (state == EventState.COMPLETED || state == EventState.CANCELLED) {
            throw new IllegalStateException("Non è possibile modificare la location di un evento terminato");
        }
        this.location = Objects.requireNonNull(location, "La location non può essere null");
        updateLastModified();
        notifyObservers();
    }
    
    public void setEndDate(LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La data di fine non può essere precedente a quella di inizio");
        }
        this.endDate = endDate;
        
        // Aggiorna il tipo di evento in base alla durata
        if (startDate.equals(endDate)) {
            this.type = services.size() > 1 ? EventType.COMPLEX : EventType.SINGLE;
        } else {
            this.type = EventType.COMPLEX;
        }
        
        updateLastModified();
        notifyObservers();
    }
    
    public void setExpectedGuests(int expectedGuests) {
        if (expectedGuests < 0) {
            throw new IllegalArgumentException("Il numero di ospiti non può essere negativo");
        }
        this.expectedGuests = expectedGuests;
        updateLastModified();
        notifyObservers();
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        updateLastModified();
    }
    
    public void setClientId(String clientId) {
        if (state != EventState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare il cliente di un evento non in bozza");
        }
        this.clientId = clientId;
        updateLastModified();
    }
    
    // Business methods
    public void assignChef(String chefId) {
        if (state == EventState.COMPLETED || state == EventState.CANCELLED) {
            throw new IllegalStateException("Non è possibile assegnare uno chef a un evento terminato");
        }
        this.assignedChefId = Objects.requireNonNull(chefId, "L'ID dello chef non può essere null");
        updateLastModified();
        notifyObservers();
    }
    
    public void addService(Service service) {
        if (state == EventState.COMPLETED || state == EventState.CANCELLED) {
            throw new IllegalStateException("Non è possibile aggiungere servizi a un evento terminato");
        }
        
        Objects.requireNonNull(service, "Il servizio non può essere null");
        
        // Verifica che il servizio sia nell'intervallo dell'evento
        if (service.getDate().isBefore(startDate) || service.getDate().isAfter(endDate)) {
            throw new IllegalArgumentException("Il servizio deve essere nell'intervallo dell'evento");
        }
        
        services.add(service);
        
        // Aggiorna il tipo se necessario
        if (services.size() > 1) {
            type = EventType.COMPLEX;
        }
        
        updateLastModified();
        notifyObservers();
    }
    
    public boolean removeService(String serviceId) {
        if (state == EventState.COMPLETED || state == EventState.CANCELLED) {
            throw new IllegalStateException("Non è possibile rimuovere servizi da un evento terminato");
        }
        
        boolean removed = services.removeIf(service -> service.getId().equals(serviceId));
        if (removed) {
            updateLastModified();
            notifyObservers();
        }
        return removed;
    }
    
    // State management methods
    public void startEvent() {
        if (state != EventState.DRAFT) {
            throw new IllegalStateException("Solo gli eventi in bozza possono essere avviati");
        }
        if (assignedChefId == null) {
            throw new IllegalStateException("Deve essere assegnato uno chef prima di avviare l'evento");
        }
        if (services.isEmpty()) {
            throw new IllegalStateException("L'evento deve avere almeno un servizio");
        }
        
        state = EventState.IN_PROGRESS;
        updateLastModified();
        notifyObservers();
    }
    
    public void completeEvent() {
        if (state != EventState.IN_PROGRESS) {
            throw new IllegalStateException("Solo gli eventi in corso possono essere completati");
        }
        
        state = EventState.COMPLETED;
        updateLastModified();
        notifyObservers();
    }
    
    public void cancelEvent() {
        if (state == EventState.COMPLETED) {
            throw new IllegalStateException("Non è possibile annullare un evento completato");
        }
        
        state = EventState.CANCELLED;
        updateLastModified();
        notifyObservers();
    }
    
    // Information Expert methods
    public boolean canBeModified() {
        return state == EventState.DRAFT;
    }
    
    public boolean canBeDeleted() {
        return state == EventState.DRAFT;
    }
    
    public boolean isActive() {
        return state == EventState.IN_PROGRESS;
    }
    
    public boolean isCompleted() {
        return state == EventState.COMPLETED;
    }
    
    public int getDurationInDays() {
        return (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;
    }
    
    public Service getServiceById(String serviceId) {
        return services.stream()
                .filter(service -> service.getId().equals(serviceId))
                .findFirst()
                .orElse(null);
    }
    
    // Observer pattern methods
    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers() {
        for (EventObserver observer : observers) {
            observer.onEventUpdated(this);
        }
    }
    
    private void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event event = (Event) obj;
        return Objects.equals(id, event.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Event{id='%s', name='%s', location='%s', date=%s, type=%s, state=%s}", 
                           id, name, location, startDate, type, state);
    }
    
    // Observer interface
    public interface EventObserver {
        void onEventUpdated(Event event);
    }
}