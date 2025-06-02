import com.saslab.Calculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class per Calculator
 * Verifica il corretto funzionamento dei calcoli nel sistema Cat &amp; Ring
 */
public class TestCalculator {
    
    @Test
    @DisplayName("Test calcolo tempo totale preparazione - caso normale")
    public void testCalculateTotalPreparationTime_Normal() {
        int result = Calculator.calculateTotalPreparationTime(30, 45, 20);
        assertEquals(95, result, "Il tempo totale dovrebbe essere 95 minuti");
    }
    
    @Test
    @DisplayName("Test calcolo tempo totale preparazione - array vuoto")
    public void testCalculateTotalPreparationTime_EmptyArray() {
        int result = Calculator.calculateTotalPreparationTime();
        assertEquals(0, result, "Con array vuoto il tempo dovrebbe essere 0");
    }
    
    @Test
    @DisplayName("Test calcolo tempo totale preparazione - array null")
    public void testCalculateTotalPreparationTime_NullArray() {
        int result = Calculator.calculateTotalPreparationTime((int[]) null);
        assertEquals(0, result, "Con array null il tempo dovrebbe essere 0");
    }
    
    @Test
    @DisplayName("Test calcolo tempo totale preparazione - tempo negativo")
    public void testCalculateTotalPreparationTime_NegativeTime() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateTotalPreparationTime(30, -10, 20);
        }, "Dovrebbe lanciare eccezione per tempo negativo");
    }
    
    @Test
    @DisplayName("Test calcolo porzioni scalate - caso normale")
    public void testCalculateScaledPortions_Normal() {
        double result = Calculator.calculateScaledPortions(4.0, 10, 25);
        assertEquals(10.0, result, 0.01, "4 porzioni per 10 persone scalate a 25 persone dovrebbero essere 10");
    }
    
    @Test
    @DisplayName("Test calcolo porzioni scalate - stesso numero persone")
    public void testCalculateScaledPortions_SamePeople() {
        double result = Calculator.calculateScaledPortions(6.0, 15, 15);
        assertEquals(6.0, result, 0.01, "Con stesso numero di persone le porzioni non cambiano");
    }
    
    @Test
    @DisplayName("Test calcolo porzioni scalate - persone negative")
    public void testCalculateScaledPortions_NegativePeople() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateScaledPortions(4.0, -5, 10);
        }, "Dovrebbe lanciare eccezione per numero persone negativo");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateScaledPortions(4.0, 10, -5);
        }, "Dovrebbe lanciare eccezione per numero persone target negativo");
    }
    
    @Test
    @DisplayName("Test calcolo porzioni scalate - porzioni negative")
    public void testCalculateScaledPortions_NegativePortions() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateScaledPortions(-2.0, 10, 15);
        }, "Dovrebbe lanciare eccezione per porzioni negative");
    }
    
    @Test
    @DisplayName("Test calcolo costo per persona - caso normale")
    public void testCalculateCostPerPerson_Normal() {
        double result = Calculator.calculateCostPerPerson(1000.0, 50);
        assertEquals(20.0, result, 0.01, "1000 euro per 50 persone dovrebbero essere 20 euro a persona");
    }
    
    @Test
    @DisplayName("Test calcolo costo per persona - una persona")
    public void testCalculateCostPerPerson_OnePerson() {
        double result = Calculator.calculateCostPerPerson(150.0, 1);
        assertEquals(150.0, result, 0.01, "150 euro per 1 persona dovrebbero essere 150 euro");
    }
    
    @Test
    @DisplayName("Test calcolo costo per persona - costo zero")
    public void testCalculateCostPerPerson_ZeroCost() {
        double result = Calculator.calculateCostPerPerson(0.0, 10);
        assertEquals(0.0, result, 0.01, "Costo zero dovrebbe risultare in 0 per persona");
    }
    
    @Test
    @DisplayName("Test calcolo costo per persona - parametri invalidi")
    public void testCalculateCostPerPerson_InvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateCostPerPerson(100.0, 0);
        }, "Dovrebbe lanciare eccezione per zero persone");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateCostPerPerson(-100.0, 5);
        }, "Dovrebbe lanciare eccezione per costo negativo");
    }
    
    @Test
    @DisplayName("Test verifica capacità cuoco - può gestire compito aggiuntivo")
    public void testCanHandleAdditionalTask_CanHandle() {
        boolean result = Calculator.canHandleAdditionalTask(180, 60, 300);
        assertTrue(result, "Cuoco con 180 min di carico dovrebbe poter gestire altri 60 min con capacità 300");
    }
    
    @Test
    @DisplayName("Test verifica capacità cuoco - non può gestire compito aggiuntivo")
    public void testCanHandleAdditionalTask_CannotHandle() {
        boolean result = Calculator.canHandleAdditionalTask(250, 100, 300);
        assertFalse(result, "Cuoco con 250 min di carico non dovrebbe poter gestire altri 100 min con capacità 300");
    }
    
    @Test
    @DisplayName("Test verifica capacità cuoco - esattamente al limite")
    public void testCanHandleAdditionalTask_ExactLimit() {
        boolean result = Calculator.canHandleAdditionalTask(200, 100, 300);
        assertTrue(result, "Cuoco con 200 min + 100 min dovrebbe raggiungere esattamente la capacità 300");
    }
    
    @Test
    @DisplayName("Test verifica capacità cuoco - parametri invalidi")
    public void testCanHandleAdditionalTask_InvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.canHandleAdditionalTask(-50, 60, 300);
        }, "Dovrebbe lanciare eccezione per carico attuale negativo");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.canHandleAdditionalTask(100, -30, 300);
        }, "Dovrebbe lanciare eccezione per compito aggiuntivo negativo");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.canHandleAdditionalTask(100, 50, 0);
        }, "Dovrebbe lanciare eccezione per capacità massima zero o negativa");
    }
    
    @Test
    @DisplayName("Test calcolo percentuale completamento - caso normale")
    public void testCalculateCompletionPercentage_Normal() {
        double result = Calculator.calculateCompletionPercentage(7, 10);
        assertEquals(70.0, result, 0.01, "7 compiti su 10 dovrebbero essere il 70%");
    }
    
    @Test
    @DisplayName("Test calcolo percentuale completamento - completamento totale")
    public void testCalculateCompletionPercentage_FullCompletion() {
        double result = Calculator.calculateCompletionPercentage(5, 5);
        assertEquals(100.0, result, 0.01, "5 compiti su 5 dovrebbero essere il 100%");
    }
    
    @Test
    @DisplayName("Test calcolo percentuale completamento - nessun completamento")
    public void testCalculateCompletionPercentage_NoCompletion() {
        double result = Calculator.calculateCompletionPercentage(0, 8);
        assertEquals(0.0, result, 0.01, "0 compiti su 8 dovrebbero essere lo 0%");
    }
    
    @Test
    @DisplayName("Test calcolo percentuale completamento - parametri invalidi")
    public void testCalculateCompletionPercentage_InvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateCompletionPercentage(5, 0);
        }, "Dovrebbe lanciare eccezione per totale compiti zero");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateCompletionPercentage(-2, 10);
        }, "Dovrebbe lanciare eccezione per compiti completati negativi");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateCompletionPercentage(12, 10);
        }, "Dovrebbe lanciare eccezione per compiti completati maggiori del totale");
    }
    
    @Test
    @DisplayName("Test calcolo buffer tempo - caso normale")
    public void testCalculateTimeBuffer_Normal() {
        int result = Calculator.calculateTimeBuffer(120, 0.2);
        assertEquals(24, result, "Buffer del 20% su 120 minuti dovrebbe essere 24 minuti");
    }
    
    @Test
    @DisplayName("Test calcolo buffer tempo - buffer zero")
    public void testCalculateTimeBuffer_ZeroBuffer() {
        int result = Calculator.calculateTimeBuffer(180, 0.0);
        assertEquals(0, result, "Buffer dello 0% dovrebbe essere 0 minuti");
    }
    
    @Test
    @DisplayName("Test calcolo buffer tempo - buffer massimo")
    public void testCalculateTimeBuffer_MaxBuffer() {
        int result = Calculator.calculateTimeBuffer(100, 1.0);
        assertEquals(100, result, "Buffer del 100% dovrebbe raddoppiare il tempo");
    }
    
    @Test
    @DisplayName("Test calcolo buffer tempo - parametri invalidi")
    public void testCalculateTimeBuffer_InvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateTimeBuffer(-60, 0.15);
        }, "Dovrebbe lanciare eccezione per tempo negativo");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateTimeBuffer(120, -0.1);
        }, "Dovrebbe lanciare eccezione per percentuale buffer negativa");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Calculator.calculateTimeBuffer(120, 1.5);
        }, "Dovrebbe lanciare eccezione per percentuale buffer maggiore di 1");
    }
    
    @Test
    @DisplayName("Test calcolo buffer tempo - numeri decimali")
    public void testCalculateTimeBuffer_DecimalNumbers() {
        int result = Calculator.calculateTimeBuffer(75, 0.33);
        assertEquals(24, result, "Buffer del 33% su 75 minuti dovrebbe essere 24 minuti (troncato)");
    }
}