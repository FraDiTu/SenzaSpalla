import com.saslab.Recipe;
import com.saslab.RecipeBook;
import com.saslab.model.Preparation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class per RecipeBook
 * Verifica il corretto funzionamento del pattern Singleton e della gestione del ricettario
 */
public class TestRecipeBook {
    
    private RecipeBook recipeBook;
    private Recipe testRecipe;
    private Preparation testPreparation;
    
    @BeforeEach
    public void setUp() {
        recipeBook = RecipeBook.getInstance();
        
        // Genera ID univoci per ogni test per evitare conflitti con il Singleton
        String uniqueId = String.valueOf(System.nanoTime());
        String recipeId = "RCP_TEST_" + uniqueId;
        String prepId = "PRP_TEST_" + uniqueId;
        
        testRecipe = new Recipe(recipeId, "Pasta Test", "CHF001", 30);
        testRecipe.addIngredient("Pasta", 320, "g");
        
        testPreparation = new Preparation(prepId, "Sugo Test", "CHF001", 20);
        testPreparation.addIngredient("Pomodori", 400, "g");
        testPreparation.addInstruction("Cuocere i pomodori");
    }
    
    @Test
    @DisplayName("Test pattern Singleton - stessa istanza")
    public void testSingleton_SameInstance() {
        RecipeBook anotherInstance = RecipeBook.getInstance();
        assertSame(recipeBook, anotherInstance, "Dovrebbe restituire sempre la stessa istanza");
    }
    
    @Test
    @DisplayName("Test aggiunta ricetta - caso normale")
    public void testAddRecipe_Normal() {
        int initialCount = recipeBook.getTotalRecipeCount();
        
        recipeBook.addRecipe(testRecipe);
        
        assertEquals(initialCount + 1, recipeBook.getTotalRecipeCount(), 
                    "Il numero di ricette dovrebbe aumentare di 1");
        assertEquals(testRecipe, recipeBook.getRecipe(testRecipe.getId()), 
                    "La ricetta dovrebbe essere presente nel ricettario");
    }
    
    @Test
    @DisplayName("Test aggiunta ricetta - ricetta null")
    public void testAddRecipe_NullRecipe() {
        assertThrows(NullPointerException.class, () -> {
            recipeBook.addRecipe(null);
        }, "Dovrebbe lanciare eccezione per ricetta null");
    }
    
    @Test
    @DisplayName("Test ottenimento ricette pubblicate")
    public void testGetPublishedRecipes() {
        recipeBook.addRecipe(testRecipe);
        
        // Inizialmente nessuna ricetta pubblicata
        int initialPublished = recipeBook.getPublishedRecipeCount();
        assertTrue(recipeBook.getPublishedRecipes().isEmpty() || 
                  recipeBook.getPublishedRecipes().size() == initialPublished);
        
        // Pubblica la ricetta
        testRecipe.publish();
        
        assertEquals(initialPublished + 1, recipeBook.getPublishedRecipeCount());
        assertTrue(recipeBook.getPublishedRecipes().contains(testRecipe));
    }
    
    @Test
    @DisplayName("Test ottenimento ricette per proprietario")
    public void testGetRecipesByOwner() {
        recipeBook.addRecipe(testRecipe);
        
        String uniqueId2 = String.valueOf(System.nanoTime() + 1);
        Recipe anotherRecipe = new Recipe("RCP_TEST2_" + uniqueId2, "Pizza Test", "CHF002", 45);
        anotherRecipe.addIngredient("Farina", 500, "g");
        recipeBook.addRecipe(anotherRecipe);
        
        var chf001Recipes = recipeBook.getRecipesByOwner("CHF001");
        var chf002Recipes = recipeBook.getRecipesByOwner("CHF002");
        
        assertTrue(chf001Recipes.contains(testRecipe));
        assertFalse(chf001Recipes.contains(anotherRecipe));
        assertTrue(chf002Recipes.contains(anotherRecipe));
        assertFalse(chf002Recipes.contains(testRecipe));
    }
    
    @Test
    @DisplayName("Test ricerca ricette per tag")
    public void testSearchRecipesByTag() {
        testRecipe.addTag("Vegetariano");
        testRecipe.addTag("Primo");
        testRecipe.publish();
        recipeBook.addRecipe(testRecipe);
        
        var vegetarianRecipes = recipeBook.searchRecipesByTag("Vegetariano");
        var primoRecipes = recipeBook.searchRecipesByTag("Primo");
        var nonExistentRecipes = recipeBook.searchRecipesByTag("NonEsiste");
        
        assertTrue(vegetarianRecipes.contains(testRecipe));
        assertTrue(primoRecipes.contains(testRecipe));
        assertFalse(nonExistentRecipes.contains(testRecipe));
    }
    
    @Test
    @DisplayName("Test ricerca ricette per nome")
    public void testSearchRecipesByName() {
        testRecipe.publish();
        recipeBook.addRecipe(testRecipe);
        
        var pastaRecipes = recipeBook.searchRecipesByName("pasta");
        var testRecipes = recipeBook.searchRecipesByName("TEST");
        var nonExistentRecipes = recipeBook.searchRecipesByName("NonEsiste");
        
        assertTrue(pastaRecipes.contains(testRecipe), "Dovrebbe trovare ricetta con 'pasta' nel nome");
        assertTrue(testRecipes.contains(testRecipe), "Dovrebbe trovare ricetta con 'test' nel nome (case-insensitive)");
        assertFalse(nonExistentRecipes.contains(testRecipe));
    }
    
    @Test
    @DisplayName("Test rimozione ricetta - proprietario autorizzato")
    public void testRemoveRecipe_AuthorizedOwner() {
        recipeBook.addRecipe(testRecipe);
        int initialCount = recipeBook.getTotalRecipeCount();
        
        boolean removed = recipeBook.removeRecipe(testRecipe.getId(), "CHF001");
        
        assertTrue(removed, "La rimozione dovrebbe avere successo");
        assertEquals(initialCount - 1, recipeBook.getTotalRecipeCount());
        assertNull(recipeBook.getRecipe(testRecipe.getId()));
    }
    
    @Test
    @DisplayName("Test rimozione ricetta - utente non autorizzato")
    public void testRemoveRecipe_UnauthorizedUser() {
        recipeBook.addRecipe(testRecipe);
        
        assertThrows(SecurityException.class, () -> {
            recipeBook.removeRecipe(testRecipe.getId(), "CHF002");
        }, "Dovrebbe lanciare eccezione per utente non autorizzato");
    }
    
    @Test
    @DisplayName("Test rimozione ricetta - ricetta inesistente")
    public void testRemoveRecipe_NonExistentRecipe() {
        boolean removed = recipeBook.removeRecipe("RCP_INESISTENTE", "CHF001");
        assertFalse(removed, "La rimozione di ricetta inesistente dovrebbe fallire");
    }
    
    @Test
    @DisplayName("Test aggiunta preparazione")
    public void testAddPreparation() {
        int initialCount = recipeBook.getTotalPreparationCount();
        
        recipeBook.addPreparation(testPreparation);
        
        assertEquals(initialCount + 1, recipeBook.getTotalPreparationCount());
        assertEquals(testPreparation, recipeBook.getPreparation(testPreparation.getId()));
    }
    
    @Test
    @DisplayName("Test ottenimento preparazioni pubblicate")
    public void testGetPublishedPreparations() {
        recipeBook.addPreparation(testPreparation);
        
        // Inizialmente nessuna preparazione pubblicata (per questo test)
        assertFalse(recipeBook.getPublishedPreparations().contains(testPreparation));
        
        // Pubblica la preparazione
        testPreparation.publish();
        
        assertTrue(recipeBook.getPublishedPreparations().contains(testPreparation));
    }
    
    @Test
    @DisplayName("Test ottenimento preparazioni per proprietario")
    public void testGetPreparationsByOwner() {
        recipeBook.addPreparation(testPreparation);
        
        var chf001Preparations = recipeBook.getPreparationsByOwner("CHF001");
        var chf002Preparations = recipeBook.getPreparationsByOwner("CHF002");
        
        assertTrue(chf001Preparations.contains(testPreparation));
        assertFalse(chf002Preparations.contains(testPreparation));
    }
    
    @Test
    @DisplayName("Test ottenimento tutti i tag")
    public void testGetAllTags() {
        testRecipe.addTag("Vegetariano");
        testRecipe.addTag("Primo");
        testRecipe.publish();
        recipeBook.addRecipe(testRecipe);
        
        testPreparation.addTag("Base");
        testPreparation.addTag("Vegetariano"); // Tag duplicato
        testPreparation.publish();
        recipeBook.addPreparation(testPreparation);
        
        var allTags = recipeBook.getAllTags();
        
        assertTrue(allTags.contains("Vegetariano"));
        assertTrue(allTags.contains("Primo"));
        assertTrue(allTags.contains("Base"));
        // Verifica che non ci siano duplicati
        assertEquals(3, allTags.size());
    }
    
    @Test
    @DisplayName("Test aggiornamento versione")
    public void testVersionUpdate() {
        String initialVersion = recipeBook.getVersion();
        
        recipeBook.addRecipe(testRecipe);
        
        String newVersion = recipeBook.getVersion();
        assertNotEquals(initialVersion, newVersion, "La versione dovrebbe essere aggiornata dopo l'aggiunta di una ricetta");
    }
    
    @Test
    @DisplayName("Test controllo utilizzo preparazione")
    public void testIsPreparationInUse() {
        // Crea una ricetta che usa la preparazione
        String uniqueId = String.valueOf(System.nanoTime() + 2);
        Recipe recipeWithPrep = new Recipe("RCP_WITH_PREP_" + uniqueId, "Pasta con Sugo", "CHF001", 40);
        recipeWithPrep.addIngredient(testPreparation.getId(), 200, "g"); // Usa la preparazione come ingrediente
        recipeWithPrep.publish();
        
        testPreparation.publish();
        recipeBook.addPreparation(testPreparation);
        recipeBook.addRecipe(recipeWithPrep);
        
        assertTrue(recipeBook.isPreparationInUse(testPreparation.getId()), 
                  "La preparazione dovrebbe risultare in uso");
    }
}