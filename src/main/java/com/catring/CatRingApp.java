package com.catring;

import com.catring.viewfx.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

public class CatRingApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {

            MainView mainView = new MainView(primaryStage);

            mainView.mostra();

            stampaInformazioniAvvio();
            
        } catch (Exception e) {
            System.err.println("Errore nell'avvio dell'applicazione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stampaInformazioniAvvio() {
        System.out.println("=== CAT & RING - SISTEMA AVVIATO ===");
        System.out.println("Sistema di gestione catering pronto all'uso!");
        System.out.println();
        System.out.println("FUNZIONALITA PRINCIPALI:");
        System.out.println("  • Gestione eventi assegnati");
        System.out.println("  • Creazione e personalizzazione menu");
        System.out.println("  • Gestione ricettario aziendale");
        System.out.println("  • Condivisione menu tramite file TXT e bacheca");
        System.out.println();
        System.out.println("ARCHITETTURA SOFTWARE:");
        System.out.println("  • Pattern MVC con view Java pure");
        System.out.println("  • Pattern GRASP per responsabilita ben definite");
        System.out.println("  • Pattern GoF per flessibilita e manutenibilita");
        System.out.println("  • Separazione completa Model-View-Controller");
        System.out.println();
        System.out.println("STRUTTURA DEL PROGETTO:");
        System.out.println("  • Model: Classi del dominio business");
        System.out.println("  • ViewFX: Interfacce utente in Java");
        System.out.println("  • Controller: Logica di controllo");
        System.out.println("  • Service: Logica di business (Singleton)");
        System.out.println("==========================================");
    }

    public static void main(String[] args) {
        launch(args);
    }
}