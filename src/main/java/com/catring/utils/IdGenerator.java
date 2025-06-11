package com.catring.utils;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong menuCounter = new AtomicLong(1000);
    private static final AtomicLong sezioneCounter = new AtomicLong(1000);
    private static final AtomicLong voceCounter = new AtomicLong(1000);
    private static final AtomicLong ricettaCounter = new AtomicLong(1000);
    
    public static String generateMenuId() {
        return "M" + menuCounter.incrementAndGet();
    }
    
    public static String generateSezioneId() {
        return "S" + sezioneCounter.incrementAndGet();
    }
    
    public static String generateVoceId() {
        return "V" + voceCounter.incrementAndGet();
    }
    
    public static String generateRicettaId() {
        return "R" + ricettaCounter.incrementAndGet();
    }
}