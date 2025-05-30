import com.saslab.controller.MenuController;
import com.saslab.controller.RecipeController;
import com.saslab.model.Menu;
import com.saslab.Recipe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

/**
 * Test class per MenuController
 * Verifica il corretto funzionamento della gestione dei menù
 */
public class TestMenuController {
    
    @Mock
    private RecipeController recipeController;
    
    private MenuController menuController;
    private Recipe testRecipe;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        menuController = new MenuController(recipeController);
        
        // Setup recipe mock
        testRecipe = new Recipe("RCP_001", "Pasta Test", "CHF001", 30);
        testRecipe.addIngredient("Pasta", 320, "g");
        testRecipe.publish();
    }
    
    @Test
    @DisplayName("Test creazione menù - caso normale")
    public void testCreateMenu_Normal() {
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        
        assertNotNull(menuId, "L'ID del menù dovrebbe essere generato");
        assertTrue(menuId.startsWith("MNU_"), "L'ID dovrebbe iniziare con MNU_");
        
        Menu menu = menuController.getMenu(menuId);
        assertNotNull(menu, "Il menù dovrebbe essere creato");
        assertEquals("Menu Test", menu.getName());
        assertEquals("CHF001", menu.getChefId());
        assertEquals(Menu.MenuState.DRAFT, menu.getState());
    }
    
    @Test
    @DisplayName("Test creazione menù - parametri invalidi")
    public void testCreateMenu_InvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            menuController.createMenu(null, "Menu Test");
        }, "Dovrebbe lanciare eccezione per chef ID null");
        
        assertThrows(IllegalArgumentException.class, () -> {
            menuController.createMenu("CHF001", "");
        }, "Dovrebbe lanciare eccezione per nome vuoto");
    }
    
    @Test
    @DisplayName("Test definizione sezioni menù - caso normale")
    public void testDefineMenuSections_Normal() {
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        List<String> sections = Arrays.asList("Antipasti", "Primi", "Secondi");
        
        menuController.defineMenuSections(menuId, sections);
        
        Menu menu = menuController.getMenu(menuId);
        assertEquals(3, menu.getSections().size());
        assertEquals("Antipasti", menu.getSections().get(0).getTitle());
        assertEquals("Primi", menu.getSections().get(1).getTitle());
        assertEquals("Secondi", menu.getSections().get(2).getTitle());
    }
    
    @Test
    @DisplayName("Test definizione sezioni menù - nomi duplicati")
    public void testDefineMenuSections_DuplicateNames() {
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        List<String> sections = Arrays.asList("Antipasti", "Primi", "Antipasti");
        
        assertThrows(IllegalArgumentException.class, () -> {
            menuController.defineMenuSections(menuId, sections);
        }, "Dovrebbe lanciare eccezione per nomi duplicati");
    }
    
    @Test
    @DisplayName("Test aggiunta ricetta a sezione - caso normale")
    public void testAddRecipeToSection_Normal() {
        // Setup
        when(recipeController.getRecipe("RCP_001")).thenReturn(testRecipe);
        
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        menuController.defineMenuSections(menuId, Arrays.asList("Primi"));
        
        // Test
        menuController.addRecipeToSection(menuId, "RCP_001", "Primi");
        
        Menu menu = menuController.getMenu(menuId);
        assertEquals(1, menu.getTotalMenuItems());
        assertNotNull(menu.findMenuItemByRecipeId("RCP_001"));
    }
    
    @Test
    @DisplayName("Test aggiunta ricetta a sezione - ricetta non pubblicata")
    public void testAddRecipeToSection_UnpublishedRecipe() {
        Recipe unpublishedRecipe = new Recipe("RCP_002", "Test", "CHF001", 20);
        unpublishedRecipe.addIngredient("Test", 100, "g");
        // Non pubblicata
        
        when(recipeController.getRecipe("RCP_002")).thenReturn(unpublishedRecipe);
        
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        menuController.defineMenuSections(menuId, Arrays.asList("Primi"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            menuController.addRecipeToSection(menuId, "RCP_002", "Primi");
        }, "Dovrebbe lanciare eccezione per ricetta non pubblicata");
    }
    
    @Test
    @DisplayName("Test spostamento ricetta tra sezioni")
    public void testMoveRecipesBetweenSection() {
        // Setup
        when(recipeController.getRecipe("RCP_001")).thenReturn(testRecipe);
        
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        menuController.defineMenuSections(menuId, Arrays.asList("Antipasti", "Primi"));
        menuController.addRecipeToSection(menuId, "RCP_001", "Antipasti");
        
        // Test
        menuController.moveRecipesBetweenSection(menuId, "RCP_001", "Antipasti", "Primi");
        
        Menu menu = menuController.getMenu(menuId);
        assertNull(menu.findSectionByName("Antipasti").findMenuItemByRecipeId("RCP_001"));
        assertNotNull(menu.findSectionByName("Primi").findMenuItemByRecipeId("RCP_001"));
    }
    
    @Test
    @DisplayName("Test conferma creazione e generazione PDF")
    public void testConfirmMenuCreationAndGeneratePDF() {
        // Setup
        when(recipeController.getRecipe("RCP_001")).thenReturn(testRecipe);
        
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        menuController.defineMenuSections(menuId, Arrays.asList("Primi"));
        menuController.addRecipeToSection(menuId, "RCP_001", "Primi");
        
        // Test
        String pdfPath = menuController.confirmMenuCreationAndGeneratePDF(menuId);
        
        assertNotNull(pdfPath, "Il percorso PDF dovrebbe essere generato");
        assertTrue(pdfPath.contains("menu_"), "Il percorso dovrebbe contenere 'menu_'");
        
        Menu menu = menuController.getMenu(menuId);
        assertEquals(Menu.MenuState.PUBLISHED, menu.getState());
    }
    
    @Test
    @DisplayName("Test conferma creazione PDF - menù vuoto")
    public void testConfirmMenuCreationAndGeneratePDF_EmptyMenu() {
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        menuController.defineMenuSections(menuId, Arrays.asList("Primi"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            menuController.confirmMenuCreationAndGeneratePDF(menuId);
        }, "Dovrebbe lanciare eccezione per menù vuoto");
    }
    
    @Test
    @DisplayName("Test copia menù")
    public void testCopyMenu() {
        // Setup menù originale
        when(recipeController.getRecipe("RCP_001")).thenReturn(testRecipe);
        
        String originalMenuId = menuController.createMenu("CHF001", "Menu Originale");
        menuController.defineMenuSections(originalMenuId, Arrays.asList("Primi"));
        menuController.addRecipeToSection(originalMenuId, "RCP_001", "Primi");
        menuController.confirmMenuCreationAndGeneratePDF(originalMenuId);
        
        // Test copia
        String copiedMenuId = menuController.copyMenu(originalMenuId, "CHF002");
        
        assertNotEquals(originalMenuId, copiedMenuId);
        Menu copiedMenu = menuController.getMenu(copiedMenuId);
        
        assertEquals("CHF002", copiedMenu.getChefId());
        assertEquals(Menu.MenuState.DRAFT, copiedMenu.getState());
        assertTrue(copiedMenu.getName().contains("Copia"));
        assertEquals(1, copiedMenu.getTotalMenuItems());
    }
    
    @Test
    @DisplayName("Test eliminazione menù")
    public void testDeleteMenu() {
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        
        boolean deleted = menuController.deleteMenu(menuId, "CHF001");
        
        assertTrue(deleted, "Il menù dovrebbe essere eliminato");
        assertNull(menuController.getMenu(menuId));
    }
    
    @Test
    @DisplayName("Test eliminazione menù - utente non autorizzato")
    public void testDeleteMenu_Unauthorized() {
        String menuId = menuController.createMenu("CHF001", "Menu Test");
        
        assertThrows(SecurityException.class, () -> {
            menuController.deleteMenu(menuId, "CHF002");
        }, "Dovrebbe lanciare eccezione per utente non autorizzato");
    }
    
    @Test
    @DisplayName("Test ottenimento menù per chef")
    public void testGetMenusByChef() {
        String menuId1 = menuController.createMenu("CHF001", "Menu 1");
        String menuId2 = menuController.createMenu("CHF001", "Menu 2");
        String menuId3 = menuController.createMenu("CHF002", "Menu 3");
        
        List<Menu> chf001Menus = menuController.getMenusByChef("CHF001");
        List<Menu> chf002Menus = menuController.getMenusByChef("CHF002");
        
        assertEquals(2, chf001Menus.size());
        assertEquals(1, chf002Menus.size());
        
        assertTrue(chf001Menus.stream().anyMatch(m -> m.getId().equals(menuId1)));
        assertTrue(chf001Menus.stream().anyMatch(m -> m.getId().equals(menuId2)));
        assertTrue(chf002Menus.stream().anyMatch(m -> m.getId().equals(menuId3)));
    }
    
    @Test
    @DisplayName("Test statistiche menù")
    public void testGetMenuStatistics() {
        // Setup diversi menù in stati diversi
        when(recipeController.getRecipe("RCP_001")).thenReturn(testRecipe);
        
        String menuId1 = menuController.createMenu("CHF001", "Menu Draft");
        
        String menuId2 = menuController.createMenu("CHF001", "Menu Published");
        menuController.defineMenuSections(menuId2, Arrays.asList("Primi"));
        menuController.addRecipeToSection(menuId2, "RCP_001", "Primi");
        menuController.confirmMenuCreationAndGeneratePDF(menuId2);
        
        var stats = menuController.getMenuStatistics();
        
        assertTrue(stats.get("total") >= 2);
        assertTrue(stats.get("draft") >= 1);
        assertTrue(stats.get("published") >= 1);
        assertNotNull(stats.get("inUse"));
        assertNotNull(stats.get("archived"));
    }
}