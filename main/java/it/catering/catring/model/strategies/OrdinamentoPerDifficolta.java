package it.catering.catring.model.strategies;

import it.catering.catring.model.entities.Compito;
import java.util.Comparator;
import java.util.List;

public class OrdinamentoPerDifficolta implements OrdinamentoStrategy {
    
    @Override
    public void ordina(List<Compito> compiti) {
        compiti.sort(Comparator.comparingInt(Compito::getTempoStimato).reversed());
    }
    
    @Override
    public String getNome() {
        return "Per Difficoltà (Tempo)";
    }
}