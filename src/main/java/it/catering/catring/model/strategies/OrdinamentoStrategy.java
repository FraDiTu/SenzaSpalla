package it.catering.catring.model.strategies;

import it.catering.catring.model.entities.Compito;
import java.util.List;

public interface OrdinamentoStrategy {
    void ordina(List<Compito> compiti);
    String getNome();
}
