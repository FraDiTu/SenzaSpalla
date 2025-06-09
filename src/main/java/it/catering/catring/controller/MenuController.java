package it.catering.catring.controller;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.factories.ComponenteMenuFactory;
import it.catering.catring.model.managers.*;
import java.util.List;

public class MenuController {
    private MenuManager menuManager;
    private RicettarioManager ricettarioManager;
    private User currentUser;
    
    public MenuController() {
        this.menuManager = MenuManager.getInstance();
        this.ricettarioManager = RicettarioManager.getInstance();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public Menu createMenu(String titolo, String descrizione) {
        if (!(currentUser instanceof Chef chef)) {
            throw new IllegalStateException("Solo gli chef possono creare menu");
        }
        
        Menu menu = menuManager.createMenu(titolo, chef);
        if (descrizione != null && !descrizione.trim().isEmpty()) {
            menu.setDescrizione(descrizione);
        }
        
        return menu;
    }
    
    public void addSezioneToMenu(Menu menu, String nomeSezione) {
        if (!canModifyMenu(menu)) {
            throw new IllegalStateException("Non è possibile modificare questo menu");
        }
        
        SezioneMenu sezione = ComponenteMenuFactory.createSezioneMenu(nomeSezione);
        menu.aggiungiSezione(sezione);
        menuManager.updateMenu(menu);
    }
    
    public void addRicettaToSezione(Menu menu, String nomeSezione, Ricetta ricetta, String nomePersonalizzato) {
        if (!canModifyMenu(menu)) {
            throw new IllegalStateException("Non è possibile modificare questo menu");
        }
        
        SezioneMenu sezione = menu.trovaSezione(nomeSezione);
        if (sezione == null) {
            throw new IllegalArgumentException("Sezione non trovata: " + nomeSezione);
        }
        
        VoceMenu voce = ComponenteMenuFactory.createVoceMenu(ricetta, nomePersonalizzato, nomeSezione);
        sezione.aggiungiVoce(voce);
        menuManager.updateMenu(menu);
    }
    
    public void removeRicettaFromSezione(Menu menu, String nomeSezione, VoceMenu voce) {
        if (!canModifyMenu(menu)) {
            throw new IllegalStateException("Non è possibile modificare questo menu");
        }
        
        SezioneMenu sezione = menu.trovaSezione(nomeSezione);
        if (sezione != null) {
            sezione.rimuoviVoce(voce);
            menuManager.updateMenu(menu);
        }
    }
    
    public void moveRicettaBetweenSezioni(Menu menu, VoceMenu voce, String sezioneSorgente, String sezioneDestinazione) {
        if (!canModifyMenu(menu)) {
            throw new IllegalStateException("Non è possibile modificare questo menu");
        }
        
        SezioneMenu sezSorgente = menu.trovaSezione(sezioneSorgente);
        SezioneMenu sezDestinazione = menu.trovaSezione(sezioneDestinazione);
        
        if (sezSorgente != null && sezDestinazione != null) {
            sezSorgente.rimuoviVoce(voce);
            voce.setSezione(sezioneDestinazione);
            sezDestinazione.aggiungiVoce(voce);
            menuManager.updateMenu(menu);
        }
    }
    
    public void updateMenuInfo(Menu menu, boolean cuocoRichiesto, boolean soloPiattiFreddi, 
                              boolean cucinaRichiesta, boolean adeguatoBuffet, boolean fingerFood) {
        if (!canModifyMenu(menu)) {
            throw new IllegalStateException("Non è possibile modificare questo menu");
        }
        
        menu.setCuocoRichiesto(cuocoRichiesto);
        menu.setSoloPiattiFreddi(soloPiattiFreddi);
        menu.setCucinaRichiesta(cucinaRichiesta);
        menu.setAdeguatoBuffet(adeguatoBuffet);
        menu.setFingerFood(fingerFood);
        
        menuManager.updateMenu(menu);
    }
    
    public void deleteMenu(Menu menu) {
        if (!canModifyMenu(menu)) {
            throw new IllegalStateException("Non è possibile eliminare questo menu");
        }
        
        menuManager.deleteMenu(menu);
    }
    
    public List<Menu> getMenusByCurrentChef() {
        if (!(currentUser instanceof Chef chef)) {
            throw new IllegalStateException("Solo gli chef possono visualizzare i propri menu");
        }
        
        return menuManager.getMenusByChef(chef);
    }
    
    public List<Menu> getAllMenusDisponibili() {
        return menuManager.getMenusDisponibili();
    }
    
    public List<Ricetta> getRicetteDisponibili() {
        return ricettarioManager.getRicettePubblicate();
    }
    
    private boolean canModifyMenu(Menu menu) {
        return !menu.isUtilizzato() && 
               (currentUser instanceof Chef && menu.getChef().equals(currentUser));
    }
}