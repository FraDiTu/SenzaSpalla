package com.saslab.config;

/**
 * Configurazione centrale per i pattern utilizzati nell'applicazione
 * Documenta l'uso dei pattern GRASP e GoF nel sistema
 */
public class PatternConfiguration {
    
    /**
     * Pattern GRASP implementati:
     * 
     * 1. CONTROLLER
     *    - Tutti i controller nel package com.saslab.controller
     *    - Gestiscono il flusso delle operazioni tra UI e modello
     * 
     * 2. CREATOR
     *    - UserFactory: crea istanze di User
     *    - Recipe/Menu/Event: creano i propri oggetti correlati
     * 
     * 3. INFORMATION EXPERT
     *    - Recipe: gestisce le proprie informazioni e operazioni
     *    - Menu: conosce le proprie sezioni e voci
     *    - Event: gestisce servizi e stato
     * 
     * 4. LOW COUPLING & HIGH COHESION
     *    - Separazione tra model, controller, view
     *    - Ogni classe ha responsabilità ben definite
     * 
     * 5. SEPARATION OF CONCERNS
     *    - UI separata dalla logica business
     *    - Persistenza separata dal modello
     *    - Servizi separati dai controller
     */
    
    /**
     * Pattern GoF Creazionali:
     * 
     * 1. ABSTRACT FACTORY
     *    - UserFactory: crea famiglie di utenti
     *    - AdapterFactory: crea adapter per sistemi esterni
     * 
     * 2. SINGLETON
     *    - RecipeBook: unica istanza del ricettario
     *    - SessionManager: gestione centralizzata sessioni
     *    - NotificationService: servizio notifiche unico
     */
    
    /**
     * Pattern GoF Strutturali:
     * 
     * 1. ADAPTER
     *    - ExternalSystemAdapter: integra sistemi esterni
     *    - JSONCateringSystemAdapter: adatta formato JSON
     *    - XMLLegacySystemAdapter: adatta formato XML
     * 
     * 2. COMPOSITE
     *    - Menu/MenuSection/MenuItem: struttura ad albero
     *    - Permette di trattare uniformemente elementi singoli e composti
     * 
     * 3. DECORATOR
     *    - MenuDecorator: base per decoratori di menù
     *    - PremiumMenuDecorator: aggiunge funzionalità premium
     *    - ThemedMenuDecorator: aggiunge temi ai menù
     */
    
    /**
     * Pattern GoF Comportamentali:
     * 
     * 1. OBSERVER
     *    - Event.EventObserver: notifica cambiamenti eventi
     *    - Shift.ShiftObserver: notifica cambiamenti turni
     *    - NotificationService: sistema di notifiche
     * 
     * 2. STATE
     *    - Event.EventState: gestisce stati evento
     *    - Menu.MenuState: gestisce stati menù
     *    - Shift.ShiftState: gestisce stati turno
     * 
     * 3. STRATEGY
     *    - PricingStrategy: strategie di pricing
     *    - StandardPricingStrategy: pricing standard
     *    - PremiumPricingStrategy: pricing premium
     *    - BudgetPricingStrategy: pricing economico
     * 
     * 4. VISITOR
     *    - MenuVisitor: operazioni sui menù
     *    - MenuPriceCalculatorVisitor: calcola prezzi
     *    - MenuExportVisitor: esporta in vari formati
     */
    
    // Classe di configurazione, non istanziabile
    private PatternConfiguration() {
        throw new AssertionError("Classe di configurazione non istanziabile");
    }
}