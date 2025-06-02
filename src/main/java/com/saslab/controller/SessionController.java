package com.saslab.controller;

import com.saslab.model.User;
import com.saslab.service.SessionManager;
import java.util.Map;

/**
 * Controller per la gestione delle sessioni utente
 * Implementa pattern Controller (GRASP) come intermediario tra UI e SessionManager
 * Implementa pattern Facade per semplificare l'accesso alle funzionalità di sessione
 */
public class SessionController {
    
    private final SessionManager sessionManager;
    
    public SessionController() {
        this.sessionManager = SessionManager.getInstance();
    }
    
    /**
     * Crea una nuova sessione per l'utente autenticato
     */
    public String createSession(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utente non può essere null");
        }
        
        if (!user.isActive()) {
            throw new IllegalStateException("L'utente deve essere attivo per creare una sessione");
        }
        
        return sessionManager.createSession(user);
    }
    
    /**
     * Verifica se una sessione è valida
     */
    public boolean isSessionValid(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return false;
        }
        
        return sessionManager.isValidSession(sessionId);
    }
    
    /**
     * Ottiene l'utente associato a una sessione
     */
    public User getUserFromSession(String sessionId) {
        if (!isSessionValid(sessionId)) {
            return null;
        }
        
        return sessionManager.getUserFromSession(sessionId);
    }
    
    /**
     * Aggiorna l'attività della sessione
     */
    public void refreshSession(String sessionId) {
        if (isSessionValid(sessionId)) {
            sessionManager.refreshSession(sessionId);
        }
    }
    
    /**
     * Termina una sessione
     */
    public boolean endSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return false;
        }
        
        return sessionManager.endSession(sessionId);
    }
    
    /**
     * Ottiene informazioni sulle sessioni attive (per amministrazione)
     */
    public Map<String, Object> getSessionStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("activeSessions", sessionManager.getActiveSessionCount());
        stats.put("sessions", sessionManager.getActiveSessions());
        
        return stats;
    }
    
    /**
     * Pulisce le sessioni scadute
     */
    public void performSessionCleanup() {
        sessionManager.cleanupExpiredSessions();
    }
    
    /**
     * Termina tutte le sessioni di un utente specifico
     */
    public void terminateUserSessions(String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            sessionManager.removeUserSessions(userId);
        }
    }
}
