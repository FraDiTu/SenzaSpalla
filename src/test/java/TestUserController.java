import com.saslab.controller.UserController;
import com.saslab.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Test class per UserController
 * Verifica il corretto funzionamento della gestione degli utenti
 */
public class TestUserController {
    
    private UserController userController;
    
    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }
    
    @Test
    @DisplayName("Test inizializzazione utenti di default")
    public void testDefaultUsersInitialization() {
        assertTrue(userController.getTotalUserCount() >= 4, 
                  "Dovrebbero esserci almeno 4 utenti di default");
        
        // Verifica che ci sia almeno un utente per ogni ruolo
        assertTrue(userController.getAllOrganizers().size() >= 1);
        assertTrue(userController.getAllChefs().size() >= 1);
        assertTrue(userController.getAllCooks().size() >= 1);
        assertTrue(userController.getAllServiceStaff().size() >= 1);
    }
    
    @Test
    @DisplayName("Test creazione chef - caso normale")
    public void testCreateChef_Normal() {
        String chefId = userController.createChef("Mario Bianchi", "mario.b@test.com", 
                                                 "password123", "Pasticceria", 8);
        
        assertNotNull(chefId, "L'ID dello chef dovrebbe essere generato");
        
        User user = userController.getUser(chefId);
        assertNotNull(user, "Lo chef dovrebbe essere creato");
        assertTrue(user instanceof Chef, "Dovrebbe essere un'istanza di Chef");
        
        Chef chef = (Chef) user;
        assertEquals("Mario Bianchi", chef.getName());
        assertEquals("mario.b@test.com", chef.getEmail());
        assertEquals("Pasticceria", chef.getSpecialization());
        assertEquals(8, chef.getExperienceYears());
        assertEquals(User.UserRole.CHEF, chef.getRole());
    }
    
    @Test
    @DisplayName("Test creazione cuoco - caso normale")
    public void testCreateCook_Normal() {
        String cookId = userController.createCook("Giuseppe Rossi", "giuseppe.r@test.com", 
                                                 "password123", 3);
        
        assertNotNull(cookId);
        
        User user = userController.getUser(cookId);
        assertTrue(user instanceof Cook);
        
        Cook cook = (Cook) user;
        assertEquals("Giuseppe Rossi", cook.getName());
        assertEquals(3, cook.getExperienceYears());
        assertEquals(User.UserRole.COOK, cook.getRole());
    }
    
    @Test
    @DisplayName("Test creazione organizzatore - caso normale")
    public void testCreateOrganizer_Normal() {
        String orgId = userController.createOrganizer("Anna Verdi", "anna.v@test.com", 
                                                     "password123", "Eventi Aziendali");
        
        assertNotNull(orgId);
        
        User user = userController.getUser(orgId);
        assertTrue(user instanceof Organizer);
        
        Organizer organizer = (Organizer) user;
        assertEquals("Anna Verdi", organizer.getName());
        assertEquals("Eventi Aziendali", organizer.getDepartment());
        assertEquals(User.UserRole.ORGANIZER, organizer.getRole());
    }
    
    @Test
    @DisplayName("Test creazione personale servizio - caso normale")
    public void testCreateServiceStaff_Normal() {
        String staffId = userController.createServiceStaff("Marco Neri", "marco.n@test.com", 
                                                          "password123", "Sommelier", 5);
        
        assertNotNull(staffId);
        
        User user = userController.getUser(staffId);
        assertTrue(user instanceof ServiceStaff);
        
        ServiceStaff staff = (ServiceStaff) user;
        assertEquals("Marco Neri", staff.getName());
        assertEquals("Sommelier", staff.getServiceRole());
        assertEquals(5, staff.getExperienceYears());
        assertEquals(User.UserRole.SERVICE_STAFF, staff.getRole());
    }
    
    @Test
    @DisplayName("Test creazione utente - email duplicata")
    public void testCreateUser_DuplicateEmail() {
        userController.createChef("Chef 1", "test@example.com", "password123", "Cucina", 5);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userController.createCook("Cook 1", "test@example.com", "password123", 3);
        }, "Dovrebbe lanciare eccezione per email duplicata");
    }
    
    @Test
    @DisplayName("Test creazione utente - parametri invalidi")
    public void testCreateUser_InvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            userController.createChef("", "test@example.com", "password123", "Cucina", 5);
        }, "Dovrebbe lanciare eccezione per nome vuoto");
        
        assertThrows(IllegalArgumentException.class, () -> {
            userController.createChef("Test", "invalid-email", "password123", "Cucina", 5);
        }, "Dovrebbe lanciare eccezione per email invalida");
        
        assertThrows(IllegalArgumentException.class, () -> {
            userController.createChef("Test", "test@example.com", "123", "Cucina", 5);
        }, "Dovrebbe lanciare eccezione per password troppo corta");
    }
    
    @Test
    @DisplayName("Test autenticazione utente - credenziali corrette")
    public void testAuthenticateUser_ValidCredentials() {
        String chefId = userController.createChef("Test Chef", "testchef@example.com", 
                                                 "password123", "Cucina", 5);
        
        User authenticatedUser = userController.authenticateUser("testchef@example.com", "password123");
        
        assertNotNull(authenticatedUser, "L'autenticazione dovrebbe riuscire");
        assertEquals(chefId, authenticatedUser.getId());
        assertEquals("Test Chef", authenticatedUser.getName());
    }
    
    @Test
    @DisplayName("Test autenticazione utente - credenziali errate")
    public void testAuthenticateUser_InvalidCredentials() {
        userController.createChef("Test Chef", "testchef@example.com", "password123", "Cucina", 5);
        
        User result1 = userController.authenticateUser("testchef@example.com", "wrongpassword");
        User result2 = userController.authenticateUser("wrong@example.com", "password123");
        
        assertNull(result1, "L'autenticazione dovrebbe fallire con password errata");
        assertNull(result2, "L'autenticazione dovrebbe fallire con email errata");
    }
    
    @Test
    @DisplayName("Test ottenimento utente per email")
    public void testGetUserByEmail() {
        String chefId = userController.createChef("Test Chef", "findme@example.com", 
                                                 "password123", "Cucina", 5);
        
        User foundUser = userController.getUserByEmail("findme@example.com");
        User notFoundUser = userController.getUserByEmail("notfound@example.com");
        
        assertNotNull(foundUser, "Dovrebbe trovare l'utente esistente");
        assertEquals(chefId, foundUser.getId());
        assertNull(notFoundUser, "Non dovrebbe trovare utente inesistente");
    }
    
    @Test
    @DisplayName("Test ottenimento utenti per ruolo")
    public void testGetUsersByRole() {
        int initialChefs = userController.getAllChefs().size();
        int initialCooks = userController.getAllCooks().size();
        
        userController.createChef("Chef Test", "chef@test.com", "password123", "Cucina", 5);
        userController.createCook("Cook Test", "cook@test.com", "password123", 3);
        
        List<User> chefs = userController.getUsersByRole(User.UserRole.CHEF);
        List<User> cooks = userController.getUsersByRole(User.UserRole.COOK);
        
        assertEquals(initialChefs + 1, chefs.size(), "Dovrebbe esserci un chef in più");
        assertEquals(initialCooks + 1, cooks.size(), "Dovrebbe esserci un cuoco in più");
    }
    
    @Test
    @DisplayName("Test aggiornamento informazioni utente")
    public void testUpdateUser() {
        String chefId = userController.createChef("Original Name", "original@test.com", 
                                                 "password123", "Cucina", 5);
        
        userController.updateUser(chefId, "Updated Name", "updated@test.com");
        
        User updatedUser = userController.getUser(chefId);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());
    }
    
    @Test
    @DisplayName("Test aggiornamento utente - email duplicata")
    public void testUpdateUser_DuplicateEmail() {
        String chef1Id = userController.createChef("Chef 1", "chef1@test.com", "password123", "Cucina", 5);
        assertThrows(IllegalArgumentException.class, () -> {
            userController.updateUser(chef1Id, "Chef 1", "chef2@test.com");
        }, "Non dovrebbe permettere email duplicate nell'aggiornamento");
    }
    
    @Test
    @DisplayName("Test cambio password")
    public void testChangePassword() {
        String chefId = userController.createChef("Test Chef", "test@example.com", 
                                                 "oldpassword", "Cucina", 5);
        
        userController.changePassword(chefId, "oldpassword", "newpassword123");
        
        // Verifica che la nuova password funzioni
        User authenticatedUser = userController.authenticateUser("test@example.com", "newpassword123");
        assertNotNull(authenticatedUser, "L'autenticazione con la nuova password dovrebbe funzionare");
        
        // Verifica che la vecchia password non funzioni più
        User oldAuthUser = userController.authenticateUser("test@example.com", "oldpassword");
        assertNull(oldAuthUser, "L'autenticazione con la vecchia password dovrebbe fallire");
    }
    
    @Test
    @DisplayName("Test cambio password - password attuale errata")
    public void testChangePassword_WrongCurrentPassword() {
        String chefId = userController.createChef("Test Chef", "test@example.com", 
                                                 "password123", "Cucina", 5);
        
        assertThrows(SecurityException.class, () -> {
            userController.changePassword(chefId, "wrongpassword", "newpassword123");
        }, "Dovrebbe lanciare eccezione per password attuale errata");
    }
    
    @Test
    @DisplayName("Test cambio password - nuova password troppo corta")
    public void testChangePassword_NewPasswordTooShort() {
        String chefId = userController.createChef("Test Chef", "test@example.com", 
                                                 "password123", "Cucina", 5);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userController.changePassword(chefId, "password123", "123");
        }, "Dovrebbe lanciare eccezione per nuova password troppo corta");
    }
    
    @Test
    @DisplayName("Test attivazione e disattivazione utente")
    public void testActivateDeactivateUser() {
        String chefId = userController.createChef("Test Chef", "test@example.com", 
                                                 "password123", "Cucina", 5);
        
        User user = userController.getUser(chefId);
        assertTrue(user.isActive(), "L'utente dovrebbe essere attivo inizialmente");
        
        // Disattiva
        userController.deactivateUser(chefId);
        assertFalse(user.isActive(), "L'utente dovrebbe essere disattivato");
        
        // Riattiva
        userController.activateUser(chefId);
        assertTrue(user.isActive(), "L'utente dovrebbe essere riattivato");
    }
    
    @Test
    @DisplayName("Test eliminazione utente")
    public void testDeleteUser() {
        String chefId = userController.createChef("Test Chef", "test@example.com", 
                                                 "password123", "Cucina", 5);
        
        assertNotNull(userController.getUser(chefId), "L'utente dovrebbe esistere prima dell'eliminazione");
        
        boolean deleted = userController.deleteUser(chefId);
        
        assertTrue(deleted, "L'eliminazione dovrebbe riuscire");
        assertNull(userController.getUser(chefId), "L'utente non dovrebbe più esistere");
    }
    
    @Test
    @DisplayName("Test eliminazione utente inesistente")
    public void testDeleteUser_NonExistent() {
        boolean deleted = userController.deleteUser("INVALID_ID");
        assertFalse(deleted, "L'eliminazione di utente inesistente dovrebbe fallire");
    }
    
    @Test
    @DisplayName("Test ricerca utenti per nome")
    public void testSearchUsersByName() {
        userController.createChef("Mario Rossi", "mario@test.com", "password123", "Cucina", 5);
        userController.createChef("Giuseppe Verdi", "giuseppe@test.com", "password123", "Pasticceria", 3);
        userController.createCook("Anna Mario", "anna@test.com", "password123", 2);
        
        List<User> marioUsers = userController.searchUsersByName("mario");
        List<User> verdiUsers = userController.searchUsersByName("VERDI");
        List<User> notFoundUsers = userController.searchUsersByName("NonEsiste");
        
        assertTrue(marioUsers.size() >= 2, "Dovrebbe trovare almeno 2 utenti con 'mario' nel nome");
        assertTrue(verdiUsers.size() >= 1, "Dovrebbe trovare almeno 1 utente con 'verdi' nel nome");
        assertEquals(0, notFoundUsers.size(), "Non dovrebbe trovare utenti inesistenti");
    }
    
    @Test
    @DisplayName("Test verifica permessi utente")
    public void testCanUserPerformAction() {
        String chefId = userController.createChef("Test Chef", "chef@test.com", "password123", "Cucina", 5);
        String cookId = userController.createCook("Test Cook", "cook@test.com", "password123", 3);
        
        // Chef può creare ricette e menù
        assertTrue(userController.canUserPerformAction(chefId, "CREATE_RECIPE"));
        assertTrue(userController.canUserPerformAction(chefId, "CREATE_MENU"));
        
        // Cook può creare ricette ma non menù
        assertTrue(userController.canUserPerformAction(cookId, "CREATE_RECIPE"));
        assertFalse(userController.canUserPerformAction(cookId, "CREATE_MENU"));
        
        // Utente inesistente non può fare nulla
        assertFalse(userController.canUserPerformAction("INVALID_ID", "CREATE_RECIPE"));
    }
    
    @Test
    @DisplayName("Test statistiche utenti")
    public void testGetUserStatistics() {
        int initialTotal = userController.getTotalUserCount();
        
        userController.createChef("Test Chef", "chef@test.com", "password123", "Cucina", 5);
        userController.createCook("Test Cook", "cook@test.com", "password123", 3);
        
        Map<String, Integer> stats = userController.getUserStatistics();
        
        assertEquals(initialTotal + 2, (int) stats.get("total"));
        assertTrue(stats.get("chefs") >= 2); // Almeno default + nuovo
        assertTrue(stats.get("cooks") >= 2); // Almeno default + nuovo
        assertTrue(stats.get("organizers") >= 1); // Almeno default
        assertTrue(stats.get("serviceStaff") >= 1); // Almeno default
        assertTrue(stats.get("active") >= initialTotal + 2); // Tutti attivi inizialmente
        assertEquals(0, (int) stats.get("inactive")); // Nessuno disattivato
    }
    
    @Test
    @DisplayName("Test ottenimento cuochi disponibili")
    public void testGetAvailableCooks() {
        String cookId = userController.createCook("Available Cook", "available@test.com", "password123", 3);
        
        List<Cook> availableCooks = userController.getAvailableCooks();
        
        assertTrue(availableCooks.size() >= 2, "Dovrebbero esserci almeno 2 cuochi disponibili");
        assertTrue(availableCooks.stream().anyMatch(c -> c.getId().equals(cookId)));
        
        // Disattiva un cuoco
        userController.deactivateUser(cookId);
        List<Cook> availableAfterDeactivation = userController.getAvailableCooks();
        
        assertFalse(availableAfterDeactivation.stream().anyMatch(c -> c.getId().equals(cookId)),
                   "Il cuoco disattivato non dovrebbe essere disponibile");
    }
    
    @Test
    @DisplayName("Test ottenimento personale servizio disponibile")
    public void testGetAvailableServiceStaff() {
        String staffId = userController.createServiceStaff("Available Staff", "staff@test.com", 
                                                          "password123", "Cameriere", 2);
        
        List<ServiceStaff> availableStaff = userController.getAvailableServiceStaff();
        
        assertTrue(availableStaff.size() >= 2, "Dovrebbe esserci almeno 2 personale di servizio disponibile");
        assertTrue(availableStaff.stream().anyMatch(s -> s.getId().equals(staffId)));
    }
    
    @Test
    @DisplayName("Test reset sistema")
    public void testResetSystem() {
        // Aggiungi alcuni utenti
        userController.createChef("Test Chef", "chef@test.com", "password123", "Cucina", 5);
        userController.createCook("Test Cook", "cook@test.com", "password123", 3);
        
        int totalBeforeReset = userController.getTotalUserCount();
        assertTrue(totalBeforeReset >= 6, "Dovrebbero esserci almeno 6 utenti prima del reset");
        
        // Reset
        userController.resetSystem();
        
        int totalAfterReset = userController.getTotalUserCount();
        assertEquals(4, totalAfterReset, "Dopo il reset dovrebbero esserci solo 4 utenti di default");
    }
}