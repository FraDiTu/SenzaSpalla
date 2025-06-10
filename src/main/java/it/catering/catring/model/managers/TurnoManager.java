package it.catering.catring.model.managers;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.persistence.JsonPersistenceManager;
import it.catering.catring.model.states.StatoTurno;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TurnoManager {
    private static TurnoManager instance;
    private List<TurnoPreparatorio> turniPreparatori;
    private List<TurnoServizio> turniServizio;
    private Map<User, Set<Long>> disponibilita; // User -> Set di ID turni
    private JsonPersistenceManager persistenceManager;
    
    private TurnoManager() {
        this.turniPreparatori = new ArrayList<>();
        this.turniServizio = new ArrayList<>();
        this.disponibilita = new HashMap<>();
        this.persistenceManager = JsonPersistenceManager.getInstance();
        loadData();
    }
    
    public static synchronized TurnoManager getInstance() {
        if (instance == null) {
            instance = new TurnoManager();
        }
        return instance;
    }
    
    private void loadData() {
        try {
            List<TurnoPreparatorio> loadedTurniPrep = persistenceManager.loadTurniPreparatori();
            List<TurnoServizio> loadedTurniServ = persistenceManager.loadTurniServizio();
            
            turniPreparatori.addAll(loadedTurniPrep);
            turniServizio.addAll(loadedTurniServ);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento turni: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try {
            persistenceManager.saveTurniPreparatori(turniPreparatori);
            persistenceManager.saveTurniServizio(turniServizio);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio turni: " + e.getMessage());
        }
    }
    
    // CRUD Turni Preparatori
    public TurnoPreparatorio createTurnoPreparatorio(LocalDate data, LocalTime inizio, 
                                                   LocalTime fine, String luogo) {
        TurnoPreparatorio turno = new TurnoPreparatorio(data, inizio, fine);
        if (luogo != null && !luogo.trim().isEmpty()) {
            turno.setLuogo(luogo);
        }
        turniPreparatori.add(turno);
        saveData();
        return turno;
    }
    
    public void updateTurnoPreparatorio(TurnoPreparatorio turno) {
        if (!turno.isModificabile()) {
            throw new IllegalStateException("Il turno non è più modificabile");
        }
        saveData();
    }
    
    public void deleteTurnoPreparatorio(TurnoPreparatorio turno) {
        if (!turno.isModificabile()) {
            throw new IllegalStateException("Il turno non può essere eliminato");
        }
        turniPreparatori.remove(turno);
        saveData();
    }
    
    // CRUD Turni Servizio
    public TurnoServizio createTurnoServizio(LocalDate data, LocalTime inizio, LocalTime fine,
                                           String luogo, Evento evento, Servizio servizio) {
        TurnoServizio turno = new TurnoServizio(data, inizio, fine, luogo, evento, servizio);
        turniServizio.add(turno);
        saveData();
        return turno;
    }
    
    public TurnoServizio createTurnoServizioFromServizio(Servizio servizio, Evento evento, 
                                                       int tempoPrep, int tempoRig) {
        LocalDate data = evento.getDataInizio();
        LocalTime inizioServizio = servizio.getFasciaOrariaInizio();
        LocalTime fineServizio = servizio.getFasciaOrariaFine();
        
        // Calcola orari con tempo prep/rigoverno
        LocalTime inizioTurno = inizioServizio.minusMinutes(tempoPrep);
        LocalTime fineTurno = fineServizio.plusMinutes(tempoRig);
        
        TurnoServizio turno = createTurnoServizio(data, inizioTurno, fineTurno, 
                                                evento.getLuogo(), evento, servizio);
        turno.setTempoPreparazione(tempoPrep);
        turno.setTempoRigoverno(tempoRig);
        
        return turno;
    }
    
    public void updateTurnoServizio(TurnoServizio turno) {
        if (!turno.isModificabile()) {
            throw new IllegalStateException("Il turno non è più modificabile");
        }
        saveData();
    }
    
    public void deleteTurnoServizio(TurnoServizio turno) {
        if (!turno.isModificabile()) {
            throw new IllegalStateException("Il turno non può essere eliminato");
        }
        turniServizio.remove(turno);
        saveData();
    }
    
    // Gestione disponibilità
    public void dichiaraDisponibilita(User user, Turno turno) {
        disponibilita.computeIfAbsent(user, k -> new HashSet<>()).add(turno.getId());
    }
    
    public void ritiraDisponibilita(User user, Turno turno) {
        Set<Long> userDisp = disponibilita.get(user);
        if (userDisp != null) {
            userDisp.remove(turno.getId());
        }
    }
    
    public boolean isDisponibile(User user, Turno turno) {
        Set<Long> userDisp = disponibilita.get(user);
        return userDisp != null && userDisp.contains(turno.getId());
    }
    
    public List<User> getPersonaleDisponibile(Turno turno, Class<? extends User> tipoPersonale) {
        return disponibilita.entrySet().stream()
            .filter(entry -> tipoPersonale.isInstance(entry.getKey()))
            .filter(entry -> entry.getValue().contains(turno.getId()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    // Query methods
    public List<TurnoPreparatorio> getTurniPreparatoriPerPeriodo(LocalDate inizio, LocalDate fine) {
        return turniPreparatori.stream()
            .filter(t -> !t.getData().isBefore(inizio) && !t.getData().isAfter(fine))
            .sorted(Comparator.comparing(TurnoPreparatorio::getData)
                    .thenComparing(TurnoPreparatorio::getOrarioInizio))
            .collect(Collectors.toList());
    }
    
    public List<TurnoServizio> getTurniServizioPerPeriodo(LocalDate inizio, LocalDate fine) {
        return turniServizio.stream()
            .filter(t -> !t.getData().isBefore(inizio) && !t.getData().isAfter(fine))
            .sorted(Comparator.comparing(TurnoServizio::getData)
                    .thenComparing(TurnoServizio::getOrarioInizio))
            .collect(Collectors.toList());
    }
    
    public List<TurnoPreparatorio> getTurniPreparatoriLiberi(LocalDate data) {
        return turniPreparatori.stream()
            .filter(t -> t.getData().equals(data))
            .filter(t -> t.getStato() == StatoTurno.APERTO)
            .collect(Collectors.toList());
    }
    
    public List<TurnoPreparatorio> getTurniPreparatoriCompatibili(int minutiRichiesti, LocalDate data) {
        return getTurniPreparatoriLiberi(data).stream()
            .filter(t -> t.haSpazioPerCompito(minutiRichiesti))
            .collect(Collectors.toList());
    }
    
    public List<TurnoServizio> getTurniServizioPerEvento(Evento evento) {
        return turniServizio.stream()
            .filter(t -> evento.equals(t.getEvento()))
            .collect(Collectors.toList());
    }
    
    public List<TurnoPreparatorio> getAllTurniPreparatori() {
        return new ArrayList<>(turniPreparatori);
    }
    
    public List<TurnoServizio> getAllTurniServizio() {
        return new ArrayList<>(turniServizio);
    }
    
    // Creazione turni ricorrenti
    public List<TurnoPreparatorio> createTurniPreparatoriRicorrenti(LocalDate dataInizio, 
            LocalDate dataFine, java.time.DayOfWeek giornoSettimana, 
            LocalTime orarioInizio, LocalTime orarioFine, String luogo) {
        
        List<TurnoPreparatorio> turniCreati = new ArrayList<>();
        LocalDate current = dataInizio;
        
        // Trova il primo giorno del tipo richiesto
        while (!current.getDayOfWeek().equals(giornoSettimana) && !current.isAfter(dataFine)) {
            current = current.plusDays(1);
        }
        
        // Crea turni settimanali
        while (!current.isAfter(dataFine)) {
            TurnoPreparatorio turno = createTurnoPreparatorio(current, orarioInizio, orarioFine, luogo);
            turniCreati.add(turno);
            current = current.plusWeeks(1);
        }
        
        return turniCreati;
    }
    
    // Verifica sovrapposizioni
    public boolean hasSovrapposizioniTurni(LocalDate data, LocalTime inizio, LocalTime fine, 
                                         String luogo, Long escludiTurnoId) {
        List<Turno> turniGiorno = new ArrayList<>();
        turniGiorno.addAll(getTurniPreparatoriPerPeriodo(data, data));
        turniGiorno.addAll(getTurniServizioPerPeriodo(data, data));
        
        return turniGiorno.stream()
            .filter(t -> !t.getId().equals(escludiTurnoId))
            .filter(t -> t.getLuogo().equals(luogo))
            .anyMatch(t -> hasSovrapposizioneOrari(t.getOrarioInizio(), t.getOrarioFine(), inizio, fine));
    }
    
    private boolean hasSovrapposizioneOrari(LocalTime inizio1, LocalTime fine1, 
                                          LocalTime inizio2, LocalTime fine2) {
        return inizio1.isBefore(fine2) && inizio2.isBefore(fine1);
    }
}