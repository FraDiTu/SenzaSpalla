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
    
    // Apre tutti i package per JavaFX e per i test JUnit
    opens com.catring to javafx.fxml, java.base;
    opens com.catring.controller to javafx.fxml, java.base;
    opens com.catring.viewfx to javafx.fxml, java.base;
    opens com.catring.model to javafx.base, java.base;
    opens com.catring.creator to java.base;
    opens com.catring.information_expert to java.base;
    opens com.catring.singleton to java.base;
    opens com.catring.observer to java.base;
    opens com.catring.utils to java.base;
}