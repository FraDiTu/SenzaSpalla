package it.catering.catring.model.strategies;

import it.catering.catring.model.entities.Compito;
import java.util.Comparator;
import java.util.List;

public class OrdinamentoPerCuoco implements OrdinamentoStrategy {
    
    @Override
    public void ordina(List<Compito> compiti) {
        compiti.sort(Comparator.comparing(c -> c.getCuoco().getNomeCompleto()));
    }
    
    @Override
    public String getNome() {
        return "Per Cuoco";
    }
}
