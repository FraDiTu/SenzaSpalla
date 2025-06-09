package it.catering.catring.model.strategies;

import it.catering.catring.model.entities.Compito;
import java.util.Comparator;
import java.util.List;

public class OrdinamentoPerStato implements OrdinamentoStrategy {
    
    @Override
    public void ordina(List<Compito> compiti) {
        compiti.sort(Comparator.comparing(c -> c.getStato().ordinal()));
    }
    
    @Override
    public String getNome() {
        return "Per Stato";
    }
}