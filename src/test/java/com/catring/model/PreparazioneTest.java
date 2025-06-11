package com.catring.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PreparazioneTest {
    
    private Preparazione preparazione;
    
    @BeforeEach
    void setUp() {
        preparazione = new Preparazione("P001", "Soffritto base", "Preparazione del soffritto con cipolla e aglio", 10, "Attenzione a non bruciare", "pubblicata", "Chef Mario");
    }
    
    @Test
    void testCostruttore() {
        assertEquals("P001", preparazione.getId());
        assertEquals("Soffritto base", preparazione.getNome());
        assertEquals("Preparazione del soffritto con cipolla e aglio", preparazione.getDescrizione());
        assertEquals(10, preparazione.getTempoEsecuzione());
        assertEquals("Attenzione a non bruciare", preparazione.getNote());
        assertEquals("pubblicata", preparazione.getStato());
        assertEquals("Chef Mario", preparazione.getAutore());
    }
    
    @Test
    void testCostruttoreVuoto() {
        Preparazione prepVuota = new Preparazione();
        
        assertNull(prepVuota.getId());
        assertNull(prepVuota.getNome());
        assertNull(prepVuota.getDescrizione());
        assertEquals(0, prepVuota.getTempoEsecuzione());
        assertNull(prepVuota.getNote());
        assertNull(prepVuota.getStato());
        assertNull(prepVuota.getAutore());
    }
    
    @Test
    void testModificaNome() {
        preparazione.setNome("Soffritto speciale");
        assertEquals("Soffritto speciale", preparazione.getNome());
    }
    
    @Test
    void testModificaDescrizione() {
        String nuovaDescrizione = "Soffritto con cipolla, aglio e sedano per base di sugo";
        preparazione.setDescrizione(nuovaDescrizione);
        assertEquals(nuovaDescrizione, preparazione.getDescrizione());
    }
    
    @Test
    void testModificaTempoEsecuzione() {
        preparazione.setTempoEsecuzione(15);
        assertEquals(15, preparazione.getTempoEsecuzione());
        
        preparazione.setTempoEsecuzione(5);
        assertEquals(5, preparazione.getTempoEsecuzione());
    }
    
    @Test
    void testTempoEsecuzioneZero() {
        preparazione.setTempoEsecuzione(0);
        assertEquals(0, preparazione.getTempoEsecuzione());
    }
    
    @Test
    void testModificaNote() {
        String nuoveNote = "Usare fuoco medio-basso per evitare di bruciare";
        preparazione.setNote(nuoveNote);
        assertEquals(nuoveNote, preparazione.getNote());
    }
    
    @Test
    void testNoteVuote() {
        preparazione.setNote("");
        assertEquals("", preparazione.getNote());
        
        preparazione.setNote(null);
        assertNull(preparazione.getNote());
    }
    
    @Test
    void testModificaStato() {
        preparazione.setStato("bozza");
        assertEquals("bozza", preparazione.getStato());
        
        preparazione.setStato("pubblicata");
        assertEquals("pubblicata", preparazione.getStato());
        
        preparazione.setStato("archiviata");
        assertEquals("archiviata", preparazione.getStato());
    }
    
    @Test
    void testModificaAutore() {
        preparazione.setAutore("Chef Luigi");
        assertEquals("Chef Luigi", preparazione.getAutore());
    }
    
    @Test
    void testModificaId() {
        preparazione.setId("P999");
        assertEquals("P999", preparazione.getId());
    }
    
    @Test
    void testPreparazioneCompleta() {
        // Test di una preparazione con tutti i campi compilati
        Preparazione prepCompleta = new Preparazione(
            "P002", 
            "Besciamella", 
            "Salsa bianca a base di burro, farina e latte", 
            20, 
            "Mescolare continuamente per evitare grumi", 
            "pubblicata", 
            "Chef Anna"
        );
        
        assertEquals("P002", prepCompleta.getId());
        assertEquals("Besciamella", prepCompleta.getNome());
        assertEquals("Salsa bianca a base di burro, farina e latte", prepCompleta.getDescrizione());
        assertEquals(20, prepCompleta.getTempoEsecuzione());
        assertEquals("Mescolare continuamente per evitare grumi", prepCompleta.getNote());
        assertEquals("pubblicata", prepCompleta.getStato());
        assertEquals("Chef Anna", prepCompleta.getAutore());
    }
    
    @Test
    void testDiversiTipiPreparazioni() {
        // Test per diversi tipi di preparazioni
        Preparazione brodo = new Preparazione("P003", "Brodo vegetale", "Brodo con verdure miste", 60, "", "pubblicata", "Chef Marco");
        Preparazione pasta = new Preparazione("P004", "Pasta fresca", "Impasto per pasta all'uovo", 30, "Lasciare riposare l'impasto", "bozza", "Chef Sofia");
        
        assertEquals("Brodo vegetale", brodo.getNome());
        assertEquals(60, brodo.getTempoEsecuzione());
        assertEquals("", brodo.getNote());
        
        assertEquals("Pasta fresca", pasta.getNome());
        assertEquals(30, pasta.getTempoEsecuzione());
        assertEquals("bozza", pasta.getStato());
    }
    
    @Test
    void testValidazioneStati() {
        // Test per verificare che si possano impostare diversi stati
        String[] statiValidi = {"bozza", "pubblicata", "archiviata", "in_revisione", "approvata"};
        
        for (String stato : statiValidi) {
            preparazione.setStato(stato);
            assertEquals(stato, preparazione.getStato());
        }
    }
    
    @Test
    void testTempiEsecuzioneVariabili() {
        // Test con diversi tempi di esecuzione
        int[] tempi = {1, 5, 10, 30, 60, 120, 180};
        
        for (int tempo : tempi) {
            preparazione.setTempoEsecuzione(tempo);
            assertEquals(tempo, preparazione.getTempoEsecuzione());
        }
    }
    
    @Test
    void testPreparazioneConNoteComplesse() {
        String noteComplesse = "1. Scaldare il burro in padella\n" +
                              "2. Aggiungere la farina e mescolare\n" +
                              "3. Versare il latte poco alla volta\n" +
                              "4. Cuocere fino a addensamento\n" +
                              "ATTENZIONE: Non far bollire mai!";
        
        preparazione.setNote(noteComplesse);
        assertEquals(noteComplesse, preparazione.getNote());
        assertTrue(preparazione.getNote().contains("ATTENZIONE"));
        assertTrue(preparazione.getNote().contains("1."));
    }
    
    @Test
    void testClonePreparazione() {
        // Test per verificare che si possano creare copie di preparazioni
        Preparazione copia = new Preparazione(
            "P001_COPY",
            preparazione.getNome() + " - Copia",
            preparazione.getDescrizione(),
            preparazione.getTempoEsecuzione(),
            preparazione.getNote(),
            "bozza", // Nuova copia inizia come bozza
            preparazione.getAutore()
        );
        
        assertEquals("P001_COPY", copia.getId());
        assertEquals("Soffritto base - Copia", copia.getNome());
        assertEquals(preparazione.getDescrizione(), copia.getDescrizione());
        assertEquals(preparazione.getTempoEsecuzione(), copia.getTempoEsecuzione());
        assertEquals(preparazione.getNote(), copia.getNote());
        assertEquals("bozza", copia.getStato());
        assertEquals(preparazione.getAutore(), copia.getAutore());
    }
}