package com.saslab.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta un compito nel sistema Cat &amp; Ring
 * Implementa pattern State per gestire gli stati del compito
 */
public class Task {
    
    public enum TaskType {
        RECIPE("Ricetta"),
        PREPARATION("Preparazione"),
        EXTRA("Extra"),
        PREPARATION_PART("Parte di Preparazione");
        
        private final String displayName;
        
        TaskType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum TaskPriority {
        LOW("Bassa"),
        NORMAL("Normale"),
        HIGH("Alta"),
        CRITICAL("Critica");
        
        private final String displayName;
        
        TaskPriority(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String description;
    private TaskType type;
    private TaskPriority priority;
    private boolean assigned;
    private boolean completed;
    private boolean hasIssues;
    private boolean split; // indica se il task è stato suddiviso in parti
    private String parentTaskId; // per le parti di preparazione
    private List<String> partIds; // per i task suddivisi
    private String assignedShift;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    
    public Task(String id, String description, TaskType type) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.description = Objects.requireNonNull(description, "La descrizione non può essere null");
        this.type = Objects.requireNonNull(type, "Il tipo non può essere null");
        
        this.priority = TaskPriority.NORMAL;
        this.assigned = false;
        this.completed = false;
        this.hasIssues = false;
        this.split = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getDescription() { return description; }
    public TaskType getType() { return type; }
    public TaskPriority getPriority() { return priority; }
    public boolean isAssigned() { return assigned; }
    public boolean isCompleted() { return completed; }
    public boolean isHasIssues() { return hasIssues; }
    public boolean isSplit() { return split; }
    public String getParentTaskId() { return parentTaskId; }
    public List<String> getPartIds() { return partIds; }
    public String getAssignedShift() { return assignedShift; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getNotes() { return notes; }
    
    // Setters
    public void setDescription(String description) {
        this.description = Objects.requireNonNull(description, "La descrizione non può essere null");
        updateTimestamp();
    }
    
    public void setPriority(TaskPriority priority) {
        this.priority = Objects.requireNonNull(priority, "La priorità non può essere null");
        updateTimestamp();
    }
    
    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
        updateTimestamp();
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
        updateTimestamp();
    }
    
    public void setHasIssues(boolean hasIssues) {
        this.hasIssues = hasIssues;
        updateTimestamp();
    }
    
    public void setSplit(boolean split) {
        this.split = split;
        updateTimestamp();
    }
    
    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
        updateTimestamp();
    }
    
    public void setPartIds(List<String> partIds) {
        this.partIds = partIds;
        updateTimestamp();
    }
    
    public void setAssignedShift(String assignedShift) {
        this.assignedShift = assignedShift;
        updateTimestamp();
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        updateTimestamp();
    }
    
    // Business methods
    public boolean canBeAssigned() {
        return !assigned && !completed;
    }
    
    public boolean canBeCompleted() {
        return assigned && !completed;
    }
    
    public boolean requiresAttention() {
        return hasIssues || (assigned && !completed);
    }
    
    public boolean isPartOfSplitTask() {
        return parentTaskId != null && type == TaskType.PREPARATION_PART;
    }
    
    public boolean isCritical() {
        return priority == TaskPriority.CRITICAL;
    }
    
    public String getStatusDescription() {
        if (completed) {
            return "Completato";
        } else if (hasIssues) {
            return "Con Problemi";
        } else if (assigned) {
            return "Assegnato";
        } else {
            return "Non Assegnato";
        }
    }
    
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(id, task.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Task{id='%s', description='%s', type=%s, priority=%s, status='%s'}", 
                           id, description, type, priority, getStatusDescription());
    }
}