package com.saslab;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Classe utility per calcoli nel sistema di catering
 * Implementa pattern Information Expert per centralizzare i calcoli
 * Versione corretta con protezione overflow e validazione robusta
 */
public class Calculator {
    
    private static final int MAX_TIME_MINUTES = 86400; // 24 ore max
    private static final double MAX_PORTIONS = 100000.0; // Limite ragionevole
    private static final double MAX_COST = 1000000.0; // 1 milione max
    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);
    
    /**
     * Calcola il tempo totale necessario per un insieme di preparazioni
     * Protetto contro overflow e valori negativi
     */
    public static int calculateTotalPreparationTime(int... times) {
        if (times == null || times.length == 0) {
            return 0;
        }
        
        long total = 0;
        for (int time : times) {
            if (time < 0) {
                throw new IllegalArgumentException("Il tempo non può essere negativo: " + time);
            }
            if (time > MAX_TIME_MINUTES) {
                throw new IllegalArgumentException("Tempo troppo grande: " + time + " minuti");
            }
            
            total += time;
            
            // Controlla overflow
            if (total > MAX_TIME_MINUTES) {
                throw new ArithmeticException("Tempo totale troppo grande: " + total + " minuti");
            }
        }
        
        return (int) total;
    }
    
    /**
     * Calcola le porzioni scalate in base al numero di persone
     * Utilizza BigDecimal per prevenire overflow con numeri grandi
     */
    public static double calculateScaledPortions(double basePortions, int basePeople, int targetPeople) {
        if (basePeople <= 0) {
            throw new IllegalArgumentException("Il numero di persone base deve essere positivo: " + basePeople);
        }
        if (targetPeople <= 0) {
            throw new IllegalArgumentException("Il numero di persone target deve essere positivo: " + targetPeople);
        }
        if (basePortions < 0) {
            throw new IllegalArgumentException("Le porzioni base non possono essere negative: " + basePortions);
        }
        if (basePortions > MAX_PORTIONS) {
            throw new IllegalArgumentException("Porzioni base troppo grandi: " + basePortions);
        }
        if (targetPeople > 100000) {
            throw new IllegalArgumentException("Numero di persone target troppo grande: " + targetPeople);
        }
        
        // Usa BigDecimal per calcoli precisi
        BigDecimal base = BigDecimal.valueOf(basePortions);
        BigDecimal target = BigDecimal.valueOf(targetPeople);
        BigDecimal basePeopleDecimal = BigDecimal.valueOf(basePeople);
        
        BigDecimal result = base.multiply(target, MATH_CONTEXT)
                               .divide(basePeopleDecimal, MATH_CONTEXT);
        
        double finalResult = result.doubleValue();
        
        if (finalResult > MAX_PORTIONS) {
            throw new ArithmeticException("Risultato troppo grande: " + finalResult);
        }
        
        return finalResult;
    }
    
    /**
     * Calcola il costo approssimativo per persona
     * Protetto contro division by zero e overflow
     */
    public static double calculateCostPerPerson(double totalCost, int numberOfPeople) {
        if (numberOfPeople <= 0) {
            throw new IllegalArgumentException("Il numero di persone deve essere positivo: " + numberOfPeople);
        }
        if (totalCost < 0) {
            throw new IllegalArgumentException("Il costo totale non può essere negativo: " + totalCost);
        }
        if (totalCost > MAX_COST) {
            throw new IllegalArgumentException("Costo totale troppo grande: " + totalCost);
        }
        if (numberOfPeople > 100000) {
            throw new IllegalArgumentException("Numero di persone troppo grande: " + numberOfPeople);
        }
        
        // Usa BigDecimal per precisione
        BigDecimal cost = BigDecimal.valueOf(totalCost);
        BigDecimal people = BigDecimal.valueOf(numberOfPeople);
        
        return cost.divide(people, MATH_CONTEXT).doubleValue();
    }
    
    /**
     * Calcola se un cuoco può gestire un compito aggiuntivo in base al tempo disponibile
     * Validazione migliorata dei parametri
     */
    public static boolean canHandleAdditionalTask(int currentWorkload, int additionalTask, int maxCapacity) {
        if (currentWorkload < 0) {
            throw new IllegalArgumentException("Il carico di lavoro attuale non può essere negativo: " + currentWorkload);
        }
        if (additionalTask < 0) {
            throw new IllegalArgumentException("Il compito aggiuntivo non può essere negativo: " + additionalTask);
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("La capacità massima deve essere positiva: " + maxCapacity);
        }
        if (currentWorkload > maxCapacity) {
            throw new IllegalArgumentException("Il carico attuale supera già la capacità massima");
        }
        
        // Controlla overflow nella somma
        try {
            int totalWorkload = Math.addExact(currentWorkload, additionalTask);
            return totalWorkload <= maxCapacity;
        } catch (ArithmeticException e) {
            // In caso di overflow, sicuramente supera la capacità
            return false;
        }
    }
    
    /**
     * Calcola la percentuale di completamento di un evento
     * Validazione migliorata e gestione edge cases
     */
    public static double calculateCompletionPercentage(int completedTasks, int totalTasks) {
        if (totalTasks < 0) {
            throw new IllegalArgumentException("Il numero totale di compiti non può essere negativo: " + totalTasks);
        }
        if (completedTasks < 0) {
            throw new IllegalArgumentException("Il numero di compiti completati non può essere negativo: " + completedTasks);
        }
        if (completedTasks > totalTasks) {
            throw new IllegalArgumentException("Il numero di compiti completati non può superare il totale");
        }
        
        if (totalTasks == 0) {
            return 100.0; // Convenzione: se non ci sono compiti, è completato al 100%
        }
        
        // Usa BigDecimal per precisione
        BigDecimal completed = BigDecimal.valueOf(completedTasks);
        BigDecimal total = BigDecimal.valueOf(totalTasks);
        BigDecimal hundred = BigDecimal.valueOf(100.0);
        
        return completed.divide(total, MATH_CONTEXT)
                       .multiply(hundred, MATH_CONTEXT)
                       .doubleValue();
    }
    
    /**
     * Calcola il buffer di tempo consigliato per un evento
     * Validazione migliorata dei parametri
     */
    public static int calculateTimeBuffer(int estimatedTime, double bufferPercentage) {
        if (estimatedTime < 0) {
            throw new IllegalArgumentException("Il tempo stimato non può essere negativo: " + estimatedTime);
        }
        if (estimatedTime > MAX_TIME_MINUTES) {
            throw new IllegalArgumentException("Tempo stimato troppo grande: " + estimatedTime);
        }
        if (bufferPercentage < 0 || bufferPercentage > 1) {
            throw new IllegalArgumentException("La percentuale di buffer deve essere tra 0 e 1: " + bufferPercentage);
        }
        
        // Usa BigDecimal per calcolo preciso
        BigDecimal time = BigDecimal.valueOf(estimatedTime);
        BigDecimal percentage = BigDecimal.valueOf(bufferPercentage);
        
        BigDecimal buffer = time.multiply(percentage, MATH_CONTEXT);
        
        return buffer.intValue();
    }
    
    /**
     * Calcola il tempo medio per compito
     */
    public static double calculateAverageTimePerTask(int totalTime, int numberOfTasks) {
        if (numberOfTasks <= 0) {
            throw new IllegalArgumentException("Il numero di compiti deve essere positivo");
        }
        if (totalTime < 0) {
            throw new IllegalArgumentException("Il tempo totale non può essere negativo");
        }
        
        return calculateCostPerPerson(totalTime, numberOfTasks);
    }
    
    /**
     * Calcola il numero ottimale di cuochi per un carico di lavoro
     */
    public static int calculateOptimalCookCount(int totalWorkloadMinutes, int maxWorkloadPerCookMinutes) {
        if (totalWorkloadMinutes < 0) {
            throw new IllegalArgumentException("Il carico di lavoro totale non può essere negativo");
        }
        if (maxWorkloadPerCookMinutes <= 0) {
            throw new IllegalArgumentException("Il carico massimo per cuoco deve essere positivo");
        }
        
        if (totalWorkloadMinutes == 0) {
            return 0;
        }
        
        // Arrotonda per eccesso per garantire copertura completa
        return (int) Math.ceil((double) totalWorkloadMinutes / maxWorkloadPerCookMinutes);
    }
    
    /**
     * Verifica se due intervalli di tempo si sovrappongono
     */
    public static boolean hasTimeOverlap(int start1, int end1, int start2, int end2) {
        if (start1 < 0 || end1 < 0 || start2 < 0 || end2 < 0) {
            throw new IllegalArgumentException("I tempi non possono essere negativi");
        }
        if (start1 >= end1 || start2 >= end2) {
            throw new IllegalArgumentException("L'orario di inizio deve essere precedente a quello di fine");
        }
        
        return start1 < end2 && start2 < end1;
    }
}