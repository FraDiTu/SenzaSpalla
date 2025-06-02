package com.saslab;

/**
 * Classe utility per calcoli nel sistema di catering
 * Implementa pattern Information Expert per centralizzare i calcoli
 */
public class Calculator {
    
    /**
     * Calcola il tempo totale necessario per un insieme di preparazioni
     */
    public static int calculateTotalPreparationTime(int... times) {
        if (times == null || times.length == 0) {
            return 0;
        }
        
        int total = 0;
        for (int time : times) {
            if (time < 0) {
                throw new IllegalArgumentException("Il tempo non può essere negativo");
            }
            total += time;
        }
        return total;
    }
    
    /**
     * Calcola le porzioni scalate in base al numero di persone
     */
    public static double calculateScaledPortions(double basePortions, int basePeople, int targetPeople) {
        if (basePeople <= 0 || targetPeople <= 0) {
            throw new IllegalArgumentException("Il numero di persone deve essere positivo");
        }
        if (basePortions < 0) {
            throw new IllegalArgumentException("Le porzioni base non possono essere negative");
        }
        
        return (basePortions * targetPeople) / basePeople;
    }
    
    /**
     * Calcola il costo approssimativo per persona
     */
    public static double calculateCostPerPerson(double totalCost, int numberOfPeople) {
        if (numberOfPeople <= 0) {
            throw new IllegalArgumentException("Il numero di persone deve essere positivo");
        }
        if (totalCost < 0) {
            throw new IllegalArgumentException("Il costo totale non può essere negativo");
        }
        
        return totalCost / numberOfPeople;
    }
    
    /**
     * Calcola se un cuoco può gestire un compito aggiuntivo in base al tempo disponibile
     */
    public static boolean canHandleAdditionalTask(int currentWorkload, int additionalTask, int maxCapacity) {
        if (currentWorkload < 0 || additionalTask < 0 || maxCapacity <= 0) {
            throw new IllegalArgumentException("I parametri devono essere validi");
        }
        
        return (currentWorkload + additionalTask) <= maxCapacity;
    }
    
    /**
     * Calcola la percentuale di completamento di un evento
     */
    public static double calculateCompletionPercentage(int completedTasks, int totalTasks) {
        if (totalTasks <= 0) {
            throw new IllegalArgumentException("Il numero totale di compiti deve essere positivo");
        }
        if (completedTasks < 0 || completedTasks > totalTasks) {
            throw new IllegalArgumentException("Il numero di compiti completati non è valido");
        }
        
        return (double) completedTasks / totalTasks * 100.0;
    }
    
    /**
     * Calcola il buffer di tempo consigliato per un evento
     */
    public static int calculateTimeBuffer(int estimatedTime, double bufferPercentage) {
        if (estimatedTime < 0) {
            throw new IllegalArgumentException("Il tempo stimato non può essere negativo");
        }
        if (bufferPercentage < 0 || bufferPercentage > 1) {
            throw new IllegalArgumentException("La percentuale di buffer deve essere tra 0 e 1");
        }
        
        return (int) (estimatedTime * bufferPercentage);
    }
}