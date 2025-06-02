package com.saslab.controller;

import com.saslab.model.Shift;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Controller per la gestione dei turni
 * Implementa pattern Controller (GRASP) per coordinare le operazioni sui turni
 * Implementa pattern Observer per notificare i cambiamenti
 */
public class ShiftController implements Shift.ShiftObserver {
    
    private final Map<String, Shift> shifts;
    private final Map<String, List<String>> shiftGroups; // groupId -> list of shiftIds
    private final Map<String, Boolean> recurringGroups; // groupId -> isRecurring
    
    public ShiftController() {
        this.shifts = new ConcurrentHashMap<>();
        this.shiftGroups = new ConcurrentHashMap<>();
        this.recurringGroups = new ConcurrentHashMap<>();
    }
    
    /**
     * Crea un turno preparatorio
     */
    public String createPreparatoryShift(LocalDate date, LocalTime startTime, 
                                        LocalTime endTime, String location) {
        validateShiftCreationParameters(date, startTime, endTime, location);
        
        String shiftId = generateShiftId();
        Shift shift = new Shift(shiftId, date, startTime, endTime, location);
        
        shift.addObserver(this);
        shifts.put(shiftId, shift);
        
        return shiftId;
    }
    
    /**
     * Crea un turno di servizio
     */
    public String createServiceShift(String serviceId, LocalDate date, 
                                    LocalTime startTime, LocalTime endTime, String location) {
        validateShiftCreationParameters(date, startTime, endTime, location);
        
        String shiftId = generateShiftId();
        Shift shift = new Shift(shiftId, serviceId, date, startTime, endTime, location);
        
        shift.addObserver(this);
        shifts.put(shiftId, shift);
        
        return shiftId;
    }
    
    /**
     * Crea turni ricorrenti
     */
    public List<String> createRecurringShifts(LocalDate startDate, LocalDate endDate,
                                             LocalTime startTime, LocalTime endTime,
                                             String location, Set<Integer> daysOfWeek) {
        List<String> createdShiftIds = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            if (daysOfWeek.contains(currentDate.getDayOfWeek().getValue())) {
                String shiftId = createPreparatoryShift(currentDate, startTime, endTime, location);
                createdShiftIds.add(shiftId);
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return createdShiftIds;
    }
    
    /**
     * Crea un raggruppamento di turni
     */
    public String createShiftGroup(List<String> shiftIds, boolean isRecurring) {
        if (shiftIds == null || shiftIds.isEmpty()) {
            throw new IllegalArgumentException("La lista dei turni non può essere vuota");
        }
        
        // Verifica che tutti i turni esistano e siano modificabili
        for (String shiftId : shiftIds) {
            Shift shift = getShiftOrThrow(shiftId);
            if (!shift.canBeModified()) {
                throw new IllegalStateException("Turno " + shiftId + " non può essere raggruppato");
            }
        }
        
        String groupId = generateGroupId();
        
        // Assegna il gruppo a tutti i turni
        for (String shiftId : shiftIds) {
            Shift shift = shifts.get(shiftId);
            shift.setGroupId(groupId);
        }
        
        shiftGroups.put(groupId, new ArrayList<>(shiftIds));
        recurringGroups.put(groupId, isRecurring);
        
        return groupId;
    }
    
    /**
     * Aggiunge disponibilità a un turno
     */
    public void addAvailability(String shiftId, String staffId, boolean isAvailable) {
        Shift shift = getShiftOrThrow(shiftId);
        
        // Se il turno fa parte di un gruppo, aggiorna tutti i turni del gruppo
        if (shift.isPartOfGroup()) {
            List<String> groupShifts = shiftGroups.get(shift.getGroupId());
            if (groupShifts != null) {
                for (String groupShiftId : groupShifts) {
                    Shift groupShift = shifts.get(groupShiftId);
                    if (groupShift != null) {
                        groupShift.addAvailability(staffId, isAvailable);
                    }
                }
                return;
            }
        }
        
        shift.addAvailability(staffId, isAvailable);
    }
    
    /**
     * Rimuove disponibilità da un turno
     */
    public void removeAvailability(String shiftId, String staffId) {
        Shift shift = getShiftOrThrow(shiftId);
        shift.removeAvailability(staffId);
    }
    
    /**
     * Assegna personale a un turno
     */
    public void assignStaff(String shiftId, String staffId, String assignedByOrganizerId) {
        Shift shift = getShiftOrThrow(shiftId);
        
        if (!shift.isStaffAvailable(staffId)) {
            throw new IllegalStateException("Il personale deve aver dato disponibilità");
        }
        
        shift.assignStaff(staffId);
    }
    
    /**
     * Rimuove assegnazione personale
     */
    public void unassignStaff(String shiftId, String staffId) {
        Shift shift = getShiftOrThrow(shiftId);
        shift.unassignStaff(staffId);
    }
    
    /**
     * Modifica orario turno
     */
    public void updateShiftTime(String shiftId, LocalTime newStartTime, LocalTime newEndTime) {
        Shift shift = getShiftOrThrow(shiftId);
        
        if (newStartTime != null) {
            shift.setStartTime(newStartTime);
        }
        
        if (newEndTime != null) {
            shift.setEndTime(newEndTime);
        }
    }
    
    /**
     * Modifica location turno
     */
    public void updateShiftLocation(String shiftId, String newLocation) {
        Shift shift = getShiftOrThrow(shiftId);
        shift.setLocation(newLocation);
    }
    
    /**
     * Avvia un turno
     */
    public void startShift(String shiftId) {
        Shift shift = getShiftOrThrow(shiftId);
        shift.startShift();
    }
    
    /**
     * Completa un turno
     */
    public void completeShift(String shiftId) {
        Shift shift = getShiftOrThrow(shiftId);
        shift.completeShift();
    }
    
    /**
     * Annulla un turno
     */
    public void cancelShift(String shiftId, String reason) {
        Shift shift = getShiftOrThrow(shiftId);
        shift.cancelShift(reason);
    }
    
    /**
     * Ottiene un turno per ID
     */
    public Shift getShift(String shiftId) {
        return shifts.get(shiftId);
    }
    
    /**
     * Ottiene tutti i turni preparatori
     */
    public List<Shift> getPreparatoryShifts() {
        return shifts.values().stream()
                .filter(shift -> shift.getType() == Shift.ShiftType.PREPARATORY)
                .sorted((s1, s2) -> s1.getDate().compareTo(s2.getDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene tutti i turni di servizio
     */
    public List<Shift> getServiceShifts() {
        return shifts.values().stream()
                .filter(shift -> shift.getType() == Shift.ShiftType.SERVICE)
                .sorted((s1, s2) -> s1.getDate().compareTo(s2.getDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene turni per data
     */
    public List<Shift> getShiftsByDate(LocalDate date) {
        return shifts.values().stream()
                .filter(shift -> shift.getDate().equals(date))
                .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene turni in un intervallo di date
     */
    public List<Shift> getShiftsInDateRange(LocalDate startDate, LocalDate endDate) {
        return shifts.values().stream()
                .filter(shift -> !shift.getDate().isBefore(startDate) && 
                               !shift.getDate().isAfter(endDate))
                .sorted((s1, s2) -> {
                    int dateCompare = s1.getDate().compareTo(s2.getDate());
                    if (dateCompare != 0) return dateCompare;
                    return s1.getStartTime().compareTo(s2.getStartTime());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene turni futuri
     */
    public List<Shift> getFutureShifts() {
        return shifts.values().stream()
                .filter(Shift::isFuture)
                .sorted((s1, s2) -> s1.getDate().compareTo(s2.getDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene turni con posti disponibili
     */
    public List<Shift> getShiftsWithAvailableSlots() {
        return shifts.values().stream()
                .filter(Shift::hasAvailableSlots)
                .filter(Shift::isFuture)
                .sorted((s1, s2) -> s1.getDate().compareTo(s2.getDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene turni per personale
     */
    public List<Shift> getShiftsByStaff(String staffId) {
        return shifts.values().stream()
                .filter(shift -> shift.isStaffAssigned(staffId))
                .sorted((s1, s2) -> s1.getDate().compareTo(s2.getDate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene turni per servizio
     */
    public List<Shift> getShiftsByService(String serviceId) {
        return shifts.values().stream()
                .filter(shift -> serviceId.equals(shift.getServiceId()))
                .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica conflitti di turno per un membro del personale
     */
    public List<Shift> checkStaffScheduleConflicts(String staffId, LocalDate date) {
        return shifts.values().stream()
                .filter(shift -> shift.getDate().equals(date))
                .filter(shift -> shift.isStaffAssigned(staffId) || 
                               shift.isStaffAvailable(staffId))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene statistiche sui turni
     */
    public Map<String, Integer> getShiftStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        long totalShifts = shifts.size();
        long preparatoryShifts = shifts.values().stream()
                .filter(s -> s.getType() == Shift.ShiftType.PREPARATORY).count();
        long serviceShifts = shifts.values().stream()
                .filter(s -> s.getType() == Shift.ShiftType.SERVICE).count();
        long scheduledShifts = shifts.values().stream()
                .filter(s -> s.getState() == Shift.ShiftState.SCHEDULED).count();
        long inProgressShifts = shifts.values().stream()
                .filter(s -> s.getState() == Shift.ShiftState.IN_PROGRESS).count();
        long completedShifts = shifts.values().stream()
                .filter(s -> s.getState() == Shift.ShiftState.COMPLETED).count();
        long cancelledShifts = shifts.values().stream()
                .filter(s -> s.getState() == Shift.ShiftState.CANCELLED).count();
        
        stats.put("total", (int) totalShifts);
        stats.put("preparatory", (int) preparatoryShifts);
        stats.put("service", (int) serviceShifts);
        stats.put("scheduled", (int) scheduledShifts);
        stats.put("inProgress", (int) inProgressShifts);
        stats.put("completed", (int) completedShifts);
        stats.put("cancelled", (int) cancelledShifts);
        
        return stats;
    }
    
    /**
     * Elimina un turno (solo se non ha disponibilità)
     */
    public boolean deleteShift(String shiftId) {
        Shift shift = shifts.get(shiftId);
        if (shift == null) {
            return false;
        }
        
        if (!shift.getAvailabilities().isEmpty()) {
            throw new IllegalStateException("Non è possibile eliminare un turno con disponibilità");
        }
        
        if (shift.isPartOfGroup()) {
            List<String> groupShifts = shiftGroups.get(shift.getGroupId());
            if (groupShifts != null) {
                groupShifts.remove(shiftId);
                if (groupShifts.isEmpty()) {
                    shiftGroups.remove(shift.getGroupId());
                    recurringGroups.remove(shift.getGroupId());
                }
            }
        }
        
        shift.removeObserver(this);
        shifts.remove(shiftId);
        return true;
    }
    
    // Implementazione dell'interfaccia ShiftObserver
    @Override
    public void onShiftUpdated(Shift shift) {
        // Log o altre azioni quando un turno viene aggiornato
        System.out.printf("[ShiftController] Turno aggiornato: %s - Stato: %s%n", 
                         shift.getId(), shift.getState());
    }
    
    // Metodi di utilità privati
    private Shift getShiftOrThrow(String shiftId) {
        Shift shift = shifts.get(shiftId);
        if (shift == null) {
            throw new IllegalArgumentException("Turno non trovato: " + shiftId);
        }
        return shift;
    }
    
    private String generateShiftId() {
        return "SHF_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateGroupId() {
        return "GRP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void validateShiftCreationParameters(LocalDate date, LocalTime startTime, 
                                                LocalTime endTime, String location) {
        if (date == null) {
            throw new IllegalArgumentException("La data è obbligatoria");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Gli orari sono obbligatori");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("L'orario di fine deve essere successivo a quello di inizio");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("La location è obbligatoria");
        }
    }
    
    /**
     * Ottiene il numero totale di turni
     */
    public int getTotalShiftCount() {
        return shifts.size();
    }
}