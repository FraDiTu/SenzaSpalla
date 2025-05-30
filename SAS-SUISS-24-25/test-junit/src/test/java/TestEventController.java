import com.saslab.controller.EventController;
import com.saslab.model.Event;
import com.saslab.model.Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Test class per EventController
 * Verifica il corretto funzionamento della gestione degli eventi
 */
public class TestEventController {
    
    private EventController eventController;
    
    @BeforeEach
    public void setUp() {
        eventController = new EventController();
    }
    
    @Test
    @DisplayName("Test creazione evento - caso normale")
    public void testCreateEvent_Normal() {
        LocalDate eventDate = LocalDate.now().plusDays(7);
        String eventId = eventController.createEvent("ORG_001", "Matrimonio Rossi", 
                                                    "Villa dei Pini", eventDate, 150);
        
        assertNotNull(eventId, "L'ID dell'evento dovrebbe essere generato");
        assertTrue(eventId.startsWith("EVT_"), "L'ID dovrebbe iniziare con EVT_");
        
        Event event = eventController.getEvent(eventId);
        assertNotNull(event, "L'evento dovrebbe essere creato");
        assertEquals("Matrimonio Rossi", event.getName());
        assertEquals("ORG_001", event.getOrganizerId());
        assertEquals("Villa dei Pini", event.getLocation());
        assertEquals(eventDate, event.getStartDate());
        assertEquals(150, event.getExpectedGuests());
        assertEquals(Event.EventState.DRAFT, event.getState());
    }
    
    @Test
    @DisplayName("Test creazione evento - parametri invalidi")
    public void testCreateEvent_InvalidParameters() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            eventController.createEvent(null, "Test Event", "Location", today, 50);
        }, "Dovrebbe lanciare eccezione per organizer ID null");
        
        assertThrows(IllegalArgumentException.class, () -> {
            eventController.createEvent("ORG_001", "Test Event", "Location", today, -10);
        }, "Dovrebbe lanciare eccezione per numero ospiti negativo");
    }
    
    @Test
    @DisplayName("Test assegnazione chef a evento - caso normale")
    public void testAssignChefToEvent_Normal() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        
        eventController.assignChefToEvent(eventId, "CHF_001", "ORG_001");
        
        Event event = eventController.getEvent(eventId);
        assertEquals("CHF_001", event.getAssignedChefId());
    }
    
    @Test
    @DisplayName("Test assegnazione chef - organizzatore non autorizzato")
    public void testAssignChefToEvent_Unauthorized() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        
        assertThrows(SecurityException.class, () -> {
            eventController.assignChefToEvent(eventId, "CHF_001", "ORG_002");
        }, "Solo l'organizzatore dell'evento può assegnare lo chef");
    }
    
    @Test
    @DisplayName("Test aggiunta servizio a evento")
    public void testAddServiceToEvent() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        
        LocalDate serviceDate = LocalDate.now().plusDays(1);
        String serviceId = eventController.addServiceToEvent(eventId, serviceDate, 
                                                           LocalTime.of(12, 0), 
                                                           LocalTime.of(15, 0), 
                                                           Service.ServiceType.LUNCH);
        
        assertNotNull(serviceId);
        Event event = eventController.getEvent(eventId);
        assertEquals(1, event.getServices().size());
        
        Service service = event.getServiceById(serviceId);
        assertNotNull(service);
        assertEquals(Service.ServiceType.LUNCH, service.getType());
        assertEquals(serviceDate, service.getDate());
    }
    
    @Test
    @DisplayName("Test aggiornamento dettagli evento")
    public void testUpdateEventDetails() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        
        eventController.updateEventDetails(eventId, "ORG_001", "Nuovo Nome", 
                                         "Nuova Location", "Note aggiuntive");
        
        Event event = eventController.getEvent(eventId);
        assertEquals("Nuovo Nome", event.getName());
        assertEquals("Nuova Location", event.getLocation());
        assertEquals("Note aggiuntive", event.getNotes());
    }
    
    @Test
    @DisplayName("Test avvio evento")
    public void testStartEvent() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        eventController.assignChefToEvent(eventId, "CHF_001", "ORG_001");
        eventController.addServiceToEvent(eventId, LocalDate.now().plusDays(1), 
                                        LocalTime.of(12, 0), LocalTime.of(15, 0), 
                                        Service.ServiceType.LUNCH);
        
        eventController.startEvent(eventId, "ORG_001");
        
        Event event = eventController.getEvent(eventId);
        assertEquals(Event.EventState.IN_PROGRESS, event.getState());
    }
    
    @Test
    @DisplayName("Test avvio evento - precondizioni non soddisfatte")
    public void testStartEvent_PreconditionsNotMet() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        
        // Senza chef e servizi
        assertThrows(IllegalStateException.class, () -> {
            eventController.startEvent(eventId, "ORG_001");
        }, "Non dovrebbe poter avviare evento senza chef");
        
        // Con chef ma senza servizi
        eventController.assignChefToEvent(eventId, "CHF_001", "ORG_001");
        assertThrows(IllegalStateException.class, () -> {
            eventController.startEvent(eventId, "ORG_001");
        }, "Non dovrebbe poter avviare evento senza servizi");
    }
    
    @Test
    @DisplayName("Test completamento evento")
    public void testCompleteEvent() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        eventController.assignChefToEvent(eventId, "CHF_001", "ORG_001");
        eventController.addServiceToEvent(eventId, LocalDate.now().plusDays(1), 
                                        LocalTime.of(12, 0), LocalTime.of(15, 0), 
                                        Service.ServiceType.LUNCH);
        eventController.startEvent(eventId, "ORG_001");
        
        eventController.completeEvent(eventId, "ORG_001", "Evento riuscito perfettamente");
        
        Event event = eventController.getEvent(eventId);
        assertEquals(Event.EventState.COMPLETED, event.getState());
        assertTrue(event.getNotes().contains("Note finali: Evento riuscito perfettamente"));
    }
    
    @Test
    @DisplayName("Test annullamento evento")
    public void testCancelEvent() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        
        eventController.cancelEvent(eventId, "ORG_001", "Maltempo previsto");
        
        Event event = eventController.getEvent(eventId);
        assertEquals(Event.EventState.CANCELLED, event.getState());
        assertTrue(event.getNotes().contains("Motivo annullamento: Maltempo previsto"));
    }
    
    @Test
    @DisplayName("Test eliminazione evento")
    public void testDeleteEvent() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        
        boolean deleted = eventController.deleteEvent(eventId, "ORG_001");
        
        assertTrue(deleted);
        assertNull(eventController.getEvent(eventId));
    }
    
    @Test
    @DisplayName("Test eliminazione evento - stato non valido")
    public void testDeleteEvent_InvalidState() {
        String eventId = eventController.createEvent("ORG_001", "Test Event", 
                                                    "Location", LocalDate.now().plusDays(1), 50);
        eventController.assignChefToEvent(eventId, "CHF_001", "ORG_001");
        eventController.addServiceToEvent(eventId, LocalDate.now().plusDays(1), 
                                        LocalTime.of(12, 0), LocalTime.of(15, 0), 
                                        Service.ServiceType.LUNCH);
        eventController.startEvent(eventId, "ORG_001");
        
        assertThrows(IllegalStateException.class, () -> {
            eventController.deleteEvent(eventId, "ORG_001");
        }, "Non dovrebbe poter eliminare evento in corso");
    }
    
    @Test
    @DisplayName("Test ottenimento eventi per organizzatore")
    public void testGetEventsByOrganizer() {
        String event1 = eventController.createEvent("ORG_001", "Event 1", 
                                                   "Location 1", LocalDate.now().plusDays(1), 50);
        String event2 = eventController.createEvent("ORG_001", "Event 2", 
                                                   "Location 2", LocalDate.now().plusDays(2), 60);
        String event3 = eventController.createEvent("ORG_002", "Event 3", 
                                                   "Location 3", LocalDate.now().plusDays(3), 70);
        
        List<Event> org001Events = eventController.getEventsByOrganizer("ORG_001");
        List<Event> org002Events = eventController.getEventsByOrganizer("ORG_002");
        
        assertEquals(2, org001Events.size());
        assertEquals(1, org002Events.size());
        
        assertTrue(org001Events.stream().anyMatch(e -> e.getId().equals(event1)));
        assertTrue(org001Events.stream().anyMatch(e -> e.getId().equals(event2)));
        assertTrue(org002Events.stream().anyMatch(e -> e.getId().equals(event3)));
    }
    
    @Test
    @DisplayName("Test ottenimento eventi per stato")
    public void testGetEventsByState() {
        String event1 = eventController.createEvent("ORG_001", "Event 1", 
                                                   "Location 1", LocalDate.now().plusDays(1), 50);
        String event2 = eventController.createEvent("ORG_001", "Event 2", 
                                                   "Location 2", LocalDate.now().plusDays(2), 60);
        
        // Avvia un evento
        eventController.assignChefToEvent(event2, "CHF_001", "ORG_001");
        eventController.addServiceToEvent(event2, LocalDate.now().plusDays(2), 
                                        LocalTime.of(12, 0), LocalTime.of(15, 0), 
                                        Service.ServiceType.LUNCH);
        eventController.startEvent(event2, "ORG_001");
        
        List<Event> draftEvents = eventController.getEventsByState(Event.EventState.DRAFT);
        List<Event> inProgressEvents = eventController.getEventsByState(Event.EventState.IN_PROGRESS);
        
        assertTrue(draftEvents.stream().anyMatch(e -> e.getId().equals(event1)));
        assertTrue(inProgressEvents.stream().anyMatch(e -> e.getId().equals(event2)));
    }
    
    @Test
    @DisplayName("Test ottenimento eventi in intervallo date")
    public void testGetEventsInDateRange() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(10);
        
        String event1 = eventController.createEvent("ORG_001", "Event in range", 
                                                   "Location", start.plusDays(2), 50);
        String event2 = eventController.createEvent("ORG_001", "Event out of range", 
                                                   "Location", end.plusDays(5), 50);
        
        List<Event> eventsInRange = eventController.getEventsInDateRange(start, end);
        
        assertTrue(eventsInRange.stream().anyMatch(e -> e.getId().equals(event1)));
        assertFalse(eventsInRange.stream().anyMatch(e -> e.getId().equals(event2)));
    }
    
    @Test
    @DisplayName("Test ottenimento eventi per chef")
    public void testGetEventsByChef() {
        String event1 = eventController.createEvent("ORG_001", "Event 1", 
                                                   "Location", LocalDate.now().plusDays(1), 50);
        String event2 = eventController.createEvent("ORG_001", "Event 2", 
                                                   "Location", LocalDate.now().plusDays(2), 50);
        
        eventController.assignChefToEvent(event1, "CHF_001", "ORG_001");
        eventController.assignChefToEvent(event2, "CHF_002", "ORG_001");
        
        List<Event> chf001Events = eventController.getEventsByChef("CHF_001");
        List<Event> chf002Events = eventController.getEventsByChef("CHF_002");
        
        assertEquals(1, chf001Events.size());
        assertEquals(1, chf002Events.size());
        assertTrue(chf001Events.stream().anyMatch(e -> e.getId().equals(event1)));
        assertTrue(chf002Events.stream().anyMatch(e -> e.getId().equals(event2)));
    }
    
    @Test
    @DisplayName("Test ricerca eventi per nome")
    public void testSearchEventsByName() {
        String event1 = eventController.createEvent("ORG_001", "Matrimonio Romano", 
                                                   "Location", LocalDate.now().plusDays(1), 50);
        String event2 = eventController.createEvent("ORG_001", "Compleanno Maria", 
                                                   "Location", LocalDate.now().plusDays(2), 30);
        
        List<Event> matrimonioEvents = eventController.searchEventsByName("matrimonio");
        List<Event> mariaEvents = eventController.searchEventsByName("MARIA");
        
        assertTrue(matrimonioEvents.stream().anyMatch(e -> e.getId().equals(event1)));
        assertTrue(mariaEvents.stream().anyMatch(e -> e.getId().equals(event2)));
        assertFalse(matrimonioEvents.stream().anyMatch(e -> e.getId().equals(event2)));
    }
    
    @Test
    @DisplayName("Test statistiche eventi")
    public void testGetEventStatistics() {
        String event1 = eventController.createEvent("ORG_001", "Event Draft", 
                                                   "Location", LocalDate.now().plusDays(1), 50);
        String event2 = eventController.createEvent("ORG_001", "Event In Progress", 
                                                   "Location", LocalDate.now().plusDays(2), 60);
        String event3 = eventController.createEvent("ORG_001", "Event Cancelled", 
                                                   "Location", LocalDate.now().plusDays(3), 40);
        
        // Configura stati diversi
        eventController.assignChefToEvent(event2, "CHF_001", "ORG_001");
        eventController.addServiceToEvent(event2, LocalDate.now().plusDays(2), 
                                        LocalTime.of(12, 0), LocalTime.of(15, 0), 
                                        Service.ServiceType.LUNCH);
        eventController.startEvent(event2, "ORG_001");
        
        eventController.cancelEvent(event3, "ORG_001", "Motivo di test");
        
        Map<String, Integer> stats = eventController.getEventStatistics();
        
        assertTrue(stats.get("total") >= 3);
        assertTrue(stats.get("draft") >= 1);
        assertTrue(stats.get("inProgress") >= 1);
        assertTrue(stats.get("cancelled") >= 1);
        assertNotNull(stats.get("completed"));
    }
}