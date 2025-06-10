// File: src/main/java/it/catering/catring/controller/CompitoController.java
package it.catering.catring.controller;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.managers.*;
import it.catering.catring.model.strategies.*;
import it.catering.catring.model.states.StatoCompito;
import java.util.List;

public class CompitoController {
    private CompitoManager compitoManager;
    private RicettarioManager ricettarioManager;
    private User currentUser;
    
    public CompitoController() {
        this.compitoManager = CompitoManager.getInstance();
        this.ricettarioManager = RicettarioManager.getInstance();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public List<Preparazione> getPreparazioniFromMenu(Menu menu) {
        if (!(currentUser instanceof Chef)) {
            throw new IllegalStateException("Solo gli chef possono visualizzare le preparazioni dei menu");
        }
        
        List<Preparazione> preparazioni = menu.getTutteLeRicette()
                .stream()
                .map(r -> (Preparazione) r)
                .toList();
        
        return preparazioni;
    }
    
    public List<Ricetta> getRicetteFromMenu(Menu menu) {
        if (!(currentUser instanceof Chef)) {
            throw new IllegalStateException("Solo gli chef possono visualizzare le ricette dei menu");
        }
        
        return menu.getTutteLeRicette();
    }
    
    public List<Preparazione> getPreparazioniDisponibili() {
        return ricettarioManager.getPreparazioniPubblicate();
    }
    
    public List<Ricetta> getRicetteDisponibili() {
        return ricettarioManager.getRicettePubblicate();
    }
    
    public Compito assegnaCompitoRicetta(Ricetta ricetta, Cuoco cuoco, int tempoStimato, double quantita) {
        if (!(currentUser instanceof Chef)) {
            throw new IllegalStateException("Solo gli chef possono assegnare compiti");
        }
        
        return compitoManager.assegnaCompito(ricetta, cuoco, tempoStimato, quantita);
    }
    
    public Compito assegnaCompitoPreparazione(Preparazione preparazione, Cuoco cuoco, int tempoStimato, double quantita) {
        if (!(currentUser instanceof Chef)) {
            throw new IllegalStateException("Solo gli chef possono assegnare compiti");
        }
        
        return compitoManager.assegnaCompito(preparazione, cuoco, tempoStimato, quantita);
    }
    
    public void modificaAssegnazione(Compito compito, Cuoco nuovoCuoco, int nuovoTempo, double nuovaQuantita) {
        if (!(currentUser instanceof Chef)) {
            throw new IllegalStateException("Solo gli chef possono modificare le assegnazioni");
        }
        
        if (compito.getStato() == StatoCompito.COMPLETATO) {
            throw new IllegalStateException("Non è possibile modificare un compito completato");
        }
        
        compito.setCuoco(nuovoCuoco);
        compito.setTempoStimato(nuovoTempo);
        compito.setQuantita(nuovaQuantita);
        
        compitoManager.updateCompito(compito);
    }
    
    public void iniziaCompito(Compito compito) {
        if (!(currentUser instanceof Cuoco) || !compito.getCuoco().equals(currentUser)) {
            throw new IllegalStateException("Solo il cuoco assegnato può iniziare questo compito");
        }
        
        compito.inizia();
        compitoManager.updateCompito(compito);
    }
    
    public void completaCompito(Compito compito) {
        if (!(currentUser instanceof Cuoco) || !compito.getCuoco().equals(currentUser)) {
            throw new IllegalStateException("Solo il cuoco assegnato può completare questo compito");
        }
        
        compitoManager.completaCompito(compito);
    }
    
    public void segnalaProblema(Compito compito, String note) {
        if (!(currentUser instanceof Cuoco) || !compito.getCuoco().equals(currentUser)) {
            throw new IllegalStateException("Solo il cuoco assegnato può segnalare problemi");
        }
        
        compito.segnalaProblema(note);
        compitoManager.updateCompito(compito);
    }
    
    public List<Compito> getCompitiPerCuocoCorrente() {
        if (!(currentUser instanceof Cuoco cuoco)) {
            throw new IllegalStateException("Solo i cuochi possono visualizzare i propri compiti");
        }
        
        return compitoManager.getCompitiPerCuoco(cuoco);
    }
    
    public List<Compito> getTuttiICompiti() {
        // Permesso sia a Chef che a Organizzatori per supervisione
        if (!(currentUser instanceof Chef) && !(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo chef e organizzatori possono visualizzare tutti i compiti");
        }
        
        return compitoManager.getTuttiICompiti();
    }
    
    public void setOrdinamentoCompiti(String tipoOrdinamento) {
        if (!(currentUser instanceof Chef) && !(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo chef e organizzatori possono ordinare i compiti");
        }
        
        OrdinamentoStrategy strategy = switch (tipoOrdinamento) {
            case "difficolta" -> new OrdinamentoPerDifficolta();
            case "cuoco" -> new OrdinamentoPerCuoco();
            case "stato" -> new OrdinamentoPerStato();
            case "menu" -> new OrdinamentoPerMenu();
            default -> throw new IllegalArgumentException("Tipo ordinamento non valido: " + tipoOrdinamento);
        };
        
        compitoManager.setOrdinamentoStrategy(strategy);
    }
}