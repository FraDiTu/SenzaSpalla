package com.saslab.controller;

import com.saslab.model.Event;
import com.saslab.model.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Controller per la gestione degli eventi
 * CORRETTO: Aggiunge gestione eventi ricorrenti e controllo chef assignment
 */
public class EventController implements Event.EventObserver {
    
    private final Map<String, Event> events;
    private final Map<String, List<Event.EventObserver>> eventObservers;
    private final Map<String, RecurringEventGroup> recurringEventGroups; // NUOVO
    
    public EventController() {
        this.events = new ConcurrentHashMap<>();
        this.eventObservers = new ConcurrentHashMap<>();
        this.recurringEventGroups = new ConcurrentHashMap<>(); // NUOVO
    }
    
    /**
     * Crea un nuovo evento singolo
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
     * NUOVO: Crea eventi ricorrenti
     */
    public RecurringEventGroup createRecurringEvents(String organizerId, String eventName, String location,
                                                    LocalDate startDate, LocalDate endDate,
                                                    Set<DayOfWeek> daysOfWeek, int expectedGuests,
                                                    RecurrencePattern pattern) {
        validateRecurringEventParameters(organizerId, eventName, location, startDate, endDate, daysOfWeek, expectedGuests);
        
        String groupId = generateGroupId();
        List<String> eventIds = new ArrayList<>();
        
        LocalDate currentDate = startDate;
        int eventCounter = 1;
        
        while (!currentDate.isAfter(endDate)) {
            if (daysOfWeek.contains(currentDate.getDayOfWeek())) {
                String eventId = generateEventId();
                String eventNameWithSequence = eventName + " #" + eventCounter;
                
                Event event = new Event(eventId, eventNameWithSequence, location, currentDate, organizerId);
                event.setExpectedGuests(expectedGuests);
                event.setRecurringGroupId(groupId); // NUOVO campo in Event
                event.addObserver(this);
                
                events.put(eventId, event);
                eventIds.add(eventId);
                eventCounter++;
            }
            
            currentDate = getNextDate(currentDate, pattern);
        }
        
        RecurringEventGroup group = new RecurringEventGroup(groupId, eventName, organizerId, 
                                                           startDate, endDate, daysOfWeek, eventIds, pattern);
        recurringEventGroups.put(groupId, group);
        
        return group;
    }
    
    private LocalDate getNextDate(LocalDate currentDate, RecurrencePattern pattern) {
        switch (pattern) {
            case DAILY:
                return currentDate.plusDays(1);
            case WEEKLY:
                return currentDate.plusWeeks(1);
            case MONTHLY:
                return currentDate.plusMonths(1);
            default:
                return currentDate.plusDays(1);
        }
    }
    
    /**
     * CORRETTO: Assegna uno chef con controllo di disponibilità
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
        
        // NUOVO: Controlla se lo chef è già assegnato ad altri eventi nella stessa data
        List<Event> conflictingEvents = checkChefScheduleConflicts(chefId, event.getStartDate());
        if (!conflictingEvents.isEmpty()) {
            String conflictDetails = conflictingEvents.stream()
                    .map(e -> e.getName() + " (" + e.getStartDate() + ")")
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Lo chef è già assegnato agli eventi: " + conflictDetails);
        }
        
        event.assignChef(chefId);
    }
    
    /**
     * NUOVO: Assegna chef a tutti gli eventi di un gruppo ricorrente
     */
    public void assignChefToRecurringEventGroup(String groupId, String chefId, String organizerId) {
        RecurringEventGroup group = recurringEventGroups.get(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Gruppo di eventi ricorrenti non trovato: " + groupId);
        }
        
        if (!group.getOrganizerId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore può assegnare lo chef al gruppo");
        }
        
        // Verifica conflitti per tutti gli eventi del gruppo
        List<String> conflictingEventIds = new ArrayList<>();
        for (String eventId : group.getEventIds()) {
            Event event = events.get(eventId);
            if (event != null) {
                List<Event> conflicts = checkChefScheduleConflicts(chefId, event.getStartDate());
                if (!conflicts.isEmpty()) {
                    conflictingEventIds.add(eventId);
                }
            }
        }
        
        if (!conflictingEventIds.isEmpty()) {
            throw new IllegalStateException("Lo chef ha conflitti con " + conflictingEventIds.size() + " eventi del gruppo");
        }
        
        // Assegna lo chef a tutti gli eventi del gruppo
        for (String eventId : group.getEventIds()) {
            Event event = events.get(eventId);
            if (event != null && event.getState() == Event.EventState.DRAFT) {
                event.assignChef(chefId);
            }
        }
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
     * NUOVO: Annulla tutti gli eventi di un gruppo ricorrente
     */
    public void cancelRecurringEventGroup(String groupId, String organizerId, String reason) {
        RecurringEventGroup group = recurringEventGroups.get(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Gruppo di eventi ricorrenti non trovato: " + groupId);
        }
        
        if (!group.getOrganizerId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore può annullare il gruppo");
        }
        
        for (String eventId : group.getEventIds()) {
            Event event = events.get(eventId);
            if (event != null && event.getState() != Event.EventState.COMPLETED) {
                cancelEvent(eventId, organizerId, reason + " (Cancellazione gruppo)");
            }
        }
        
        group.setCancelled(true);
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
        
        // NUOVO: Se fa parte di un gruppo ricorrente, rimuovilo dal gruppo
        if (event.getRecurringGroupId() != null) {
            RecurringEventGroup group = recurringEventGroups.get(event.getRecurringGroupId());
            if (group != null) {
                group.removeEvent(eventId);
                if (group.getEventIds().isEmpty()) {
                    recurringEventGroups.remove(event.getRecurringGroupId());
                }
            }
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
     * NUOVO: Ottiene un gruppo di eventi ricorrenti
     */
    public RecurringEventGroup getRecurringEventGroup(String groupId) {
        return recurringEventGroups.get(groupId);
    }
    
    /**
     * NUOVO: Ottiene tutti i gruppi di eventi ricorrenti
     */
    public List<RecurringEventGroup> getAllRecurringEventGroups() {
        return new ArrayList<>(recurringEventGroups.values());
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
     * NUOVO: Cerca eventi ricorrenti per nome
     */
    public List<RecurringEventGroup> searchRecurringEventsByName(String namePattern) {
        String pattern = namePattern.toLowerCase();
        return recurringEventGroups.values().stream()
                .filter(group -> group.getBaseName().toLowerCase().contains(pattern))
                .sorted((g1, g2) -> g2.getStartDate().compareTo(g1.getStartDate()))
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
        long recurringGroupsCount = recurringEventGroups.size();
        
        stats.put("total", events.size());
        stats.put("draft", (int) draftCount);
        stats.put("inProgress", (int) inProgressCount);
        stats.put("completed", (int) completedCount);
        stats.put("cancelled", (int) cancelledCount);
        stats.put("recurringGroups", (int) recurringGroupsCount);
        
        return stats;
    }
    
    /**
     * CORRETTO: Verifica conflitti di programmazione per uno chef
     */
    public List<Event> checkChefScheduleConflicts(String chefId, LocalDate date) {
        return events.values().stream()
                .filter(event -> chefId.equals(event.getAssignedChefId()))
                .filter(event -> event.getState() == Event.EventState.IN_PROGRESS || 
                               event.getState() == Event.EventState.DRAFT)
                .filter(event -> event.getStartDate().equals(date) || 
                               (!event.getStartDate().isAfter(date) && !event.getEndDate().isBefore(date)))
                .collect(Collectors.toList());
    }
    
    /**
     * NUOVO: Verifica conflitti di programmazione per un intervallo di date
     */
    public List<Event> checkChefScheduleConflicts(String chefId, LocalDate startDate, LocalDate endDate) {
        return events.values().stream()
                .filter(event -> chefId.equals(event.getAssignedChefId()))
                .filter(event -> event.getState() == Event.EventState.IN_PROGRESS || 
                               event.getState() == Event.EventState.DRAFT)
                .filter(event -> !(event.getEndDate().isBefore(startDate) || event.getStartDate().isAfter(endDate)))
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
        
        // NUOVO: Se l'evento fa parte di un gruppo ricorrente, aggiorna anche il gruppo
        if (event.getRecurringGroupId() != null) {
            RecurringEventGroup group = recurringEventGroups.get(event.getRecurringGroupId());
            if (group != null) {
                group.updateEventStatus(event.getId(), event.getState());
            }
        }
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
    
    private String generateGroupId() {
        return "GRP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
    
    private void validateRecurringEventParameters(String organizerId, String eventName, String location,
                                                 LocalDate startDate, LocalDate endDate, 
                                                 Set<DayOfWeek> daysOfWeek, int expectedGuests) {
        validateEventCreationParameters(organizerId, eventName, location, startDate, expectedGuests);
        
        if (endDate == null) {
            throw new IllegalArgumentException("La data di fine è obbligatoria per eventi ricorrenti");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La data di fine non può essere precedente a quella di inizio");
        }
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            throw new IllegalArgumentException("Specificare almeno un giorno della settimana");
        }
    }
    
    /**
     * Ottiene il numero totale di eventi
     */
    public int getTotalEventCount() {
        return events.size();
    }
    
    /**
     * NUOVO: Ottiene il numero totale di gruppi ricorrenti
     */
    public int getTotalRecurringGroupCount() {
        return recurringEventGroups.size();
    }
    
    // NUOVO: Enum per pattern di ricorrenza
    public enum RecurrencePattern {
        DAILY("Giornaliero"),
        WEEKLY("Settimanale"), 
        MONTHLY("Mensile");
        
        private final String displayName;
        
        RecurrencePattern(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
// NOTA: Il modello Event deve essere aggiornato per includere:
// - private String recurringGroupId;
// - public String getRecurringGroupId() { return recurringGroupId; }
// - public void setRecurringGroupId(String recurringGroupId) { this.recurringGroupId = recurringGroupId; }
    public static class RecurringEventGroup {
        private String groupId;
        private String baseName;
        private String organizerId;
        private LocalDate startDate;
        private LocalDate endDate;
        private Set<DayOfWeek> daysOfWeek;
        private List<String> eventIds;
        private RecurrencePattern pattern;
        private boolean cancelled;
        private Map<String, Event.EventState> eventStates;
        
        public RecurringEventGroup(String groupId, String baseName, String organizerId,
                                 LocalDate startDate, LocalDate endDate, Set<DayOfWeek> daysOfWeek,
                                 List<String> eventIds, RecurrencePattern pattern) {
            this.groupId = groupId;
            this.baseName = baseName;
            this.organizerId = organizerId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.daysOfWeek = new HashSet<>(daysOfWeek);
            this.eventIds = new ArrayList<>(eventIds);
            this.pattern = pattern;
            this.cancelled = false;
            this.eventStates = new HashMap<>();
        }
        
        // Getters
        public String getGroupId() { return groupId; }
        public String getBaseName() { return baseName; }
        public String getOrganizerId() { return organizerId; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public Set<DayOfWeek> getDaysOfWeek() { return new HashSet<>(daysOfWeek); }
        public List<String> getEventIds() { return new ArrayList<>(eventIds); }
        public RecurrencePattern getPattern() { return pattern; }
        public boolean isCancelled() { return cancelled; }
        
        // Setters
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
        
        // Business methods
        public void removeEvent(String eventId) {
            eventIds.remove(eventId);
            eventStates.remove(eventId);
        }
        
        public void updateEventStatus(String eventId, Event.EventState state) {
            eventStates.put(eventId, state);
        }
        
        public int getCompletedEventsCount() {
            return (int) eventStates.values().stream()
                    .filter(state -> state == Event.EventState.COMPLETED)
                    .count();
        }
        
        public int getCancelledEventsCount() {
            return (int) eventStates.values().stream()
                    .filter(state -> state == Event.EventState.CANCELLED)
                    .count();
        }
        
        public double getCompletionPercentage() {
            if (eventIds.isEmpty()) return 100.0;
            return (double) getCompletedEventsCount() / eventIds.size() * 100.0;
        }
        
        @Override
        public String toString() {
            return String.format("RecurringEventGroup{id='%s', name='%s', events=%d, pattern=%s}", 
                               groupId, baseName, eventIds.size(), pattern);
        }
    }
}