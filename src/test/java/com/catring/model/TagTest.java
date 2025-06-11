package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    
    private Tag tag;
    
    @BeforeEach
    void setUp() {
        tag = new Tag("vegetariano");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("vegetariano", tag.getNome());
    }
    
    @Test
    void testCostruttoreVuoto() {
        Tag tagVuoto = new Tag();
        assertNull(tagVuoto.getNome());
    }
    
    @Test
    void testModificaNome() {
        tag.setNome("vegano");
        assertEquals("vegano", tag.getNome());
    }
}