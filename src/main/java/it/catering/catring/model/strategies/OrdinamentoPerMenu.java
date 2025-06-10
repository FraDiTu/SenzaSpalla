// File: src/main/java/it/catering/catring/model/strategies/OrdinamentoPerMenu.java
package it.catering.catring.model.strategies;

import it.catering.catring.model.entities.Compito;
import java.util.Comparator;
import java.util.List;

public class OrdinamentoPerMenu implements OrdinamentoStrategy {
    
    @Override
    public void ordina(List<Compito> compiti) {
        compiti.sort(Comparator.comparing(c -> c.getPreparazione().getNome()));
    }
    
    @Override
    public String getNome() {
        return "Per Menu/Ricetta";
    }
}