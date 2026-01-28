package com.sistema.customer.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentNumberNormalizerTest {
    @Test
    void normalizeRemovesNonDigits() {
        assertEquals("12345678901", DocumentNumberNormalizer.normalize("123.456.789-01"));
        assertEquals("12345678000199", DocumentNumberNormalizer.normalize("12.345.678/0001-99"));
    }

    @Test
    void normalizeReturnsNullWhenEmptyOrBlank() {
        assertNull(DocumentNumberNormalizer.normalize(null));
        assertNull(DocumentNumberNormalizer.normalize(""));
        assertNull(DocumentNumberNormalizer.normalize("   "));
        assertNull(DocumentNumberNormalizer.normalize("..-"));
    }
}

