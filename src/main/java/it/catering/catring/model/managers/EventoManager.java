package it.catering.catring.model.managers;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.persistence.JsonPersistenceManager;
import it.catering.catring.model.states.StatoEvento;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EventoManager {
    private static EventoManager instance;
    private List<Evento> eventi;
    private List<Cliente> clienti;
    private JsonPersistenceManager persistenceManager;
    
    private EventoManager() {
        this.eventi = new ArrayList<>();
        this.clienti = new ArrayList<>();
        this.persistenceManager = JsonPersistenceManager.getInstance();
        loadData();
    }
    
    public static synchronized EventoManager getInstance() {
        if (instance == null) {
            instance = new EventoManager();
        }
        return instance;
    }
    
    private void loadData() {
        try {
            List<Evento> loadedEventi = persistenceManager.loadEventi();
            List<Cliente> loadedClienti = persistenceManager.loadClienti();
            
            eventi.addAll(loadedEventi);
            clienti.addAll(loadedClienti);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento eventi: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try {
            persistenceManager.saveEventi(eventi);
            persistenceManager.saveClienti(clienti);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio eventi: " + e.getMessage());
        }
    }
    
    // CRUD Eventi
    public Evento createEvento(String nome, LocalDate dataInizio, LocalDate dataFine, 
                              String luogo, Cliente cliente, Organizzatore organizzatore) {
        Evento evento = new Evento(nome, dataInizio, luogo, cliente, organizzatore);
        if (dataFine != null) {
            evento.setDataFine(dataFine);
        }
        
        // Determina tipo evento
        if (dataInizio.equals(dataFine)) {
            evento.setTipo("singolo");
        } else {
            evento.setTipo("complesso");
        }
        
        eventi.add(evento);
        saveData();
        return evento;
    }
    
    public void updateEvento(Evento evento) {
        if (evento.getStato() == StatoEvento.COMPLETATO) {
            throw new IllegalStateException("Non è possibile modificare un evento completato");
        }
        saveData();
    }
    
    public void deleteEvento(Evento evento) {
        if (evento.getStato() != StatoEvento.BOZZA) {
            throw new IllegalStateException("Non è possibile eliminare un evento in corso");
        }
        eventi.remove(evento);
        saveData();
    }
    
    public void assegnaChef(Evento evento, Chef chef) {
        evento.setChef(chef);
        if (evento.getStato() == StatoEvento.BOZZA) {
            evento.setStato(StatoEvento.IN_CORSO);
        }
        saveData();
    }
    
    public void aggiungiServizio(Evento evento, Servizio servizio) {
        evento.aggiungiServizio(servizio);
        saveData();
    }
    
    public void rimuoviServizio(Evento evento, Servizio servizio) {
        evento.rimuoviServizio(servizio);
        saveData();
    }
    
    public void approvaMenuEvento(Evento evento) {
        if (!evento.hasChefAssegnato()) {
            throw new IllegalStateException("Deve essere assegnato uno chef prima di approvare i menu");
        }
        
        if (evento.getServizi().isEmpty()) {
            throw new IllegalStateException("L'evento deve avere almeno un servizio");
        }
        
        // Verifica che tutti i servizi abbiano un menu
        boolean tuttiIMenuPresenti = evento.getServizi().stream()
            .allMatch(s -> s.getMenu() != null);
        
        if (!tuttiIMenuPresenti) {
            throw new IllegalStateException("Tutti i servizi devono avere un menu assegnato");
        }
        
        // Marca tutti i menu come utilizzati
        evento.getServizi().forEach(s -> s.getMenu().setUtilizzato(true));
        
        evento.setStato(StatoEvento.APPROVATO);
        saveData();
    }
    
    public void completaEvento(Evento evento, String noteFinali) {
        evento.setStato(StatoEvento.COMPLETATO);
        if (noteFinali != null) {
            evento.setNote(evento.getNote() + "\n" + noteFinali);
        }
        saveData();
    }
    
    public void annullaEvento(Evento evento, String motivo) {
        evento.setStato(StatoEvento.ANNULLATO);
        evento.setNote(evento.getNote() + "\nAnnullato: " + motivo);
        saveData();
    }
    
    // CRUD Clienti
    public Cliente createCliente(String nome, String tipo, String contatti) {
        Cliente cliente = new Cliente(nome, tipo, contatti);
        clienti.add(cliente);
        saveData();
        return cliente;
    }
    
    public void updateCliente(Cliente cliente) {
        saveData();
    }
    
    public void deleteCliente(Cliente cliente) {
        // Verifica che non ci siano eventi associati
        boolean hasEventi = eventi.stream()
            .anyMatch(e -> cliente.equals(e.getCliente()));
        
        if (hasEventi) {
            throw new IllegalStateException("Non è possibile eliminare un cliente con eventi associati");
        }
        
        clienti.remove(cliente);
        saveData();
    }
    
    // Query methods
    public List<Evento> getEventiPerOrganizzatore(Organizzatore organizzatore) {
        return eventi.stream()
            .filter(e -> organizzatore.equals(e.getOrganizzatore()))
            .collect(Collectors.toList());
    }
    
    public List<Evento> getEventiPerChef(Chef chef) {
        return eventi.stream()
            .filter(e -> chef.equals(e.getChef()))
            .collect(Collectors.toList());
    }
    
    public List<Evento> getEventiPerPeriodo(LocalDate inizio, LocalDate fine) {
        return eventi.stream()
            .filter(e -> !e.getDataInizio().isAfter(fine) && !e.getDataFine().isBefore(inizio))
            .sorted(Comparator.comparing(Evento::getDataInizio))
            .collect(Collectors.toList());
    }
    
    public List<Evento> getEventiPerStato(StatoEvento stato) {
        return eventi.stream()
            .filter(e -> e.getStato() == stato)
            .collect(Collectors.toList());
    }
    
    public List<Evento> getEventiInCorso() {
        return getEventiPerStato(StatoEvento.IN_CORSO);
    }
    
    public List<Evento> getEventiApprovati() {
        return getEventiPerStato(StatoEvento.APPROVATO);
    }
    
    public List<Cliente> getAllClienti() {
        return new ArrayList<>(clienti);
    }
    
    public List<Evento> getAllEventi() {
        return new ArrayList<>(eventi);
    }
    
    public Cliente findClienteById(Long id) {
        return clienti.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public Evento findEventoById(Long id) {
        return eventi.stream()
            .filter(e -> e.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    // Statistiche
    public Map<String, Object> getStatisticheEventi() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totaleEventi", eventi.size());
        stats.put("eventiInCorso", getEventiInCorso().size());
        stats.put("eventiApprovati", getEventiApprovati().size());
        stats.put("eventiCompletati", getEventiPerStato(StatoEvento.COMPLETATO).size());
        
        // Conteggio per tipo
        Map<String, Long> eventiPerTipo = eventi.stream()
            .collect(Collectors.groupingBy(Evento::getTipo, Collectors.counting()));
        stats.put("eventiPerTipo", eventiPerTipo);
        
        // Eventi per mese corrente
        LocalDate oggi = LocalDate.now();
        LocalDate inizioMese = oggi.withDayOfMonth(1);
        LocalDate fineMese = oggi.withDayOfMonth(oggi.lengthOfMonth());
        
        long eventiMeseCorrente = getEventiPerPeriodo(inizioMese, fineMese).size();
        stats.put("eventiMeseCorrente", eventiMeseCorrente);
        
        return stats;
    }
}