import com.saslab.controller.TaskController;
import com.saslab.model.Task;
import com.saslab.model.TaskAssignment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Test class per TaskController
 * Verifica il corretto funzionamento della gestione dei compiti della cucina
 */
public class TestTaskController {
    
    private TaskController taskController;
    
    @BeforeEach
    public void setUp() {
        taskController = new TaskController();
    }
    
    @Test
    @DisplayName("Test aggiunta attività extra - caso normale")
    public void testAddExtraTask_Normal() {
        String taskId = taskController.addExtraTask("Preparare pane");
        
        assertNotNull(taskId, "L'ID del task dovrebbe essere generato");
        assertTrue(taskId.startsWith("TSK_"), "L'ID dovrebbe iniziare con TSK_");
        
        Task task = taskController.getTask(taskId);
        assertNotNull(task, "Il task dovrebbe essere creato");
        assertEquals("Preparare pane", task.getDescription());
        assertEquals(Task.TaskType.EXTRA, task.getType());
        assertFalse(task.isAssigned(), "Il task inizialmente non dovrebbe essere assegnato");
    }
    
    @Test
    @DisplayName("Test aggiunta attività extra - dettagli vuoti")
    public void testAddExtraTask_EmptyDetails() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskController.addExtraTask("");
        }, "Dovrebbe lanciare eccezione per dettagli vuoti");
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskController.addExtraTask(null);
        }, "Dovrebbe lanciare eccezione per dettagli null");
    }
    
    @Test
    @DisplayName("Test assegnazione compito a cuoco - caso normale")
    public void testAssignTaskToCook_Normal() {
        String taskId = taskController.addExtraTask("Preparare antipasti");
        
        taskController.assignTaskToCook(taskId, "COOK_001", "SHIFT_001", 60, 5);
        
        Task task = taskController.getTask(taskId);
        assertTrue(task.isAssigned(), "Il task dovrebbe essere assegnato");
        
        List<TaskAssignment> assignments = taskController.getTaskAssignmentsByCook("COOK_001");
        assertEquals(1, assignments.size(), "Dovrebbe esserci un'assegnazione per il cuoco");
        
        TaskAssignment assignment = assignments.get(0);
        assertEquals(taskId, assignment.getTaskId());
        assertEquals("COOK_001", assignment.getCookId());
        assertEquals("SHIFT_001", assignment.getShiftId());
        assertEquals(60, assignment.getTimeEstimate());
        assertEquals(5, assignment.getQuantity());
    }
    
    @Test
    @DisplayName("Test assegnazione compito - parametri invalidi")
    public void testAssignTaskToCook_InvalidParameters() {
        String taskId = taskController.addExtraTask("Test Task");
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskController.assignTaskToCook("INVALID_TASK", "COOK_001", "SHIFT_001", 60, 5);
        }, "Dovrebbe lanciare eccezione per task inesistente");
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskController.assignTaskToCook(taskId, null, "SHIFT_001", 60, 5);
        }, "Dovrebbe lanciare eccezione per cuoco null");
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskController.assignTaskToCook(taskId, "COOK_001", "SHIFT_001", 0, 5);
        }, "Dovrebbe lanciare eccezione per tempo stimato zero");
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskController.assignTaskToCook(taskId, "COOK_001", "SHIFT_001", 60, 0);
        }, "Dovrebbe lanciare eccezione per quantità zero");
    }
    
    @Test
    @DisplayName("Test suddivisione preparazione - caso normale")
    public void testSplitPreparation_Normal() {
        String taskId = taskController.addExtraTask("Preparare pasta per 100 persone");
        
        List<String> partIds = taskController.splitPreparation(taskId, 3);
        
        assertEquals(3, partIds.size(), "Dovrebbero essere create 3 parti");
        
        Task originalTask = taskController.getTask(taskId);
        assertTrue(originalTask.isSplit(), "Il task originale dovrebbe essere marcato come suddiviso");
        assertEquals(partIds, originalTask.getPartIds());
        
        // Verifica le parti create
        for (int i = 0; i < partIds.size(); i++) {
            Task part = taskController.getTask(partIds.get(i));
            assertNotNull(part, "La parte dovrebbe esistere");
            assertEquals(Task.TaskType.PREPARATION_PART, part.getType());
            assertEquals(taskId, part.getParentTaskId());
            assertTrue(part.getDescription().contains("Parte " + (i + 1) + "/3"));
        }
    }
    
    @Test
    @DisplayName("Test suddivisione preparazione - parametri invalidi")
    public void testSplitPreparation_InvalidParameters() {
        String taskId = taskController.addExtraTask("Test Task");
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskController.splitPreparation("INVALID_TASK", 3);
        }, "Dovrebbe lanciare eccezione per task inesistente");
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskController.splitPreparation(taskId, 1);
        }, "Dovrebbe lanciare eccezione per numero parti <= 1");
    }
    
    @Test
    @DisplayName("Test assegnazione parte preparazione a turno")
    public void testAssignPreparationPartToShift() {
        String taskId = taskController.addExtraTask("Preparare lasagne");
        List<String> partIds = taskController.splitPreparation(taskId, 2);
        
        String partId = partIds.get(0);
        taskController.assignPreparationPartToShift(partId, "SHIFT_MORNING");
        
        Task part = taskController.getTask(partId);
        assertEquals("SHIFT_MORNING", part.getAssignedShift());
    }
    
    @Test
    @DisplayName("Test conferma piano compiti - senza attività critiche")
    public void testConfirmTaskPlan_NoCriticalTasks() {
        taskController.addExtraTask("Task Extra");
        
        // Senza task critici (RECIPE type), dovrebbe poter confermare
        taskController.confirmTaskPlan();
        
        assertTrue(taskController.isPlanConfirmed(), "Il piano dovrebbe essere confermato");
    }
    
    @Test
    @DisplayName("Test segna compito completato")
    public void testMarkTaskCompleted() {
        String taskId = taskController.addExtraTask("Preparare dolci");
        taskController.assignTaskToCook(taskId, "COOK_001", "SHIFT_001", 30, 1);
        
        taskController.markTaskCompleted(taskId, "COOK_001");
        
        Task task = taskController.getTask(taskId);
        assertTrue(task.isCompleted(), "Il task dovrebbe essere completato");
        
        List<TaskAssignment> assignments = taskController.getTaskAssignmentsByCook("COOK_001");
        TaskAssignment assignment = assignments.get(0);
        assertTrue(assignment.isCompleted(), "L'assegnazione dovrebbe essere completata");
        assertNotNull(assignment.getCompletedAt());
    }
    
    @Test
    @DisplayName("Test segnalazione problema su compito")
    public void testReportTaskIssue() {
        String taskId = taskController.addExtraTask("Preparare carne");
        taskController.assignTaskToCook(taskId, "COOK_001", "SHIFT_001", 45, 2);
        
        taskController.reportTaskIssue(taskId, "COOK_001", "Ingredienti mancanti");
        
        Task task = taskController.getTask(taskId);
        assertTrue(task.isHasIssues(), "Il task dovrebbe avere problemi segnalati");
        
        List<TaskAssignment> assignments = taskController.getTaskAssignmentsByCook("COOK_001");
        TaskAssignment assignment = assignments.get(0);
        assertTrue(assignment.hasIssues(), "L'assegnazione dovrebbe avere problemi");
        assertTrue(assignment.getIssues().contains("Ingredienti mancanti"));
    }
    
    @Test
    @DisplayName("Test stato avanzamento turno")
    public void testGetShiftProgress() {
        // Crea e assegna alcuni task
        String task1 = taskController.addExtraTask("Task 1");
        String task2 = taskController.addExtraTask("Task 2");
        String task3 = taskController.addExtraTask("Task 3");
        
        taskController.assignTaskToCook(task1, "COOK_001", "SHIFT_MORNING", 30, 1);
        taskController.assignTaskToCook(task2, "COOK_002", "SHIFT_MORNING", 45, 1);
        taskController.assignTaskToCook(task3, "COOK_003", "SHIFT_MORNING", 60, 1);
        
        // Completa uno e segnala problema su un altro
        taskController.markTaskCompleted(task1, "COOK_001");
        taskController.reportTaskIssue(task2, "COOK_002", "Problema generico");
        
        Map<String, Object> progress = taskController.getShiftProgress("SHIFT_MORNING");
        
        assertEquals(3, progress.get("totalTasks"));
        assertEquals(1, progress.get("completedTasks"));
        assertEquals(1, progress.get("tasksWithIssues"));
        assertEquals(33.33, (Double) progress.get("completionPercentage"), 0.01);
    }
    
    @Test
    @DisplayName("Test riassegnazione compito")
    public void testReassignTask() {
        String taskId = taskController.addExtraTask("Task da riassegnare");
        taskController.assignTaskToCook(taskId, "COOK_001", "SHIFT_MORNING", 30, 1);
        
        taskController.reassignTask(taskId, "COOK_002", "SHIFT_EVENING");
        
        List<TaskAssignment> cook001Assignments = taskController.getTaskAssignmentsByCook("COOK_001");
        List<TaskAssignment> cook002Assignments = taskController.getTaskAssignmentsByCook("COOK_002");
        
        assertEquals(0, cook001Assignments.size(), "COOK_001 non dovrebbe più avere assegnazioni");
        assertEquals(1, cook002Assignments.size(), "COOK_002 dovrebbe avere l'assegnazione");
        
        TaskAssignment reassignedTask = cook002Assignments.get(0);
        assertEquals("COOK_002", reassignedTask.getCookId());
        assertEquals("SHIFT_EVENING", reassignedTask.getShiftId());
    }
    
    @Test
    @DisplayName("Test statistiche compiti")
    public void testGetTaskStatistics() {
        // Crea task in stati diversi
        String task1 = taskController.addExtraTask("Task 1");
        String task2 = taskController.addExtraTask("Task 2");
        String task3 = taskController.addExtraTask("Task 3");
        
        taskController.assignTaskToCook(task1, "COOK_001", "SHIFT_001", 30, 1);
        taskController.assignTaskToCook(task2, "COOK_002", "SHIFT_001", 45, 1);
        
        taskController.markTaskCompleted(task1, "COOK_001");
        taskController.reportTaskIssue(task2, "COOK_002", "Problema");
        
        Map<String, Integer> stats = taskController.getTaskStatistics();
        
        assertEquals(3, (int) stats.get("total"));
        assertEquals(2, (int) stats.get("assigned"));
        assertEquals(1, (int) stats.get("completed"));
        assertEquals(1, (int) stats.get("withIssues"));
        assertEquals(1, (int) stats.get("unassigned"));
    }
    
    @Test
    @DisplayName("Test reset piano")
    public void testResetPlan() {
        String taskId = taskController.addExtraTask("Test task");
        taskController.assignTaskToCook(taskId, "COOK_001", "SHIFT_001", 30, 1);
        taskController.confirmTaskPlan();
        
        assertTrue(taskController.isPlanConfirmed(), "Il piano dovrebbe essere confermato");
        
        taskController.resetPlan();
        
        assertFalse(taskController.isPlanConfirmed(), "Il piano non dovrebbe più essere confermato");
    }
    
    @Test
    @DisplayName("Test modifiche dopo conferma piano")
    public void testModificationsAfterConfirmation() {
        String taskId = taskController.addExtraTask("Test task");
        taskController.confirmTaskPlan();
        
        assertThrows(IllegalStateException.class, () -> {
            taskController.addExtraTask("Nuovo task");
        }, "Non dovrebbe essere possibile aggiungere task dopo conferma");
        
        assertThrows(IllegalStateException.class, () -> {
            taskController.assignTaskToCook(taskId, "COOK_001", "SHIFT_001", 30, 1);
        }, "Non dovrebbe essere possibile assegnare task dopo conferma");
    }
}