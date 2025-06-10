module catring {
    requires javafx.controls;
    requires javafx.fxml;
    
    // Esporta tutti i package dei pattern
    exports com.catring;
    exports com.catring.model;
    exports com.catring.controller;
    exports com.catring.creator;
    exports com.catring.information_expert;
    exports com.catring.singleton;
    exports com.catring.observer;
    exports com.catring.utils;
    
    // Apre i package per JavaFX
    opens com.catring to javafx.fxml;
    opens com.catring.controller to javafx.fxml;
    opens com.catring.model to javafx.base;
}