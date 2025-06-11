package com.catring.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {
    
    @Test
    void testIsValidString() {
        assertTrue(ValidationUtils.isValidString("test"));
        assertTrue(ValidationUtils.isValidString("  test  "));
        assertFalse(ValidationUtils.isValidString(""));
        assertFalse(ValidationUtils.isValidString("   "));
        assertFalse(ValidationUtils.isValidString(null));
    }

    
    @Test
    void testIsValidId() {
        assertTrue(ValidationUtils.isValidId("M001"));
        assertTrue(ValidationUtils.isValidId("MENU123"));
        assertFalse(ValidationUtils.isValidId("AB"));
        assertFalse(ValidationUtils.isValidId(""));
        assertFalse(ValidationUtils.isValidId(null));
    }
    
    @Test
    void testSanitizeString() {
        assertEquals("test", ValidationUtils.sanitizeString("  test  "));
        assertEquals("hello world", ValidationUtils.sanitizeString("hello world"));
        assertEquals("", ValidationUtils.sanitizeString(""));
        assertEquals("", ValidationUtils.sanitizeString("   "));
        assertEquals("", ValidationUtils.sanitizeString(null));
    }
}