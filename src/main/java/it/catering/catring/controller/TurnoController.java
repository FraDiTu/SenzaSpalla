package it.catering.catring.controller;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.managers.TurnoManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TurnoController {
    private TurnoManager turnoManager;
    private User currentUser;
    
    public TurnoController() {
        this.turnoManager = TurnoManager.getInstance();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    // Turni Preparatori
    public TurnoPreparatorio createTurnoPreparatorio(LocalDate data, LocalTime inizio, 
                                                   LocalTime fine, String luogo) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono creare turni preparatori");
        }
        
        return turnoManager.createTurnoPreparatorio(data, inizio, fine, luogo);
    }
    
    public void updateTurnoPreparatorio(TurnoPreparatorio turno) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono modificare turni preparatori");
        }
        
        turnoManager.updateTurnoPreparatorio(turno);
    }
    
    public void deleteTurnoPreparatorio(TurnoPreparatorio turno) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono eliminare turni preparatori");
        }
        
        turnoManager.deleteTurnoPreparatorio(turno);
    }
    
    public List<TurnoPreparatorio> createTurniPreparatoriRicorrenti(LocalDate dataInizio, 
            LocalDate dataFine, java.time.DayOfWeek giornoSettimana, 
            LocalTime orarioInizio, LocalTime orarioFine, String luogo) {
        
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono creare turni ricorrenti");
        }
        
        return turnoManager.createTurniPreparatoriRicorrenti(dataInizio, dataFine, 
                                                           giornoSettimana, orarioInizio, orarioFine, luogo);
    }
    
    // Turni Servizio
    public TurnoServizio createTurnoServizio(LocalDate data, LocalTime inizio, LocalTime fine,
                                           String luogo, Evento evento, Servizio servizio) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono creare turni di servizio");
        }
        
        return turnoManager.createTurnoServizio(data, inizio, fine, luogo, evento, servizio);
    }
    
    public TurnoServizio createTurnoServizioFromServizio(Servizio servizio, Evento evento, 
                                                       int tempoPrep, int tempoRig) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono creare turni di servizio");
        }
        
        return turnoManager.createTurnoServizioFromServizio(servizio, evento, tempoPrep, tempoRig);
    }
    
    // Disponibilità
    public void dichiaraDisponibilita(Turno turno) {
        if (currentUser instanceof Cuoco || currentUser instanceof PersonaleServizio) {
            turnoManager.dichiaraDisponibilita(currentUser, turno);
        } else {
            throw new IllegalStateException("Solo cuochi e personale di servizio possono dichiarare disponibilità");
        }
    }
    
    public void ritiraDisponibilita(Turno turno) {
        if (currentUser instanceof Cuoco || currentUser instanceof PersonaleServizio) {
            turnoManager.ritiraDisponibilita(currentUser, turno);
        } else {
            throw new IllegalStateException("Solo cuochi e personale di servizio possono ritirare disponibilità");
        }
    }
    
    public boolean isDisponibile(Turno turno) {
        return turnoManager.isDisponibile(currentUser, turno);
    }
    
    // Query methods
    public List<TurnoPreparatorio> getTurniPreparatoriPerPeriodo(LocalDate inizio, LocalDate fine) {
        return turnoManager.getTurniPreparatoriPerPeriodo(inizio, fine);
    }
    
    public List<TurnoServizio> getTurniServizioPerPeriodo(LocalDate inizio, LocalDate fine) {
        return turnoManager.getTurniServizioPerPeriodo(inizio, fine);
    }
    
    public List<TurnoPreparatorio> getTurniPreparatoriCompatibili(int minutiRichiesti, LocalDate data) {
        return turnoManager.getTurniPreparatoriCompatibili(minutiRichiesti, data);
    }
    
    public List<TurnoPreparatorio> getAllTurniPreparatori() {
        return turnoManager.getAllTurniPreparatori();
    }
    
    public List<TurnoServizio> getAllTurniServizio() {
        return turnoManager.getAllTurniServizio();
    }
    
    public List<User> getPersonaleDisponibile(Turno turno, Class<? extends User> tipoPersonale) {
        return turnoManager.getPersonaleDisponibile(turno, tipoPersonale);
    }
}