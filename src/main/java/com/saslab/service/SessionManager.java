package com.saslab.service;

import com.saslab.model.User;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestisce le sessioni degli utenti nel sistema Cat & Ring
 * Implementa pattern Singleton per garantire una gestione centralizzata delle sessioni
 */
public class SessionManager {
    
    private static SessionManager instance;
    private final Map<String, UserSession> activeSessions;
    private final int sessionTimeoutMinutes = 120; // 2 ore
    
    private SessionManager() {
        this.activeSessions = new ConcurrentHashMap<>();
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Crea una nuova sessione per l'utente
     */
    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        UserSession session = new UserSession(sessionId, user, LocalDateTime.now());
        
        // Rimuovi eventuali sessioni precedenti dello stesso utente
        removeUserSessions(user.getId());
        
        activeSessions.put(sessionId, session);
        return sessionId;
    }
    
    /**
     * Ottiene la sessione attiva per l'ID fornito
     */
    public UserSession getSession(String sessionId) {
        UserSession session = activeSessions.get(sessionId);
        
        if (session != null && isSessionExpired(session)) {
            activeSessions.remove(sessionId);
            return null;
        }
        
        return session;
    }
    
    /**
     * Ottiene l'utente dalla sessione
     */
    public User getUserFromSession(String sessionId) {
        UserSession session = getSession(sessionId);
        return session != null ? session.getUser() : null;
    }
    
    /**
     * Verifica se una sessione è valida
     */
    public boolean isValidSession(String sessionId) {
        return getSession(sessionId) != null;
    }
    
    /**
     * Aggiorna il timestamp dell'ultima attività della sessione
     */
    public void refreshSession(String sessionId) {
        UserSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.setLastActivity(LocalDateTime.now());
        }
    }
    
    /**
     * Termina una sessione specifica
     */
    public boolean endSession(String sessionId) {
        return activeSessions.remove(sessionId) != null;
    }
    
    /**
     * Termina tutte le sessioni di un utente
     */
    public void removeUserSessions(String userId) {
        activeSessions.entrySet().removeIf(entry -> 
            entry.getValue().getUser().getId().equals(userId));
    }
    
    /**
     * Pulisce le sessioni scadute
     */
    public void cleanupExpiredSessions() {
        activeSessions.entrySet().removeIf(entry -> 
            isSessionExpired(entry.getValue()));
    }
    
    /**
     * Ottiene il numero di sessioni attive
     */
    public int getActiveSessionCount() {
        cleanupExpiredSessions();
        return activeSessions.size();
    }
    
    /**
     * Ottiene tutte le sessioni attive (per amministrazione)
     */
    public Map<String, UserSession> getActiveSessions() {
        cleanupExpiredSessions();
        return new ConcurrentHashMap<>(activeSessions);
    }
    
    private boolean isSessionExpired(UserSession session) {
        return session.getLastActivity().isBefore(
            LocalDateTime.now().minusMinutes(sessionTimeoutMinutes));
    }
    
    /**
     * Classe interna per rappresentare una sessione utente
     */
    public static class UserSession {
        private final String sessionId;
        private final User user;
        private final LocalDateTime createdAt;
        private LocalDateTime lastActivity;
        
        public UserSession(String sessionId, User user, LocalDateTime createdAt) {
            this.sessionId = sessionId;
            this.user = user;
            this.createdAt = createdAt;
            this.lastActivity = createdAt;
        }
        
        public String getSessionId() { return sessionId; }
        public User getUser() { return user; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getLastActivity() { return lastActivity; }
        
        public void setLastActivity(LocalDateTime lastActivity) {
            this.lastActivity = lastActivity;
        }
        
        @Override
        public String toString() {
            return String.format("UserSession{sessionId='%s', user='%s', lastActivity=%s}", 
                               sessionId, user.getName(), lastActivity);
        }
    }
}