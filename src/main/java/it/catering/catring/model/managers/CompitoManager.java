// File: src/main/java/it/catering/catring/model/managers/CompitoManager.java
package it.catering.catring.model.managers;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.observers.*;
import it.catering.catring.model.persistence.JsonPersistenceManager;
import it.catering.catring.model.strategies.OrdinamentoStrategy;
import java.util.*;
import java.util.stream.Collectors;

public class CompitoManager implements Subject<CompitoObserver> {
    private static CompitoManager instance;
    private List<Compito> compiti;
    private List<CompitoObserver> observers;
    private OrdinamentoStrategy ordinamentoStrategy;
    private JsonPersistenceManager persistenceManager;
    
    private CompitoManager() {
        this.compiti = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.persistenceManager = JsonPersistenceManager.getInstance();
        loadData();
    }
    
    public static synchronized CompitoManager getInstance() {
        if (instance == null) {
            instance = new CompitoManager();
        }
        return instance;
    }
    
    private void loadData() {
        try {
            List<Compito> loadedCompiti = persistenceManager.loadCompiti();
            compiti.addAll(loadedCompiti);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei compiti: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try {
            persistenceManager.saveCompiti(compiti);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio dei compiti: " + e.getMessage());
        }
    }
    
    public Compito assegnaCompito(Preparazione preparazione, Cuoco cuoco, 
                                 int tempoStimato, double quantita) {
        Compito compito = new Compito(preparazione, cuoco, tempoStimato, quantita);
        compiti.add(compito);
        saveData();
        notifyCompitoAssegnato(compito);
        return compito;
    }
    
    public void updateCompito(Compito compito) {
        if (compiti.contains(compito)) {
            saveData();
            notifyCompitoAggiornato(compito);
        }
    }
    
    public void completaCompito(Compito compito) {
        compito.completa();
        saveData();
        notifyCompitoCompletato(compito);
    }
    
    public void deleteCompito(Compito compito) {
        if (compiti.remove(compito)) {
            saveData();
        }
    }
    
    public List<Compito> getCompitiPerCuoco(Cuoco cuoco) {
        return compiti.stream()
                .filter(c -> c.getCuoco().equals(cuoco))
                .toList();
    }
    
    public List<Compito> getTuttiICompiti() {
        List<Compito> listaOrdinata = new ArrayList<>(compiti);
        if (ordinamentoStrategy != null) {
            ordinamentoStrategy.ordina(listaOrdinata);
        }
        return listaOrdinata;
    }
    
    public List<Compito> getCompitiPerPreparazione(Preparazione preparazione) {
        return compiti.stream()
                .filter(c -> c.getPreparazione().equals(preparazione))
                .toList();
    }
    
    public void setOrdinamentoStrategy(OrdinamentoStrategy strategy) {
        this.ordinamentoStrategy = strategy;
    }
    
    // Metodi per statistiche
    public Map<String, Long> getStatisticheStati() {
        return compiti.stream()
                .collect(Collectors.groupingBy(
                    c -> c.getStato().toString(),
                    Collectors.counting()
                ));
    }
    
    public Map<String, Long> getStatisticheCuochi() {
        return compiti.stream()
                .collect(Collectors.groupingBy(
                    c -> c.getCuoco().getNomeCompleto(),
                    Collectors.counting()
                ));
    }
    
    public double getTempoTotaleStimato() {
        return compiti.stream()
                .mapToInt(Compito::getTempoStimato)
                .sum();
    }
    
    public long getCompitiCompletati() {
        return compiti.stream()
                .filter(c -> c.getStato() == it.catering.catring.model.states.StatoCompito.COMPLETATO)
                .count();
    }
    
    @Override
    public void addObserver(CompitoObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(CompitoObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        // Implementazione generica
    }
    
    private void notifyCompitoAssegnato(Compito compito) {
        for (CompitoObserver observer : observers) {
            observer.onCompitoAssegnato(compito);
        }
    }
    
    private void notifyCompitoAggiornato(Compito compito) {
        for (CompitoObserver observer : observers) {
            observer.onCompitoAggiornato(compito);
        }
    }
    
    private void notifyCompitoCompletato(Compito compito) {
        for (CompitoObserver observer : observers) {
            observer.onCompitoCompletato(compito);
        }
    }
}