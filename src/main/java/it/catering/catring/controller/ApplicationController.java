package it.catering.catring.controller;

import it.catering.catring.model.entities.User;

public class ApplicationController {
    private static ApplicationController instance;
    private AuthController authController;
    private MenuController menuController;
    private CompitoController compitoController;
    
    private ApplicationController() {
        this.authController = AuthController.getInstance();
        this.menuController = new MenuController();
        this.compitoController = new CompitoController();
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
    
    public void updateCurrentUser() {
        User currentUser = authController.getCurrentUser();
        menuController.setCurrentUser(currentUser);
        compitoController.setCurrentUser(currentUser);
    }
}