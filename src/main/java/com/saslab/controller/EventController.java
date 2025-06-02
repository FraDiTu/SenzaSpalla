package com.saslab.controller;

import com.saslab.model.Event;
import com.saslab.model.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Controller per la gestione degli eventi
 * Implementa pattern Controller (GRASP) per coordinare le operazioni sugli eventi
 * Implementa pattern Observer per notificare i cambiamenti
 */
public class EventController implements Event.EventObserver {
    
    private final Map<String, Event> events;
    private final Map<String, List<Event.EventObserver>> eventObservers;
    
    public EventController() {
        this.events = new ConcurrentHashMap<>();
        this.eventObservers = new ConcurrentHashMap<>();
    }
    
    /**
     * Crea un nuovo evento
     */
    public String createEvent(String organizerId, String eventName, String location, 
                             LocalDate startDate, int expectedGuests) {
        validateEventCreationParameters(organizerId, eventName, location, startDate, expectedGuests);
        
        String eventId = generateEventId();
        Event event = new Event(eventId, eventName, location, startDate, organizerId);
        event.setExpectedGuests(expectedGuests);
        
        // Registra questo controller come observer
        event.addObserver(this);
        
        events.put(eventId, event);
        return eventId;
    }
    
    /**
     * Assegna uno chef ad un evento
     */
    public void assignChefToEvent(String eventId, String chefId, String organizerId) {
        Event event = getEventOrThrow(eventId);
        
        // Verifica che solo l'organizzatore possa assegnare lo chef
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore dell'evento può assegnare lo chef");
        }
        
        if (event.getState() != Event.EventState.DRAFT) {
            throw new IllegalStateException("È possibile assegnare lo chef solo agli eventi in bozza");
        }
        
        event.assignChef(chefId);
    }
    
    /**
     * Aggiunge un servizio ad un evento
     */
    public String addServiceToEvent(String eventId, LocalDate date, LocalTime startTime, 
                                   LocalTime endTime, Service.ServiceType serviceType) {
        Event event = getEventOrThrow(eventId);
        
        if (event.getState() == Event.EventState.COMPLETED || event.getState() == Event.EventState.CANCELLED) {
            throw new IllegalStateException("Non è possibile aggiungere servizi ad eventi terminati");
        }
        
        String serviceId = generateServiceId();
        Service service = new Service(serviceId, eventId, date, startTime, endTime, serviceType);
        
        event.addService(service);
        return serviceId;
    }
    
    /**
     * Aggiorna i dettagli di un evento
     */
    public void updateEventDetails(String eventId, String organizerId, String newName, 
                                  String newLocation, String notes) {
        Event event = getEventOrThrow(eventId);
        
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore può modificare i dettagli dell'evento");
        }
        
        if (newName != null && !newName.trim().isEmpty()) {
            event.setName(newName);
        }
        
        if (newLocation != null && !newLocation.trim().isEmpty()) {
            event.setLocation(newLocation);
        }
        
        if (notes != null) {
            event.setNotes(notes);
        }
    }
    
    /**
     * Avvia un evento (passa da DRAFT a IN_PROGRESS)
     */
    public void startEvent(String eventId, String organizerId) {
        Event event = getEventOrThrow(eventId);
        
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore può avviare l'evento");
        }
        
        event.startEvent();
    }
    
    /**
     * Completa un evento
     */
    public void completeEvent(String eventId, String organizerId, String finalNotes) {
        Event event = getEventOrThrow(eventId);
        
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore può completare l'evento");
        }
        
        if (finalNotes != null && !finalNotes.trim().isEmpty()) {
            event.setNotes(event.getNotes() + "\n\nNote finali: " + finalNotes);
        }
        
        event.completeEvent();
    }
    
    /**
     * Annulla un evento
     */
    public void cancelEvent(String eventId, String organizerId, String reason) {
        Event event = getEventOrThrow(eventId);
        
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore può annullare l'evento");
        }
        
        if (reason != null && !reason.trim().isEmpty()) {
            String currentNotes = event.getNotes() != null ? event.getNotes() : "";
            event.setNotes(currentNotes + "\n\nMotivo annullamento: " + reason);
        }
        
        event.cancelEvent();
    }
    
    /**
     * Elimina un evento (solo se in bozza)
     */
    public boolean deleteEvent(String eventId, String organizerId) {
        Event event = events.get(eventId);
        if (event == null) {
            return false;
        }
        
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore può eliminare l'evento");
        }
        
        if (!event.canBeDeleted()) {
            throw new IllegalStateException("L'evento non può essere eliminato nel suo stato attuale");
        }
        
        event.removeObserver(this);
        events.remove(eventId);
        eventObservers.remove(eventId);
        return true;
    }
    
    /**
     * Ottiene un evento per ID
     */
    public Event getEvent(String eventId) {
        return events.get(eventId);
    }
    
    /**
     * Ottiene tutti gli eventi di un organizzatore
     */
    public List<Event> getEventsByOrganizer(String organizerId) {
        return events.values().stream()
                .filter(event -> event.getOrganizerId().equals(organizerId))
                .sorted((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene eventi per stato
     */
    public List<Event> getEventsByState(Event.EventState state) {
        return events.values().stream()
                .filter(event -> event.getState() == state)
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene eventi in un intervallo di date
     */
    public List<Event> getEventsInDateRange(LocalDate startDate, LocalDate endDate) {
        return events.values().stream()
                .filter(event -> !event.getStartDate().isBefore(startDate) && 
                               !event.getStartDate().isAfter(endDate))
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene eventi attivi (in corso)
     */
    public List<Event> getActiveEvents() {
        return getEventsByState(Event.EventState.IN_PROGRESS);
    }
    
    /**
     * Ottiene eventi assegnati ad uno chef
     */
    public List<Event> getEventsByChef(String chefId) {
        return events.values().stream()
                .filter(event -> chefId.equals(event.getAssignedChefId()))
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Cerca eventi per nome
     */
    public List<Event> searchEventsByName(String namePattern) {
        String pattern = namePattern.toLowerCase();
        return events.values().stream()
                .filter(event -> event.getName().toLowerCase().contains(pattern))
                .sorted((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene statistiche sugli eventi
     */
    public Map<String, Integer> getEventStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        long draftCount = events.values().stream().filter(e -> e.getState() == Event.EventState.DRAFT).count();
        long inProgressCount = events.values().stream().filter(e -> e.getState() == Event.EventState.IN_PROGRESS).count();
        long completedCount = events.values().stream().filter(e -> e.getState() == Event.EventState.COMPLETED).count();
        long cancelledCount = events.values().stream().filter(e -> e.getState() == Event.EventState.CANCELLED).count();
        
        stats.put("total", events.size());
        stats.put("draft", (int) draftCount);
        stats.put("inProgress", (int) inProgressCount);
        stats.put("completed", (int) completedCount);
        stats.put("cancelled", (int) cancelledCount);
        
        return stats;
    }
    
    /**
     * Verifica conflitti di programmazione per uno chef
     */
    public List<Event> checkChefScheduleConflicts(String chefId, LocalDate date) {
        return events.values().stream()
                .filter(event -> chefId.equals(event.getAssignedChefId()))
                .filter(event -> event.getState() == Event.EventState.IN_PROGRESS)
                .filter(event -> !event.getStartDate().isAfter(date) && !event.getEndDate().isBefore(date))
                .collect(Collectors.toList());
    }
    
    /**
     * Registra un observer per un evento specifico
     */
    public void addEventObserver(String eventId, Event.EventObserver observer) {
        Event event = events.get(eventId);
        if (event != null) {
            event.addObserver(observer);
            
            eventObservers.computeIfAbsent(eventId, k -> new ArrayList<>()).add(observer);
        }
    }
    
    /**
     * Rimuove un observer per un evento specifico
     */
    public void removeEventObserver(String eventId, Event.EventObserver observer) {
        Event event = events.get(eventId);
        if (event != null) {
            event.removeObserver(observer);
            
            List<Event.EventObserver> observers = eventObservers.get(eventId);
            if (observers != null) {
                observers.remove(observer);
            }
        }
    }
    
    // Implementazione dell'interfaccia EventObserver
    @Override
    public void onEventUpdated(Event event) {
        // Gestisce le notifiche di aggiornamento degli eventi
        System.out.printf("[EventController] Evento aggiornato: %s - Stato: %s%n", 
                         event.getName(), event.getState());
        
        // Qui si potrebbero implementare altre logiche, come notifiche via email, 
        // aggiornamenti del database, ecc.
    }
    
    // Metodi di utilità privati
    private Event getEventOrThrow(String eventId) {
        Event event = events.get(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Evento non trovato: " + eventId);
        }
        return event;
    }
    
    private String generateEventId() {
        return "EVT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateServiceId() {
        return "SRV_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void validateEventCreationParameters(String organizerId, String eventName, 
                                                String location, LocalDate startDate, int expectedGuests) {
        if (organizerId == null || organizerId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID dell'organizzatore è obbligatorio");
        }
        if (eventName == null || eventName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'evento è obbligatorio");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("La location è obbligatoria");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("La data di inizio è obbligatoria");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La data di inizio non può essere nel passato");
        }
        if (expectedGuests < 0) {
            throw new IllegalArgumentException("Il numero di ospiti attesi non può essere negativo");
        }
    }
    
    /**
     * Ottiene il numero totale di eventi
     */
    public int getTotalEventCount() {
        return events.size();
    }
}