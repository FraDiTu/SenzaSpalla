package com.saslab.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servizio di notifiche per il sistema Cat & Ring
 * Implementa pattern Observer per la gestione delle notifiche
 * Implementa pattern Singleton per garantire un'unica istanza
 */

public class NotificationService {
    
    private static NotificationService instance;
    private final Map<String, List<NotificationObserver>> observers;
    private final Map<String, List<Notification>> userNotifications;
    
    private NotificationService() {
        this.observers = new ConcurrentHashMap<>();
        this.userNotifications = new ConcurrentHashMap<>();
    }
    
    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    /**
     * Registra un observer per un utente specifico
     */
    public void addObserver(String userId, NotificationObserver observer) {
        observers.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(observer);
    }
    
    /**
     * Rimuove un observer
     */
    
    public void removeObserver(String userId, NotificationObserver observer) {
        List<NotificationObserver> userObservers = observers.get(userId);
        if (userObservers != null) {
            userObservers.remove(observer);
        }
    }
    
    /**
     * Invia una notifica a un utente
     */
    public void sendNotification(String userId, String title, String message, NotificationType type) {
        Notification notification = new Notification(
            UUID.randomUUID().toString(),
            userId,
            title,
            message,
            type,
            LocalDateTime.now()
        );
        
        // Salva la notifica
        userNotifications.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(notification);
        
        // Notifica gli observer
        List<NotificationObserver> userObservers = observers.get(userId);
        if (userObservers != null) {
            for (NotificationObserver observer : userObservers) {
                observer.onNotificationReceived(notification);
            }
        }
    }
    
    /**
     * Invia una notifica broadcast a tutti gli utenti di un ruolo
     */
    public void broadcastNotification(Set<String> userIds, String title, String message, NotificationType type) {
        for (String userId : userIds) {
            sendNotification(userId, title, message, type);
        }
    }
    
    /**
     * Ottiene le notifiche non lette di un utente
     */
    public List<Notification> getUnreadNotifications(String userId) {
        List<Notification> allNotifications = userNotifications.get(userId);
        if (allNotifications == null) return new ArrayList<>();
        
        return allNotifications.stream()
                .filter(n -> !n.isRead())
                .sorted((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Ottiene tutte le notifiche di un utente
     */
    public List<Notification> getAllNotifications(String userId) {
        List<Notification> allNotifications = userNotifications.get(userId);
        if (allNotifications == null) return new ArrayList<>();
        
        return new ArrayList<>(allNotifications);
    }
    
    /**
     * Segna una notifica come letta
     */
    public void markAsRead(String notificationId) {
        for (List<Notification> notifications : userNotifications.values()) {
            for (Notification notification : notifications) {
                if (notification.getId().equals(notificationId)) {
                    notification.markAsRead();
                    return;
                }
            }
        }
    }
    
    /**
     * Elimina una notifica
     */
    public void deleteNotification(String userId, String notificationId) {
        List<Notification> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.removeIf(n -> n.getId().equals(notificationId));
        }
    }
    
    /**
     * Pulisce le notifiche vecchie (più di 30 giorni)
     */
    public void cleanOldNotifications() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        for (List<Notification> notifications : userNotifications.values()) {
            notifications.removeIf(n -> n.getTimestamp().isBefore(thirtyDaysAgo));
        }
    }
    
    // Observer interface
    public interface NotificationObserver {
        void onNotificationReceived(Notification notification);
    }
    
    // Notification class
    public static class Notification {
        private final String id;
        private final String userId;
        private final String title;
        private final String message;
        private final NotificationType type;
        private final LocalDateTime timestamp;
        private boolean read;
        
        public Notification(String id, String userId, String title, String message, 
                          NotificationType type, LocalDateTime timestamp) {
            this.id = id;
            this.userId = userId;
            this.title = title;
            this.message = message;
            this.type = type;
            this.timestamp = timestamp;
            this.read = false;
        }
        
        // Getters
        public String getId() { return id; }
        public String getUserId() { return userId; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public NotificationType getType() { return type; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public boolean isRead() { return read; }
        
        public void markAsRead() {
            this.read = true;
        }
    }
    
    // Notification types
    public enum NotificationType {
        INFO("Informazione", "blue"),
        SUCCESS("Successo", "green"),
        WARNING("Attenzione", "orange"),
        ERROR("Errore", "red"),
        TASK_ASSIGNED("Compito Assegnato", "purple"),
        EVENT_UPDATE("Aggiornamento Evento", "teal"),
        MENU_APPROVED("Menù Approvato", "green"),
        SHIFT_REMINDER("Promemoria Turno", "yellow");
        
        private final String displayName;
        private final String color;
        
        NotificationType(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
}
