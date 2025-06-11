package com.catring.viewfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

/**
 * Classe base per tutte le view del progetto
 * Contiene metodi comuni per messaggi e utilita
 */
public abstract class BaseView extends VBox {
    
    /**
     * Costruttore base che imposta lo spacing
     */
    public BaseView() {
        super(10); // spacing di 10 pixel
        this.setPrefWidth(800);
        this.setPrefHeight(600);
    }
    
    /**
     * Mostra un messaggio di successo
     */
    public void mostraMessaggioSuccesso(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    
    /**
     * Mostra un messaggio di errore
     */
    public void mostraMessaggioErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    
    /**
     * Mostra un messaggio informativo
     */
    public void mostraMessaggioInfo(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    
    /**
     * Chiede conferma per una azione
     */
    protected boolean chiediConferma(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        return alert.showAndWait().get() == ButtonType.OK;
    }
    
    /**
     * Metodo astratto che ogni view deve implementare
     */
    public abstract void aggiornaView();
}