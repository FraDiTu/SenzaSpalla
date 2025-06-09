package it.catering.catring.model.managers;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.observers.*;
import it.catering.catring.model.strategies.OrdinamentoStrategy;
import java.util.*;

public class CompitoManager implements Subject<CompitoObserver> {
    private static CompitoManager instance;
    private List<Compito> compiti;
    private List<CompitoObserver> observers;
    private OrdinamentoStrategy ordinamentoStrategy;
    
    private CompitoManager() {
        this.compiti = new ArrayList<>();
        this.observers = new ArrayList<>();
    }
    
    public static synchronized CompitoManager getInstance() {
        if (instance == null) {
            instance = new CompitoManager();
        }
        return instance;
    }
    
    public Compito assegnaCompito(Preparazione preparazione, Cuoco cuoco, 
                                 int tempoStimato, double quantita) {
        Compito compito = new Compito(preparazione, cuoco, tempoStimato, quantita);
        compiti.add(compito);
        notifyCompitoAssegnato(compito);
        return compito;
    }
    
    public void updateCompito(Compito compito) {
        if (compiti.contains(compito)) {
            notifyCompitoAggiornato(compito);
        }
    }
    
    public void completaCompito(Compito compito) {
        compito.completa();
        notifyCompitoCompletato(compito);
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
    
    public void setOrdinamentoStrategy(OrdinamentoStrategy strategy) {
        this.ordinamentoStrategy = strategy;
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