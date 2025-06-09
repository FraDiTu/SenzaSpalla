package it.catering.catring.model.factories;

import it.catering.catring.model.entities.*;

public abstract class ComponenteMenuFactory {
    
    public static VoceMenu createVoceMenu(Ricetta ricetta, String nomePersonalizzato, String sezione) {
        return new VoceMenu(ricetta, nomePersonalizzato, sezione);
    }
    
    public static SezioneMenu createSezioneMenu(String nome) {
        return new SezioneMenu(nome);
    }
    
    public static Menu createMenu(String titolo, Chef chef) {
        return new Menu(titolo, chef);
    }
}