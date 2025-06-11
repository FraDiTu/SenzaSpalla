package com.catring.information_expert;

import java.util.concurrent.atomic.AtomicLong;

/**
 * PATTERN GRASP: INFORMATION EXPERT
 * Questa classe è l'esperto per la generazione di ID univoci.
 * Ha tutte le informazioni necessarie per generare ID corretti.
 */
public class IdGenerator {
    
    // Contatori per garantire unicità
    private static final AtomicLong menuCounter = new AtomicLong(1000);
    private static final AtomicLong sezioneCounter = new AtomicLong(1000);
    private static final AtomicLong voceCounter = new AtomicLong(1000);
    private static final AtomicLong ricettaCounter = new AtomicLong(1000);
    private static final AtomicLong eventoCounter = new AtomicLong(1000);
    private static final AtomicLong clienteCounter = new AtomicLong(1000);
    
    /**
     * Genera un ID univoco per Menu
     */
    public String generateMenuId() {
        return "M" + menuCounter.incrementAndGet();
    }
    
    /**
     * Genera un ID univoco per Sezione Menu
     */
    public String generateSezioneId() {
        return "S" + sezioneCounter.incrementAndGet();
    }
    
    /**
     * Genera un ID univoco per Voce Menu
     */
    public String generateVoceId() {
        return "V" + voceCounter.incrementAndGet();
    }
    
    /**
     * Genera un ID univoco per Ricetta
     */
    public String generateRicettaId() {
        return "R" + ricettaCounter.incrementAndGet();
    }
    
    /**
     * Genera un ID univoco per Evento
     */
    public String generateEventoId() {
        return "E" + eventoCounter.incrementAndGet();
    }
    
    /**
     * Genera un ID univoco per Cliente
     */
    public String generateClienteId() {
        return "C" + clienteCounter.incrementAndGet();
    }
    
    /**
     * Resetta tutti i contatori (utile per i test)
     */
    public void resetCounters() {
        menuCounter.set(1000);
        sezioneCounter.set(1000);
        voceCounter.set(1000);
        ricettaCounter.set(1000);
        eventoCounter.set(1000);
        clienteCounter.set(1000);
    }
}
