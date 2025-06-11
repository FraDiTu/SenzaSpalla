package com.catring.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {
    
    @Test
    void testGenerateMenuId() {
        String id1 = IdGenerator.generateMenuId();
        String id2 = IdGenerator.generateMenuId();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(id1.startsWith("M"));
        assertTrue(id2.startsWith("M"));
        assertNotEquals(id1, id2);
    }
    
    @Test
    void testGenerateSezioneId() {
        String id1 = IdGenerator.generateSezioneId();
        String id2 = IdGenerator.generateSezioneId();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(id1.startsWith("S"));
        assertTrue(id2.startsWith("S"));
        assertNotEquals(id1, id2);
    }
    
    @Test
    void testGenerateVoceId() {
        String id1 = IdGenerator.generateVoceId();
        String id2 = IdGenerator.generateVoceId();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(id1.startsWith("V"));
        assertTrue(id2.startsWith("V"));
        assertNotEquals(id1, id2);
    }
    
    @Test
    void testGenerateRicettaId() {
        String id1 = IdGenerator.generateRicettaId();
        String id2 = IdGenerator.generateRicettaId();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(id1.startsWith("R"));
        assertTrue(id2.startsWith("R"));
        assertNotEquals(id1, id2);
    }
}