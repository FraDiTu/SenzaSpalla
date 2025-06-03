package com.saslab.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Classe che rappresenta un Servizio all'interno di un evento
 * Ogni servizio ha una fascia oraria specifica e un proprio menù
 */
public class Service {
    
    public enum ServiceType {
        BREAKFAST("Colazione"),
        LUNCH("Pranzo"),
        DINNER("Cena"),
        COCKTAIL("Cocktail"),
        BUFFET("Buffet"),
        COFFEE_BREAK("Coffee Break"),
        APERITIF("Aperitivo"),
        BRUNCH("Brunch");
        
        private final String displayName;
        
        ServiceType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String eventId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ServiceType type;
    private String menuId;
    private String notes;
    private boolean requiresKitchenOnSite;
    private boolean requiresChefPresence;
    private int expectedGuests;
    
    // Constructor
    public Service(String id, String eventId, LocalDate date, LocalTime startTime, LocalTime endTime, ServiceType type) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.eventId = Objects.requireNonNull(eventId, "L'event ID non può essere null");
        this.date = Objects.requireNonNull(date, "La data non può essere null");
        this.startTime = Objects.requireNonNull(startTime, "L'orario di inizio non può essere null");
        this.endTime = Objects.requireNonNull(endTime, "L'orario di fine non può essere null");
        this.type = Objects.requireNonNull(type, "Il tipo di servizio non può essere null");
        
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("L'orario di fine non può essere precedente a quello di inizio");
        }
        
        this.requiresKitchenOnSite = false;
        this.requiresChefPresence = false;
        this.expectedGuests = 0;
    }
    
    // Getters
    public String getId() { return id; }
    public String getEventId() { return eventId; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public ServiceType getType() { return type; }
    public String getMenuId() { return menuId; }
    public String getNotes() { return notes; }
    public boolean isRequiresKitchenOnSite() { return requiresKitchenOnSite; }
    public boolean isRequiresChefPresence() { return requiresChefPresence; }
    public int getExpectedGuests() { return expectedGuests; }
    
    // Setters con validazione
    public void setDate(LocalDate date) {
        this.date = Objects.requireNonNull(date, "La data non può essere null");
    }
    
    public void setStartTime(LocalTime startTime) {
        Objects.requireNonNull(startTime, "L'orario di inizio non può essere null");
        if (endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("L'orario di inizio non può essere successivo a quello di fine");
        }
        this.startTime = startTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        Objects.requireNonNull(endTime, "L'orario di fine non può essere null");
        if (startTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("L'orario di fine non può essere precedente a quello di inizio");
        }
        this.endTime = endTime;
    }
    
    public void setType(ServiceType type) {
        this.type = Objects.requireNonNull(type, "Il tipo di servizio non può essere null");
    }
    
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void setRequiresKitchenOnSite(boolean requiresKitchenOnSite) {
        this.requiresKitchenOnSite = requiresKitchenOnSite;
    }
    
    public void setRequiresChefPresence(boolean requiresChefPresence) {
        this.requiresChefPresence = requiresChefPresence;
    }
    
    public void setExpectedGuests(int expectedGuests) {
        if (expectedGuests < 0) {
            throw new IllegalArgumentException("Il numero di ospiti non può essere negativo");
        }
        this.expectedGuests = expectedGuests;
    }
    
    // Business methods
    public boolean hasMenu() {
        return menuId != null && !menuId.trim().isEmpty();
    }
    
    public int getDurationInMinutes() {
        return (endTime.getHour() * 60 + endTime.getMinute()) - 
               (startTime.getHour() * 60 + startTime.getMinute());
    }
    
    public boolean overlapsWithTime(LocalTime otherStart, LocalTime otherEnd) {
        return !endTime.isBefore(otherStart) && !startTime.isAfter(otherEnd);
    }
    
    public boolean overlapsWith(Service otherService) {
        if (!date.equals(otherService.date)) {
            return false;
        }
        return overlapsWithTime(otherService.startTime, otherService.endTime);
    }
    
    // Information Expert - il servizio sa se è compatibile con un certo tipo di menù
    public boolean isCompatibleWithMenuType(String menuType) {
        if (menuType == null) {
            return true;
        }
        
        String lowerMenuType = menuType.toLowerCase();
        // Logic di compatibilità
        switch (type) {
            case BREAKFAST:
                return lowerMenuType.contains("colazione") || lowerMenuType.contains("breakfast");
            case LUNCH:
                return lowerMenuType.contains("pranzo") || lowerMenuType.contains("lunch");
            case DINNER:
                return lowerMenuType.contains("cena") || lowerMenuType.contains("dinner");
            case COCKTAIL:
            case APERITIF:
                return lowerMenuType.contains("cocktail") || lowerMenuType.contains("aperitivo");
            case BUFFET:
                return lowerMenuType.contains("buffet");
            case COFFEE_BREAK:
                return lowerMenuType.contains("coffee") || lowerMenuType.contains("break");
            default:
                return true; // Servizio generico accetta qualsiasi menù
        }
    }
    
    public String getTimeSlot() {
        return String.format("%s - %s", startTime, endTime);
    }
    
    public boolean isActiveAt(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Service service = (Service) obj;
        return Objects.equals(id, service.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Service{id='%s', type=%s, date=%s, time=%s-%s, eventId='%s'}", 
                           id, type, date, startTime, endTime, eventId);
    }
}