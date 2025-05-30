package com.saslab.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta l'assegnazione di un compito ad un cuoco in un turno
 * Implementa pattern Information Expert per gestire i dati dell'assegnazione
 */
public class TaskAssignment {
    
    private String id;
    private String taskId;
    private String cookId;
    private String shiftId;
    private int timeEstimate; // tempo stimato in minuti
    private int quantity; // quantità da preparare
    private boolean completed;
    private boolean locked; // bloccato dopo conferma del piano
    private boolean hasIssues;
    private List<String> issues;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastModified;
    
    public TaskAssignment(String id, String taskId, String cookId, String shiftId, int timeEstimate, int quantity) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.taskId = Objects.requireNonNull(taskId, "L'ID del task non può essere null");
        this.cookId = Objects.requireNonNull(cookId, "L'ID del cuoco non può essere null");
        this.shiftId = Objects.requireNonNull(shiftId, "L'ID del turno non può essere null");
        
        if (timeEstimate <= 0) {
            throw new IllegalArgumentException("Il tempo stimato deve essere positivo");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
        
        this.timeEstimate = timeEstimate;
        this.quantity = quantity;
        this.completed = false;
        this.locked = false;
        this.hasIssues = false;
        this.issues = new ArrayList<>();
        this.assignedAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getTaskId() { return taskId; }
    public String getCookId() { return cookId; }
    public String getShiftId() { return shiftId; }
    public int getTimeEstimate() { return timeEstimate; }
    public int getQuantity() { return quantity; }
    public boolean isCompleted() { return completed; }
    public boolean isLocked() { return locked; }
    public boolean hasIssues() { return hasIssues; }
    public List<String> getIssues() { return new ArrayList<>(issues); }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getLastModified() { return lastModified; }
    
    // Setters con validazione
    public void setCookId(String cookId) {
        if (locked) {
            throw new IllegalStateException("L'assegnazione è bloccata e non può essere modificata");
        }
        this.cookId = Objects.requireNonNull(cookId, "L'ID del cuoco non può essere null");
        updateLastModified();
    }
    
    public void setShiftId(String shiftId) {
        if (locked) {
            throw new IllegalStateException("L'assegnazione è bloccata e non può essere modificata");
        }
        this.shiftId = Objects.requireNonNull(shiftId, "L'ID del turno non può essere null");
        updateLastModified();
    }
    
    public void setTimeEstimate(int timeEstimate) {
        if (locked) {
            throw new IllegalStateException("L'assegnazione è bloccata e non può essere modificata");
        }
        if (timeEstimate <= 0) {
            throw new IllegalArgumentException("Il tempo stimato deve essere positivo");
        }
        this.timeEstimate = timeEstimate;
        updateLastModified();
    }
    
    public void setQuantity(int quantity) {
        if (locked) {
            throw new IllegalStateException("L'assegnazione è bloccata e non può essere modificata");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
        this.quantity = quantity;
        updateLastModified();
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (!completed) {
            this.completedAt = null;
        }
        updateLastModified();
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
        updateLastModified();
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
        if (completedAt != null) {
            this.completed = true;
        }
        updateLastModified();
    }
    
    // Business methods
    public void reportIssue(String issueDescription) {
        if (issueDescription == null || issueDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("La descrizione del problema non può essere vuota");
        }
        
        issues.add(issueDescription);
        this.hasIssues = true;
        updateLastModified();
    }
    
    public void resolveIssue(String issueDescription) {
        boolean removed = issues.remove(issueDescription);
        if (removed && issues.isEmpty()) {
            this.hasIssues = false;
        }
        updateLastModified();
    }
    
    public void clearAllIssues() {
        issues.clear();
        this.hasIssues = false;
        updateLastModified();
    }
    
    public boolean canBeModified() {
        return !locked && !completed;
    }
    
    public boolean canBeCompleted() {
        return !completed;
    }
    
    public boolean isOverdue() {
        // Semplificazione: considera in ritardo se assegnato da più di 24 ore e non completato
        return !completed && assignedAt.isBefore(LocalDateTime.now().minusHours(24));
    }
    
    public double getEfficiencyRate() {
        if (!completed || completedAt == null) {
            return 0.0;
        }
        
        long actualMinutes = java.time.Duration.between(assignedAt, completedAt).toMinutes();
        if (actualMinutes <= 0) {
            return 1.0; // Completato immediatamente
        }
        
        return (double) timeEstimate / actualMinutes;
    }
    
    public String getStatus() {
        if (completed) {
            return "Completato";
        } else if (hasIssues) {
            return "Con Problemi";
        } else if (isOverdue()) {
            return "In Ritardo";
        } else {
            return "In Corso";
        }
    }
    
    public int getEstimatedEndTimeInMinutes() {
        // Restituisce il tempo stimato dall'assegnazione in minuti
        long minutesSinceAssigned = java.time.Duration.between(assignedAt, LocalDateTime.now()).toMinutes();
        return Math.max(0, timeEstimate - (int) minutesSinceAssigned);
    }
    
    private void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskAssignment that = (TaskAssignment) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("TaskAssignment{id='%s', taskId='%s', cookId='%s', shiftId='%s', status='%s', timeEstimate=%d min, quantity=%d}", 
                           id, taskId, cookId, shiftId, getStatus(), timeEstimate, quantity);
    }
}