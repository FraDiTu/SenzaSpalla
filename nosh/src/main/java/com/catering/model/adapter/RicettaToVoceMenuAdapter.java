// src/main/java/com/catering/model/adapter/RicettaToVoceMenuAdapter.java
package com.catering.model.adapter;

import com.catering.model.domain.Ricetta;
import com.catering.model.domain.VoceMenu;

/**
 * Adapter pattern - adatta Ricetta per uso come VoceMenu
 * Permette di utilizzare una Ricetta in un Menu personalizzando il nome visualizzato
 * e specificando la sezione di destinazione
 */
public class RicettaToVoceMenuAdapter {
    private final Ricetta ricetta;
    private String nomePersonalizzato;
    private String sezioneTarget;
    
    /**
     * Costruisce un adapter per la ricetta specificata
     * @param ricetta la ricetta da adattare
     */
    public RicettaToVoceMenuAdapter(Ricetta ricetta) {
        this.ricetta = ricetta;
        this.nomePersonalizzato = ricetta.getNome(); // Nome di default = nome ricetta
    }
    
    /**
     * Imposta un nome personalizzato per la voce menu
     * @param nome il nome da visualizzare nel menu
     * @return this adapter per method chaining
     */
    public RicettaToVoceMenuAdapter withNomePersonalizzato(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome personalizzato non può essere null o vuoto");
        }
        this.nomePersonalizzato = nome.trim();
        return this;
    }
    
    /**
     * Specifica la sezione di destinazione nel menu
     * @param sezione la sezione del menu dove inserire la voce
     * @return this adapter per method chaining
     */
    public RicettaToVoceMenuAdapter inSezione(String sezione) {
        if (sezione == null || sezione.trim().isEmpty()) {
            throw new IllegalArgumentException("La sezione non può essere null o vuota");
        }
        this.sezioneTarget = sezione.trim();
        return this;
    }
    
    /**
     * Converte l'adapter in una VoceMenu
     * @return una nuova istanza di VoceMenu
     * @throws IllegalStateException se la sezione non è stata specificata
     */
    public VoceMenu toVoceMenu() {
        if (sezioneTarget == null) {
            throw new IllegalStateException("Sezione non specificata. Utilizzare inSezione() prima di chiamare toVoceMenu()");
        }
        return new VoceMenu(ricetta.getId(), nomePersonalizzato, sezioneTarget);
    }
    
    /**
     * Ottiene la ricetta originale
     * @return la ricetta originale utilizzata dall'adapter
     */
    public Ricetta getRicettaOriginale() {
        return ricetta;
    }
    
    /**
     * Ottiene il nome personalizzato impostato
     * @return il nome che verrà visualizzato nel menu
     */
    public String getNomePersonalizzato() {
        return nomePersonalizzato;
    }
    
    /**
     * Ottiene la sezione target impostata
     * @return la sezione dove verrà inserita la voce menu, null se non impostata
     */
    public String getSezioneTarget() {
        return sezioneTarget;
    }
    
    /**
     * Verifica se l'adapter è pronto per la conversione
     * @return true se tutti i parametri necessari sono stati impostati
     */
    public boolean isReady() {
        return sezioneTarget != null && !sezioneTarget.trim().isEmpty();
    }
    
    /**
     * Reset dell'adapter ai valori di default
     * @return this adapter per method chaining
     */
    public RicettaToVoceMenuAdapter reset() {
        this.nomePersonalizzato = ricetta.getNome();
        this.sezioneTarget = null;
        return this;
    }
    
    /**
     * Crea una copia dell'adapter con le stesse impostazioni
     * @return una nuova istanza di adapter con gli stessi valori
     */
    public RicettaToVoceMenuAdapter copy() {
        RicettaToVoceMenuAdapter copy = new RicettaToVoceMenuAdapter(this.ricetta);
        copy.nomePersonalizzato = this.nomePersonalizzato;
        copy.sezioneTarget = this.sezioneTarget;
        return copy;
    }
    
    @Override
    public String toString() {
        return String.format("RicettaToVoceMenuAdapter[ricetta=%s, nomePersonalizzato='%s', sezione='%s']",
                ricetta.getNome(), nomePersonalizzato, sezioneTarget);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RicettaToVoceMenuAdapter)) return false;
        
        RicettaToVoceMenuAdapter that = (RicettaToVoceMenuAdapter) o;
        
        return ricetta.equals(that.ricetta) &&
               nomePersonalizzato.equals(that.nomePersonalizzato) &&
               java.util.Objects.equals(sezioneTarget, that.sezioneTarget);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(ricetta, nomePersonalizzato, sezioneTarget);
    }
}