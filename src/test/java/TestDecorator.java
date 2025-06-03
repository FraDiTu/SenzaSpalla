import com.saslab.decorator.*;
import com.saslab.model.Menu;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class per Decorator pattern
 * Verifica il corretto funzionamento dei decoratori di menù
 */
public class TestDecorator {
    
    private Menu baseMenu;
    
    @BeforeEach
    public void setUp() {
        baseMenu = new Menu("MNU_001", "Menu Base", "CHF_001");
        baseMenu.setDescription("Un menu semplice per test");
    }
    
    @Test
    @DisplayName("Test Premium Menu Decorator")
    public void testPremiumMenuDecorator() {
        PremiumMenuDecorator premiumMenu = new PremiumMenuDecorator(baseMenu, "Chef Mario Rossi");
        
        String enhanced = premiumMenu.getEnhancedDescription();
        assertTrue(enhanced.contains("MENÙ PREMIUM"));
        assertTrue(enhanced.contains("Chef Mario Rossi"));
        assertTrue(enhanced.contains("Ingredienti di prima scelta"));
        
        assertEquals("Chef Mario Rossi", premiumMenu.getChefSignature());
        
        // Test calcolo prezzo premium
        double basePrice = 100.0;
        double premiumPrice = premiumMenu.calculatePremiumPrice(basePrice);
        assertEquals(150.0, premiumPrice, 0.01);
    }
    
    @Test
    @DisplayName("Test Themed Menu Decorator")
    public void testThemedMenuDecorator() {
        ThemedMenuDecorator themedMenu = new ThemedMenuDecorator(baseMenu, "Romantico");
        
        String enhanced = themedMenu.getEnhancedDescription();
        assertTrue(enhanced.contains("MENÙ A TEMA: ROMANTICO"));
        assertTrue(enhanced.contains("Rosso e Bianco"));
        assertTrue(enhanced.contains("Candele"));
        
        assertEquals("Romantico", themedMenu.getTheme());
        assertEquals("Rosso e Bianco", themedMenu.getColorScheme());
    }
    
    @Test
    @DisplayName("Test decoratori multipli")
    public void testMultipleDecorators() {
        // Si può creare un premium themed menu
        PremiumMenuDecorator premiumMenu = new PremiumMenuDecorator(baseMenu, "Chef Special");
        premiumMenu.setWineRecommendations("Barolo 2015, Champagne Dom Perignon");
        
        String description = premiumMenu.getEnhancedDescription();
        assertTrue(description.contains("Abbinamenti Vini Consigliati"));
        assertTrue(description.contains("Barolo 2015"));
    }
}