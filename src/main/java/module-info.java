// Fix per module-info.java - Versione corretta con tutte le dipendenze
module catring {
    // JavaFX dependencies with transitive to avoid accessibility warnings
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.desktop;
    
    // Jackson for JSON persistence
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.annotation;
    
    // Export main application package
    exports it.catering.catring;
    
    // Export controller package
    exports it.catering.catring.controller;
    
    // Export view packages
    exports it.catering.catring.view;
    exports it.catering.catring.view.components;
    
    // Export model main package
    exports it.catering.catring.model;
    
    // Export all model sub-packages to make entities accessible
    exports it.catering.catring.model.entities;
    exports it.catering.catring.model.factories;
    exports it.catering.catring.model.managers;
    exports it.catering.catring.model.observers;
    exports it.catering.catring.model.states;
    exports it.catering.catring.model.strategies;
    exports it.catering.catring.model.visitors;
    exports it.catering.catring.model.persistence;
    exports it.catering.catring.model.exporters;
    
    // Aprire i pacchetti a Jackson per la serializzazione/deserializzazione
    opens it.catering.catring.model.entities to com.fasterxml.jackson.databind;
    opens it.catering.catring.model.states to com.fasterxml.jackson.databind;
}