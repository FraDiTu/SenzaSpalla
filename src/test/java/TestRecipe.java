import com.saslab.Recipe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class per Recipe
 * Verifica il corretto funzionamento della gestione delle ricette
 */
public class TestRecipe {
    
    private Recipe recipe;
    private final String recipeId = "RCP_001";
    private final String recipeName = "Pasta al Pomodoro";
    private final String ownerId = "CHF001";
    private final int preparationTime = 30;
    
    @BeforeEach
    public void setUp() {
        recipe = new Recipe(recipeId, recipeName, ownerId, preparationTime);
    }
    
    @Test
    @DisplayName("Test creazione ricetta - parametri validi")
    public void testRecipeCreation_ValidParameters() {
        assertNotNull(recipe, "La ricetta dovrebbe essere creata");
        assertEquals(recipeId, recipe.getId(), "ID dovrebbe corrispondere");
        assertEquals(recipeName, recipe.getName(), "Nome dovrebbe corrispondere");
        assertEquals(ownerId, recipe.getOwnerId(), "Owner ID dovrebbe corrispondere");
        assertEquals(preparationTime, recipe.getPreparationTime(), "Tempo preparazione dovrebbe corrispondere");
        assertEquals(Recipe.RecipeState.DRAFT, recipe.getState(), "Stato iniziale dovrebbe essere DRAFT");
        assertEquals(4, recipe.getBasePortions(), "Porzioni base dovrebbero essere 4 (default)");
    }
    
    @Test
    @DisplayName("Test creazione ricetta - parametri null")
    public void testRecipeCreation_NullParameters() {
        assertThrows(NullPointerException.class, () -> {
            new Recipe(null, recipeName, ownerId, preparationTime);
        }, "Dovrebbe lanciare eccezione per ID null");
        
        assertThrows(NullPointerException.class, () -> {
            new Recipe(recipeId, null, ownerId, preparationTime);
        }, "Dovrebbe lanciare eccezione per nome null");
        
        assertThrows(NullPointerException.class, () -> {
            new Recipe(recipeId, recipeName, null, preparationTime);
        }, "Dovrebbe lanciare eccezione per owner ID null");
    }
    
    @Test
    @DisplayName("Test aggiunta ingrediente - caso normale")
    public void testAddIngredient_Normal() {
        recipe.addIngredient("Pomodori", 400, "g");
        recipe.addIngredient("Pasta", 320, "g");
        
        assertEquals(2, recipe.getIngredients().size(), "Dovrebbero esserci 2 ingredienti");
        
        Recipe.Ingredient tomatoes = recipe.getIngredients().get(0);
        assertEquals("Pomodori", tomatoes.getName());
        assertEquals(400, tomatoes.getQuantity(), 0.01);
        assertEquals("g", tomatoes.getUnit());
    }
    
    @Test
    @DisplayName("Test aggiunta ingrediente - ricetta pubblicata")
    public void testAddIngredient_PublishedRecipe() {
        recipe.addIngredient("Pasta", 320, "g");
        recipe.publish();
        
        assertThrows(IllegalStateException.class, () -> {
            recipe.addIngredient("Olio", 50, "ml");
        }, "Non dovrebbe essere possibile aggiungere ingredienti a ricetta pubblicata");
    }
    
    @Test
    @DisplayName("Test aggiunta ingrediente - quantità invalida")
    public void testAddIngredient_InvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            recipe.addIngredient("Sale", -5, "g");
        }, "Dovrebbe lanciare eccezione per quantità negativa");
        
        assertThrows(IllegalArgumentException.class, () -> {
            recipe.addIngredient("Pepe", 0, "g");
        }, "Dovrebbe lanciare eccezione per quantità zero");
    }
    
    @Test
    @DisplayName("Test aggiunta tag - caso normale")
    public void testAddTag_Normal() {
        recipe.addTag("Vegetariano");
        recipe.addTag("Primo piatto");
        recipe.addTag("Vegetariano"); // Duplicato
        
        assertEquals(2, recipe.getTags().size(), "Dovrebbero esserci 2 tag (senza duplicati)");
        assertTrue(recipe.getTags().contains("Vegetariano"));
        assertTrue(recipe.getTags().contains("Primo piatto"));
    }
    
    @Test
    @DisplayName("Test aggiunta istruzioni - caso normale")
    public void testAddInstructions_Normal() {
        recipe.addAdvanceInstruction("Preparare il sugo");
        recipe.addAdvanceInstruction("Bollire la pasta");
        recipe.addLastMinuteInstruction("Mantecare con formaggio");
        
        assertEquals(2, recipe.getAdvanceInstructions().size());
        assertEquals(1, recipe.getLastMinuteInstructions().size());
        assertEquals("Preparare il sugo", recipe.getAdvanceInstructions().get(0));
        assertEquals("Mantecare con formaggio", recipe.getLastMinuteInstructions().get(0));
    }
    
    @Test
    @DisplayName("Test pubblicazione ricetta - caso normale")
    public void testPublishRecipe_Normal() {
        recipe.addIngredient("Pasta", 320, "g");
        recipe.addAdvanceInstruction("Cuocere la pasta");
        
        recipe.publish();
        
        assertEquals(Recipe.RecipeState.PUBLISHED, recipe.getState());
        assertTrue(recipe.isPublished());
    }
    
    @Test
    @DisplayName("Test pubblicazione ricetta - senza ingredienti")
    public void testPublishRecipe_NoIngredients() {
        assertThrows(IllegalStateException.class, () -> {
            recipe.publish();
        }, "Non dovrebbe essere possibile pubblicare ricetta senza ingredienti");
    }
    
    @Test
    @DisplayName("Test pubblicazione ricetta - già pubblicata")
    public void testPublishRecipe_AlreadyPublished() {
        recipe.addIngredient("Pasta", 320, "g");
        recipe.publish();
        
        assertThrows(IllegalStateException.class, () -> {
            recipe.publish();
        }, "Non dovrebbe essere possibile pubblicare ricetta già pubblicata");
    }
    
    @Test
    @DisplayName("Test ritiro pubblicazione - caso normale")
    public void testUnpublishRecipe_Normal() {
        recipe.addIngredient("Pasta", 320, "g");
        recipe.publish();
        recipe.unpublish();
        
        assertEquals(Recipe.RecipeState.DRAFT, recipe.getState());
        assertFalse(recipe.isPublished());
    }
    
    @Test
    @DisplayName("Test ritiro pubblicazione - ricetta non pubblicata")
    public void testUnpublishRecipe_NotPublished() {
        assertThrows(IllegalStateException.class, () -> {
            recipe.unpublish();
        }, "Non dovrebbe essere possibile ritirare ricetta non pubblicata");
    }
    
    @Test
    @DisplayName("Test verifica modifica permessa - proprietario")
    public void testCanBeModified_Owner() {
        assertTrue(recipe.canBeModified(ownerId), "Il proprietario dovrebbe poter modificare la ricetta in bozza");
    }
    
    @Test
    @DisplayName("Test verifica modifica permessa - non proprietario")
    public void testCanBeModified_NotOwner() {
        assertFalse(recipe.canBeModified("CHF002"), "Un altro utente non dovrebbe poter modificare la ricetta");
    }
    
    @Test
    @DisplayName("Test verifica modifica permessa - ricetta pubblicata")
    public void testCanBeModified_PublishedRecipe() {
        recipe.addIngredient("Pasta", 320, "g");
        recipe.publish();
        
        assertFalse(recipe.canBeModified(ownerId), "Non dovrebbe essere possibile modificare ricetta pubblicata");
    }
    
    @Test
    @DisplayName("Test creazione copia - caso normale")
    public void testCreateCopy_Normal() {
        // Prepara ricetta originale
        recipe.setDescription("Ricetta tradizionale");
        recipe.addIngredient("Pasta", 320, "g");
        recipe.addIngredient("Pomodori", 400, "g");
        recipe.addTag("Vegetariano");
        recipe.addAdvanceInstruction("Preparare sugo");
        recipe.addLastMinuteInstruction("Servire caldo");
        recipe.publish();
        
        // Crea copia
        Recipe copy = recipe.createCopy("RCP_002", "CHF002");
        
        assertNotEquals(recipe.getId(), copy.getId());
        assertEquals("CHF002", copy.getOwnerId());
        assertEquals(Recipe.RecipeState.DRAFT, copy.getState());
        assertTrue(copy.getName().contains("Copia"));
        assertEquals(recipe.getIngredients().size(), copy.getIngredients().size());
        assertEquals(recipe.getTags().size(), copy.getTags().size());
        assertEquals(recipe.getAdvanceInstructions().size(), copy.getAdvanceInstructions().size());
        assertEquals(recipe.getLastMinuteInstructions().size(), copy.getLastMinuteInstructions().size());
    }
    
    @Test
    @DisplayName("Test setter porzioni base - valore valido")
    public void testSetBasePortions_Valid() {
        recipe.setBasePortions(6);
        assertEquals(6, recipe.getBasePortions());
    }
    
    @Test
    @DisplayName("Test setter porzioni base - valore invalido")
    public void testSetBasePortions_Invalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            recipe.setBasePortions(0);
        }, "Dovrebbe lanciare eccezione per porzioni zero");
        
        assertThrows(IllegalArgumentException.class, () -> {
            recipe.setBasePortions(-3);
        }, "Dovrebbe lanciare eccezione per porzioni negative");
    }
    
    @Test
    @DisplayName("Test equals e hashCode")
    public void testEqualsAndHashCode() {
        Recipe anotherRecipe = new Recipe(recipeId, "Nome Diverso", "CHF999", 60);
        Recipe differentRecipe = new Recipe("RCP_999", recipeName, ownerId, preparationTime);
        
        assertEquals(recipe, anotherRecipe, "Ricette con stesso ID dovrebbero essere uguali");
        assertNotEquals(recipe, differentRecipe, "Ricette con ID diverso dovrebbero essere diverse");
        assertEquals(recipe.hashCode(), anotherRecipe.hashCode(), "Hash code dovrebbe essere uguale per stesso ID");
    }
}