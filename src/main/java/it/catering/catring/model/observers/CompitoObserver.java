package it.catering.catring.model.observers;

import it.catering.catring.model.entities.Compito;

public interface CompitoObserver {
    void onCompitoAssegnato(Compito compito);
    void onCompitoAggiornato(Compito compito);
    void onCompitoCompletato(Compito compito);
}