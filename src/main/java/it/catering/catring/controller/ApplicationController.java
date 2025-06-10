package it.catering.catring.controller;

import it.catering.catring.model.entities.User;

public class ApplicationController {
    private static ApplicationController instance;
    private AuthController authController;
    private MenuController menuController;
    private CompitoController compitoController;
    private TurnoController turnoController;
    private EventoController eventoController;
    private PersonaleController personaleController;
    
    private ApplicationController() {
        this.authController = AuthController.getInstance();
        this.menuController = new MenuController();
        this.compitoController = new CompitoController();
        this.turnoController = new TurnoController();
        this.eventoController = new EventoController();
        this.personaleController = new PersonaleController();
    }
    
    public static synchronized ApplicationController getInstance() {
        if (instance == null) {
            instance = new ApplicationController();
        }
        return instance;
    }
    
    public AuthController getAuthController() {
        return authController;
    }
    
    public MenuController getMenuController() {
        return menuController;
    }
    
    public CompitoController getCompitoController() {
        return compitoController;
    }
    
    public TurnoController getTurnoController() {
        return turnoController;
    }
    
    public EventoController getEventoController() {
        return eventoController;
    }
    
    public PersonaleController getPersonaleController() {
        return personaleController;
    }
    
    public void updateCurrentUser() {
        User currentUser = authController.getCurrentUser();
        menuController.setCurrentUser(currentUser);
        compitoController.setCurrentUser(currentUser);
        turnoController.setCurrentUser(currentUser);
        eventoController.setCurrentUser(currentUser);
        personaleController.setCurrentUser(currentUser);
    }
}