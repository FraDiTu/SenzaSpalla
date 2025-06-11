module catring {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    
    // Esporta tutti i package necessari
    exports com.catring;
    exports com.catring.model;
    exports com.catring.controller;
    exports com.catring.viewfx;
    exports com.catring.creator;
    exports com.catring.information_expert;
    exports com.catring.singleton;
    exports com.catring.observer;
    exports com.catring.utils;
    
    // Apre i package per JavaFX (necessario per le view Java)
    opens com.catring to javafx.fxml;
    opens com.catring.controller to javafx.fxml;
    opens com.catring.viewfx to javafx.fxml;
    opens com.catring.model to javafx.base;
}