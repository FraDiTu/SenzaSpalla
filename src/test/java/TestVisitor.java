import com.saslab.visitor.*;
import com.saslab.model.*;
import com.saslab.Recipe;
import com.saslab.RecipeBook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Map;

/**
 * Test class per Visitor pattern
 * Verifica il corretto funzionamento dei visitor sui menù
 */
public class TestVisitor {
    
    private Menu testMenu;
    private RecipeBook recipeBook;
    
    @BeforeEach
   public void setUp() {
       recipeBook = RecipeBook.getInstance();
       
       // Crea ricette di test
       Recipe pasta = new Recipe("RCP_V001", "Pasta al Pomodoro", "CHF_001", 30);
       pasta.addIngredient("Pasta", 320, "g");
       pasta.addIngredient("Pomodori", 400, "g");
       pasta.publish();
       recipeBook.addRecipe(pasta);
       
       Recipe tiramisu = new Recipe("RCP_V002", "Tiramisù", "CHF_001", 45);
       tiramisu.addIngredient("Mascarpone", 500, "g");
       tiramisu.addIngredient("Savoiardi", 300, "g");
       tiramisu.addIngredient("Caffè", 200, "ml");
       tiramisu.publish();
       recipeBook.addRecipe(tiramisu);
       
       // Crea menu di test
       testMenu = new Menu("MNU_V001", "Menu Test Visitor", "CHF_001");
       testMenu.defineMenuSections(Arrays.asList("Primi", "Dolci"));
       
       // Aggiungi ricette alle sezioni
       MenuSection primi = testMenu.findSectionByName("Primi");
       primi.addMenuItem(new MenuItem("MI_001", "RCP_V001", "Pasta al Pomodoro"));
       
       MenuSection dolci = testMenu.findSectionByName("Dolci");
       MenuItem tiramisuItem = new MenuItem("MI_002", "RCP_V002", "Tiramisù");
       tiramisuItem.setPrice(8.50);
       dolci.addMenuItem(tiramisuItem);
   }
   
   @Test
   @DisplayName("Test Menu Price Calculator Visitor")
   public void testMenuPriceCalculatorVisitor() {
       MenuPriceCalculatorVisitor priceVisitor = new MenuPriceCalculatorVisitor();
       
       priceVisitor.visitMenu(testMenu);
       
       double totalPrice = priceVisitor.getTotalPrice();
       assertTrue(totalPrice > 0, "Il prezzo totale dovrebbe essere maggiore di 0");
       
       Map<String, Double> sectionPrices = priceVisitor.getSectionPrices();
       assertTrue(sectionPrices.containsKey("Primi"));
       assertTrue(sectionPrices.containsKey("Dolci"));
       
       String report = priceVisitor.generatePriceReport();
       assertTrue(report.contains("ANALISI PREZZI MENÙ"));
       assertTrue(report.contains("Primi"));
       assertTrue(report.contains("Dolci"));
   }
   
   @Test
   @DisplayName("Test Menu Export Visitor - Plain Text")
   public void testMenuExportVisitorPlainText() {
       MenuExportVisitor exportVisitor = new MenuExportVisitor(MenuExportVisitor.ExportFormat.PLAIN_TEXT);
       
       exportVisitor.visitMenu(testMenu);
       
       String exported = exportVisitor.getExportedContent();
       assertTrue(exported.contains("MENU TEST VISITOR"));
       assertTrue(exported.contains("PRIMI"));
       assertTrue(exported.contains("Pasta al Pomodoro"));
       assertTrue(exported.contains("DOLCI"));
       assertTrue(exported.contains("Tiramisù"));
   }
   
   @Test
   @DisplayName("Test Menu Export Visitor - HTML")
   public void testMenuExportVisitorHTML() {
       MenuExportVisitor exportVisitor = new MenuExportVisitor(MenuExportVisitor.ExportFormat.HTML);
       
       exportVisitor.visitMenu(testMenu);
       
       String exported = exportVisitor.getExportedContent();
       assertTrue(exported.contains("<!DOCTYPE html>"));
       assertTrue(exported.contains("<h1>Menu Test Visitor</h1>"));
       assertTrue(exported.contains("<h2>Primi</h2>"));
       assertTrue(exported.contains("<strong>Pasta al Pomodoro</strong>"));
       assertTrue(exported.contains("€8.50")); // Prezzo del tiramisù
   }
   
   @Test
   @DisplayName("Test Menu Export Visitor - Markdown")
   public void testMenuExportVisitorMarkdown() {
       MenuExportVisitor exportVisitor = new MenuExportVisitor(MenuExportVisitor.ExportFormat.MARKDOWN);
       
       exportVisitor.visitMenu(testMenu);
       
       String exported = exportVisitor.getExportedContent();
       assertTrue(exported.contains("# Menu Test Visitor"));
       assertTrue(exported.contains("## Primi"));
       assertTrue(exported.contains("**Pasta al Pomodoro**"));
       assertTrue(exported.contains("**Tiramisù** - €8.50"));
   }
   
   @Test
   @DisplayName("Test caratteristiche menù influenzano prezzo")
   public void testMenuCharacteristicsAffectPrice() {
       Menu luxuryMenu = new Menu("MNU_LUX", "Menu Luxury", "CHF_001");
       luxuryMenu.setRequiresChefPresence(true);
       luxuryMenu.setRequiresKitchenOnSite(true);
       luxuryMenu.defineMenuSections(Arrays.asList("Antipasti"));
       
       MenuPriceCalculatorVisitor priceVisitor = new MenuPriceCalculatorVisitor();
       priceVisitor.visitMenu(luxuryMenu);
       
       double totalPrice = priceVisitor.getTotalPrice();
       assertTrue(totalPrice >= 250.0, "Il prezzo dovrebbe includere i costi extra per chef e cucina");
   }
}