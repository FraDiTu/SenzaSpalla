package com.saslab.controller;

import com.saslab.model.Task;
import com.saslab.model.TaskAssignment;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Controller per la gestione dei compiti della cucina
 * Implementa pattern Controller (GRASP) e i contratti del SSD per la gestione compiti
 */
public class TaskController {
    
    private final Map<String, Task> tasks;
    private final Map<String, TaskAssignment> assignments;
    private final Map<String, List<String>> shiftAssignments; // shiftId -> list of taskIds
    private boolean planConfirmed;
    
    public TaskController() {
        this.tasks = new ConcurrentHashMap<>();
        this.assignments = new ConcurrentHashMap<>();
        this.shiftAssignments = new ConcurrentHashMap<>();
        this.planConfirmed = false;
    }
    
    /**
     * Aggiunge un'attività extra non presente nel menù
     * Implementa contratto CO1: addExtraTask
     */
    public String addExtraTask(String preparationDetails) {
        // Pre-condizioni del contratto CO1 (semplificate per questa implementazione)
        if (preparationDetails == null || preparationDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Dettagli della nuova attività sono obbligatori");
        }
        
        if (planConfirmed) {
            throw new IllegalStateException("Il piano è già confermato, non è possibile aggiungere nuove attività");
        }
        
        String taskId = generateTaskId();
        Task task = new Task(taskId, preparationDetails, Task.TaskType.EXTRA);
        
        tasks.put(taskId, task);
        
        // Post-condizioni: l'attività extra è aggiunta e pronta per essere assegnata
        return taskId;
    }
    
    /**
     * Assegna un compito ad un cuoco
     * Implementa contratto CO2: assignTaskToCook
     */
    public void assignTaskToCook(String taskID, String cookID, String shiftID, int timeEstimate, int quantity) {
        // Pre-condizioni del contratto CO2
        Task task = tasks.get(taskID);
        if (task == null) {
            throw new IllegalArgumentException("L'attività da assegnare non esiste");
        }
        
        if (cookID == null || cookID.trim().isEmpty()) {
            throw new IllegalArgumentException("Il cuoco deve essere valido");
        }
        
        if (shiftID == null || shiftID.trim().isEmpty()) {
            throw new IllegalArgumentException("Il turno deve essere valido");
        }
        
        if (timeEstimate <= 0 || quantity <= 0) {
            throw new IllegalArgumentException("Stime di tempo e quantità devono essere positive");
        }
        
        if (planConfirmed) {
            throw new IllegalStateException("Il piano è già confermato, non è possibile modificare le assegnazioni");
        }
        
        // Verifica disponibilità del cuoco nel turno (semplificata)
        if (isCookBusyInShift(cookID, shiftID, timeEstimate)) {
            throw new IllegalStateException("Il cuoco non è libero nel turno specificato");
        }
        
        String assignmentId = generateAssignmentId();
        TaskAssignment assignment = new TaskAssignment(assignmentId, taskID, cookID, shiftID, timeEstimate, quantity);
        
        assignments.put(assignmentId, assignment);
        task.setAssigned(true);
        
        // Aggiorna le assegnazioni per turno
        shiftAssignments.computeIfAbsent(shiftID, k -> new ArrayList<>()).add(taskID);
        
        // Post-condizioni: l'attività è assegnata al cuoco nel turno scelto, stato dell'attività aggiornato
    }
    
    /**
     * Suddivide una preparazione in parti
     * Implementa contratto CO3: splitPreparation
     */
    public List<String> splitPreparation(String preparationID, int quantityParts) {
        // Pre-condizioni del contratto CO3
        Task task = tasks.get(preparationID);
        if (task == null) {
            throw new IllegalArgumentException("L'ID di preparazione non è valido");
        }
        
        if (quantityParts <= 1) {
            throw new IllegalArgumentException("Il numero di parti deve essere maggiore di 1");
        }
        
        if (planConfirmed) {
            throw new IllegalStateException("Il piano è già confermato, non è possibile suddividere preparazioni");
        }
        
        List<String> partIds = new ArrayList<>();
        
        // Crea le parti della preparazione
        for (int i = 1; i <= quantityParts; i++) {
            String partId = generateTaskId();
            String partDescription = task.getDescription() + " (Parte " + i + "/" + quantityParts + ")";
            Task partTask = new Task(partId, partDescription, Task.TaskType.PREPARATION_PART);
            partTask.setParentTaskId(preparationID);
            
            tasks.put(partId, partTask);
            partIds.add(partId);
        }
        
        // Marca l'attività originale come suddivisa
        task.setSplit(true);
        task.setPartIds(partIds);
        
        // Post-condizioni: la preparazione è suddivisa nelle parti richieste
        return partIds;
    }
    
    /**
     * Assegna una parte di preparazione ad un turno
     * Implementa contratto CO4: assignPreparationPartToShift
     */
    public void assignPreparationPartToShift(String partID, String shiftID) {
        // Pre-condizioni del contratto CO4
        Task partTask = tasks.get(partID);
        if (partTask == null || partTask.getType() != Task.TaskType.PREPARATION_PART) {
            throw new IllegalArgumentException("La parte di preparazione non esiste");
        }
        
        if (!hasShiftCapacity(shiftID)) {
            throw new IllegalStateException("Il turno non ha capacità residua");
        }
        
        if (planConfirmed) {
            throw new IllegalStateException("Il piano è già confermato, non è possibile modificare le assegnazioni");
        }
        
        // Assegna la parte al turno (senza cuoco specifico per ora)
        partTask.setAssignedShift(shiftID);
        shiftAssignments.computeIfAbsent(shiftID, k -> new ArrayList<>()).add(partID);
        
        // Post-condizioni: la parte di preparazione è assegnata al turno
    }
    
    /**
     * Conferma il piano dei compiti
     * Implementa contratto CO5: confirmTaskPlan
     */
    public void confirmTaskPlan() {
        // Pre-condizioni del contratto CO5
        if (!areAllCriticalTasksAssigned()) {
            throw new IllegalStateException("Tutte le attività e preparazioni critiche devono essere assegnate");
        }
        
        if (hasExecutionOrderWarnings()) {
            throw new IllegalStateException("Non ci devono essere avvisi di ordine di esecuzione in sospeso");
        }
        
        this.planConfirmed = true;
        
        // Blocca tutti gli assignment
        for (TaskAssignment assignment : assignments.values()) {
            assignment.setLocked(true);
        }
        
        // Post-condizioni: il piano dei compiti è confermato, tutti gli assignment sono bloccati e aggiornati
    }
    
    /**
     * Ottiene tutte le attività
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    
    /**
     * Ottiene le attività assegnate ad un cuoco
     */
    public List<TaskAssignment> getTaskAssignmentsByCook(String cookId) {
        return assignments.values().stream()
                .filter(assignment -> assignment.getCookId().equals(cookId))
                .sorted((a1, a2) -> a1.getShiftId().compareTo(a2.getShiftId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene le attività di un turno
     */
    public List<TaskAssignment> getTaskAssignmentsByShift(String shiftId) {
        return assignments.values().stream()
                .filter(assignment -> assignment.getShiftId().equals(shiftId))
                .sorted((a1, a2) -> Integer.compare(a1.getTimeEstimate(), a2.getTimeEstimate()))
                .collect(Collectors.toList());
    }
    
    /**
     * Segna un compito come completato
     */
    public void markTaskCompleted(String taskId, String cookId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Attività non trovata");
        }
        
        // Trova l'assegnazione corrispondente
        TaskAssignment assignment = assignments.values().stream()
                .filter(a -> a.getTaskId().equals(taskId) && a.getCookId().equals(cookId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Assegnazione non trovata"));
        
        assignment.setCompleted(true);
        assignment.setCompletedAt(java.time.LocalDateTime.now());
        task.setCompleted(true);
    }
    
    /**
     * Riporta un problema su un compito
     */
    public void reportTaskIssue(String taskId, String cookId, String issueDescription) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Attività non trovata");
        }
        
        TaskAssignment assignment = assignments.values().stream()
                .filter(a -> a.getTaskId().equals(taskId) && a.getCookId().equals(cookId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Assegnazione non trovata"));
        
        assignment.reportIssue(issueDescription);
        task.setHasIssues(true);
    }
    
    /**
     * Ottiene lo stato di avanzamento di un turno
     */
    public Map<String, Object> getShiftProgress(String shiftId) {
        List<TaskAssignment> shiftAssignments = getTaskAssignmentsByShift(shiftId);
        
        long completedTasks = shiftAssignments.stream()
                .filter(TaskAssignment::isCompleted)
                .count();
        
        long tasksWithIssues = shiftAssignments.stream()
                .filter(TaskAssignment::hasIssues)
                .count();
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("totalTasks", shiftAssignments.size());
        progress.put("completedTasks", (int) completedTasks);
        progress.put("tasksWithIssues", (int) tasksWithIssues);
        progress.put("completionPercentage", 
                    shiftAssignments.isEmpty() ? 0 : (completedTasks * 100.0 / shiftAssignments.size()));
        
        return progress;
    }
    
    /**
     * Riassegna un compito ad un altro cuoco
     */
    public void reassignTask(String taskId, String newCookId, String newShiftId) {
        if (planConfirmed) {
            throw new IllegalStateException("Il piano è confermato, non è possibile riassegnare compiti");
        }
        
        TaskAssignment assignment = assignments.values().stream()
                .filter(a -> a.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Assegnazione non trovata"));
        
        // Rimuovi dalla lista del vecchio turno
        List<String> oldShiftTasks = shiftAssignments.get(assignment.getShiftId());
        if (oldShiftTasks != null) {
            oldShiftTasks.remove(taskId);
        }
        
        // Aggiorna l'assegnazione
        assignment.setCookId(newCookId);
        assignment.setShiftId(newShiftId);
        
        // Aggiungi alla lista del nuovo turno
        shiftAssignments.computeIfAbsent(newShiftId, k -> new ArrayList<>()).add(taskId);
    }
    
    /**
     * Ottiene statistiche sui compiti
     */
    public Map<String, Integer> getTaskStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        long totalTasks = tasks.size();
        long assignedTasks = tasks.values().stream().filter(Task::isAssigned).count();
        long completedTasks = tasks.values().stream().filter(Task::isCompleted).count();
        long tasksWithIssues = tasks.values().stream().filter(Task::isHasIssues).count();
        
        stats.put("total", (int) totalTasks);
        stats.put("assigned", (int) assignedTasks);
        stats.put("completed", (int) completedTasks);
        stats.put("withIssues", (int) tasksWithIssues);
        stats.put("unassigned", (int) (totalTasks - assignedTasks));
        
        return stats;
    }
    
    /**
     * Verifica se il piano è confermato
     */
    public boolean isPlanConfirmed() {
        return planConfirmed;
    }
    
    /**
     * Reset del piano (per ricominciare la pianificazione)
     */
    public void resetPlan() {
        if (planConfirmed) {
            // Sblocca tutti gli assignment
            for (TaskAssignment assignment : assignments.values()) {
                assignment.setLocked(false);
            }
        }
        
        this.planConfirmed = false;
    }
    
    // Metodi di utilità privati
    private boolean isCookBusyInShift(String cookId, String shiftId, int additionalTime) {
        int currentWorkload = assignments.values().stream()
                .filter(a -> a.getCookId().equals(cookId) && a.getShiftId().equals(shiftId))
                .mapToInt(TaskAssignment::getTimeEstimate)
                .sum();
        
        // Assume 8 ore (480 minuti) come capacità massima per turno
        return (currentWorkload + additionalTime) > 480;
    }
    
    private boolean hasShiftCapacity(String shiftId) {
        List<String> shiftTasks = shiftAssignments.get(shiftId);
        if (shiftTasks == null) {
            return true;
        }
        
        // Semplificazione: un turno può avere massimo 10 attività
        return shiftTasks.size() < 10;
    }
    
    private boolean areAllCriticalTasksAssigned() {
        // Semplificazione: considera critiche tutte le attività di tipo RECIPE
        return tasks.values().stream()
                .filter(task -> task.getType() == Task.TaskType.RECIPE)
                .allMatch(Task::isAssigned);
    }
    
    private boolean hasExecutionOrderWarnings() {
        // Semplificazione: nessun controllo specifico per ora
        return false;
    }
    
    private String generateTaskId() {
        return "TSK_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateAssignmentId() {
        return "ASS_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Ottiene un compito per ID
     */
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }
    
    /**
     * Ottiene un'assegnazione per ID
     */
    public TaskAssignment getAssignment(String assignmentId) {
        return assignments.get(assignmentId);
    }
    
    /**
     * Ottiene tutte le assegnazioni
     */
    public List<TaskAssignment> getAllAssignments() {
        return new ArrayList<>(assignments.values());
    }
}