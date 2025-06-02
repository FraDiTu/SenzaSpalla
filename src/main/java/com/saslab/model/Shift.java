package com.saslab.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Classe che rappresenta un Turno nel sistema Cat & Ring
 * Implementa pattern State per gestire gli stati del turno
 * Implementa pattern Observer per notificare i cambiamenti
 */
public class Shift {
    
    public enum ShiftType {
        PREPARATORY("Preparatorio", "Turno di preparazione in cucina"),
        SERVICE("Servizio", "Turno di servizio durante l'evento");
        
        private final String displayName;
        private final String description;
        
        ShiftType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    public enum ShiftState {
        SCHEDULED("Programmato"),
        MODIFIABLE("Modificabile"),
        LOCKED("Bloccato"),
        IN_PROGRESS("In Corso"),
        COMPLETED("Completato"),
        CANCELLED("Annullato");
        
        private final String displayName;
        
        ShiftState(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    private String id;
    private ShiftType type;
    private ShiftState state;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String serviceId; // Per turni di servizio
    private String groupId; // Per raggruppamenti
    private int maxCapacity; // Numero massimo di persone
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    
    // Disponibilità confermate
    private Map<String, Availability> availabilities;
    
    // Assegnazioni confermate
    private Set<String> assignedStaffIds;
    
    // Observer pattern
    private List<ShiftObserver> observers;
    
    // Constructor per turno preparatorio
    public Shift(String id, LocalDate date, LocalTime startTime, LocalTime endTime, String location) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.date = Objects.requireNonNull(date, "La data non può essere null");
        this.startTime = Objects.requireNonNull(startTime, "L'orario di inizio non può essere null");
        this.endTime = Objects.requireNonNull(endTime, "L'orario di fine non può essere null");
        this.location = Objects.requireNonNull(location, "La location non può essere null");
        
        validateTimes();
        
        this.type = ShiftType.PREPARATORY;
        this.state = ShiftState.SCHEDULED;
        this.maxCapacity = 10; // Default
        this.availabilities = new HashMap<>();
        this.assignedStaffIds = new HashSet<>();
        this.observers = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }
    
    // Constructor per turno di servizio
    public Shift(String id, String serviceId, LocalDate date, LocalTime startTime, 
                 LocalTime endTime, String location) {
        this(id, date, startTime, endTime, location);
        this.serviceId = Objects.requireNonNull(serviceId, "Il service ID non può essere null");
        this.type = ShiftType.SERVICE;
    }
    
    // Getters
    public String getId() { return id; }
    public ShiftType getType() { return type; }
    public ShiftState getState() { return state; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getServiceId() { return serviceId; }
    public String getGroupId() { return groupId; }
    public int getMaxCapacity() { return maxCapacity; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }
    public Map<String, Availability> getAvailabilities() { return new HashMap<>(availabilities); }
    public Set<String> getAssignedStaffIds() { return new HashSet<>(assignedStaffIds); }
    
    // Setters con validazione
    public void setDate(LocalDate date) {
        if (!canBeModified()) {
            throw new IllegalStateException("Il turno non può essere modificato nel suo stato attuale");
        }
        this.date = Objects.requireNonNull(date, "La data non può essere null");
        updateLastModified();
        notifyObservers();
    }
    
    public void setStartTime(LocalTime startTime) {
        if (!canBeModified()) {
            throw new IllegalStateException("Il turno non può essere modificato nel suo stato attuale");
        }
        this.startTime = Objects.requireNonNull(startTime, "L'orario di inizio non può essere null");
        validateTimes();
        updateLastModified();
        notifyObservers();
    }
    
    public void setEndTime(LocalTime endTime) {
        if (!canBeModified()) {
            throw new IllegalStateException("Il turno non può essere modificato nel suo stato attuale");
        }
        this.endTime = Objects.requireNonNull(endTime, "L'orario di fine non può essere null");
        validateTimes();
        updateLastModified();
        notifyObservers();
    }
    
    public void setLocation(String location) {
        if (!canBeModified()) {
            throw new IllegalStateException("Il turno non può essere modificato nel suo stato attuale");
        }
        this.location = Objects.requireNonNull(location, "La location non può essere null");
        updateLastModified();
        notifyObservers();
    }
    
    public void setGroupId(String groupId) {
        if (state != ShiftState.SCHEDULED) {
            throw new IllegalStateException("Il gruppo può essere impostato solo per turni programmati");
        }
        this.groupId = groupId;
        updateLastModified();
    }
    
    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("La capacità massima deve essere positiva");
        }
        if (!canBeModified()) {
            throw new IllegalStateException("Il turno non può essere modificato nel suo stato attuale");
        }
        this.maxCapacity = maxCapacity;
        updateLastModified();
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        updateLastModified();
    }
    
    // Business methods
    public void addAvailability(String staffId, boolean isAvailable) {
        if (state == ShiftState.CANCELLED || state == ShiftState.COMPLETED) {
            throw new IllegalStateException("Non è possibile aggiungere disponibilità a turni terminati");
        }
        
        Availability availability = new Availability(staffId, isAvailable);
        availabilities.put(staffId, availability);
        
        // Se ci sono disponibilità, il turno diventa bloccato per modifiche
        if (state == ShiftState.SCHEDULED && !availabilities.isEmpty()) {
            state = ShiftState.LOCKED;
        }
        
        updateLastModified();
        notifyObservers();
    }
    
    public void removeAvailability(String staffId) {
        if (assignedStaffIds.contains(staffId)) {
            throw new IllegalStateException("Non è possibile rimuovere la disponibilità di personale assegnato");
        }
        
        availabilities.remove(staffId);
        
        // Se non ci sono più disponibilità, il turno può tornare modificabile
        if (availabilities.isEmpty() && state == ShiftState.LOCKED) {
            state = ShiftState.MODIFIABLE;
        }
        
        updateLastModified();
        notifyObservers();
    }
    
    public void assignStaff(String staffId) {
        if (!availabilities.containsKey(staffId) || !availabilities.get(staffId).isAvailable()) {
            throw new IllegalStateException("Il personale deve aver dato disponibilità per essere assegnato");
        }
        
        if (assignedStaffIds.size() >= maxCapacity) {
            throw new IllegalStateException("Capacità massima del turno raggiunta");
        }
        
        assignedStaffIds.add(staffId);
        availabilities.get(staffId).setConfirmed(true);
        
        updateLastModified();
        notifyObservers();
    }
    
    public void unassignStaff(String staffId) {
        if (state == ShiftState.IN_PROGRESS || state == ShiftState.COMPLETED) {
            throw new IllegalStateException("Non è possibile rimuovere assegnazioni da turni in corso o completati");
        }
        
        assignedStaffIds.remove(staffId);
        if (availabilities.containsKey(staffId)) {
            availabilities.get(staffId).setConfirmed(false);
        }
        
        updateLastModified();
        notifyObservers();
    }
    
    public void startShift() {
        if (state != ShiftState.LOCKED) {
            throw new IllegalStateException("Solo i turni bloccati possono essere avviati");
        }
        
        if (assignedStaffIds.isEmpty()) {
            throw new IllegalStateException("Il turno deve avere almeno una persona assegnata");
        }
        
        state = ShiftState.IN_PROGRESS;
        updateLastModified();
        notifyObservers();
    }
    
    public void completeShift() {
        if (state != ShiftState.IN_PROGRESS) {
            throw new IllegalStateException("Solo i turni in corso possono essere completati");
        }
        
        state = ShiftState.COMPLETED;
        updateLastModified();
        notifyObservers();
    }
    
    public void cancelShift(String reason) {
        if (state == ShiftState.COMPLETED) {
            throw new IllegalStateException("Non è possibile annullare un turno completato");
        }
        
        state = ShiftState.CANCELLED;
        if (reason != null && !reason.trim().isEmpty()) {
            String currentNotes = notes != null ? notes : "";
            notes = currentNotes + "\nMotivo annullamento: " + reason;
        }
        
        updateLastModified();
        notifyObservers();
    }
    
    // Information Expert methods
    public boolean canBeModified() {
        return state == ShiftState.SCHEDULED || state == ShiftState.MODIFIABLE;
    }
    
    public boolean hasAvailableSlots() {
        return assignedStaffIds.size() < maxCapacity;
    }
    
    public int getAvailableSlots() {
        return maxCapacity - assignedStaffIds.size();
    }
    
    public boolean isStaffAvailable(String staffId) {
        Availability availability = availabilities.get(staffId);
        return availability != null && availability.isAvailable();
    }
    
    public boolean isStaffAssigned(String staffId) {
        return assignedStaffIds.contains(staffId);
    }
    
    public int getDurationInMinutes() {
        return (endTime.getHour() * 60 + endTime.getMinute()) - 
               (startTime.getHour() * 60 + startTime.getMinute());
    }
    
    public boolean overlapsWithTime(LocalTime otherStart, LocalTime otherEnd) {
        return !endTime.isBefore(otherStart) && !startTime.isAfter(otherEnd);
    }
    
    public boolean overlapsWith(Shift otherShift) {
        if (!date.equals(otherShift.date)) {
            return false;
        }
        return overlapsWithTime(otherShift.startTime, otherShift.endTime);
    }
    
    public boolean isPartOfGroup() {
        return groupId != null && !groupId.trim().isEmpty();
    }
    
    public boolean isPast() {
        LocalDateTime shiftDateTime = LocalDateTime.of(date, startTime);
        return shiftDateTime.isBefore(LocalDateTime.now());
    }
    
    public boolean isFuture() {
        LocalDateTime shiftDateTime = LocalDateTime.of(date, startTime);
        return shiftDateTime.isAfter(LocalDateTime.now());
    }
    
    public boolean isToday() {
        return date.equals(LocalDate.now());
    }
    
    // Observer pattern methods
    public void addObserver(ShiftObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(ShiftObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers() {
        for (ShiftObserver observer : observers) {
            observer.onShiftUpdated(this);
        }
    }
    
    // Private helper methods
    private void validateTimes() {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("L'orario di fine non può essere precedente a quello di inizio");
        }
    }
    
    private void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Shift shift = (Shift) obj;
        return Objects.equals(id, shift.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Shift{id='%s', type=%s, state=%s, date=%s, time=%s-%s, location='%s'}", 
                           id, type, state, date, startTime, endTime, location);
    }
    
    // Inner class per le disponibilità
    public static class Availability {
        private final String staffId;
        private boolean isAvailable;
        private boolean isConfirmed;
        private LocalDateTime timestamp;
        
        public Availability(String staffId, boolean isAvailable) {
            this.staffId = Objects.requireNonNull(staffId);
            this.isAvailable = isAvailable;
            this.isConfirmed = false;
            this.timestamp = LocalDateTime.now();
        }
        
        public String getStaffId() { return staffId; }
        public boolean isAvailable() { return isAvailable; }
        public boolean isConfirmed() { return isConfirmed; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        public void setAvailable(boolean available) {
            if (isConfirmed) {
                throw new IllegalStateException("Non è possibile modificare una disponibilità confermata");
            }
            this.isAvailable = available;
            this.timestamp = LocalDateTime.now();
        }
        
        public void setConfirmed(boolean confirmed) {
            this.isConfirmed = confirmed;
        }
    }
    
    // Observer interface
    public interface ShiftObserver {
        void onShiftUpdated(Shift shift);
    }
}