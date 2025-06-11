package com.catring.information_expert;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final AtomicLong menuCounter = new AtomicLong(1000);
    private static final AtomicLong sezioneCounter = new AtomicLong(1000);
    private static final AtomicLong voceCounter = new AtomicLong(1000);
    private static final AtomicLong ricettaCounter = new AtomicLong(1000);
    private static final AtomicLong eventoCounter = new AtomicLong(1000);
    private static final AtomicLong clienteCounter = new AtomicLong(1000);

    public String generateMenuId() {
        return "M" + menuCounter.incrementAndGet();
    }

    public String generateSezioneId() {
        return "S" + sezioneCounter.incrementAndGet();
    }

    public String generateVoceId() {
        return "V" + voceCounter.incrementAndGet();
    }

    public String generateRicettaId() {
        return "R" + ricettaCounter.incrementAndGet();
    }

    public String generateEventoId() {
        return "E" + eventoCounter.incrementAndGet();
    }

    public String generateClienteId() {
        return "C" + clienteCounter.incrementAndGet();
    }

    public void resetCounters() {
        menuCounter.set(1000);
        sezioneCounter.set(1000);
        voceCounter.set(1000);
        ricettaCounter.set(1000);
        eventoCounter.set(1000);
        clienteCounter.set(1000);
    }
}