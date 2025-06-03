package com.saslab.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe che rappresenta un Raggruppamento di Turni nel sistema Cat & Ring
 * Gestisce sia raggruppamenti singoli che ricorrenti
 * Implementa pattern Composite per gestire turni raggruppati
 */
public class RaggruppamentoTurni {
    
    public enum TipoRaggruppamento {
        SINGOLO("Singolo"),
        RICORRENTE("Ricorrente");
        
        private final String displayName;
        
        TipoRaggruppamento(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum StatoRaggruppamento {
        ATTIVO("Attivo"),
        SOSPESO("Sospeso"),
        TERMINATO("Terminato");
        
        private final String displayName;
        
        StatoRaggruppamento(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String nome;
    private String descrizione;
    private TipoRaggruppamento tipo;
    private StatoRaggruppamento stato;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private List<String> turniIds;
    private String pattern; // per raggruppamenti ricorrenti (es. "ogni sabato e domenica")
    private Map<String, String> eccezioni; // data -> nuovo raggruppamento per quella data
    private LocalDateTime creatoIl;
    private LocalDateTime modificatoIl;
    private String note;
    private boolean modificabile;
    
    // Constructor per raggruppamento singolo
    public RaggruppamentoTurni(String id, String nome, List<String> turniIds) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.nome = Objects.requireNonNull(nome, "Il nome non può essere null");
        this.turniIds = new ArrayList<>(Objects.requireNonNull(turniIds, "La lista turni non può essere null"));
        
        if (turniIds.isEmpty()) {
            throw new IllegalArgumentException("Il raggruppamento deve contenere almeno un turno");
        }
        
        this.tipo = TipoRaggruppamento.SINGOLO;
        this.stato = StatoRaggruppamento.ATTIVO;
        this.eccezioni = new HashMap<>();
        this.creatoIl = LocalDateTime.now();
        this.modificatoIl = LocalDateTime.now();
        this.modificabile = true;
    }
    
    // Constructor per raggruppamento ricorrente
    public RaggruppamentoTurni(String id, String nome, LocalDate dataInizio, LocalDate dataFine, String pattern) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.nome = Objects.requireNonNull(nome, "Il nome non può essere null");
        this.dataInizio = Objects.requireNonNull(dataInizio, "La data di inizio non può essere null");
        this.dataFine = Objects.requireNonNull(dataFine, "La data di fine non può essere null");
        this.pattern = Objects.requireNonNull(pattern, "Il pattern non può essere null");
        
        if (dataFine.isBefore(dataInizio)) {
            throw new IllegalArgumentException("La data di fine deve essere successiva a quella di inizio");
        }
        
        this.tipo = TipoRaggruppamento.RICORRENTE;
        this.stato = StatoRaggruppamento.ATTIVO;
        this.turniIds = new ArrayList<>();
        this.eccezioni = new HashMap<>();
        this.creatoIl = LocalDateTime.now();
        this.modificatoIl = LocalDateTime.now();
        this.modificabile = true;
    }
    
    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public TipoRaggruppamento getTipo() { return tipo; }
    public StatoRaggruppamento getStato() { return stato; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public List<String> getTurniIds() { return new ArrayList<>(turniIds); }
    public String getPattern() { return pattern; }
    public Map<String, String> getEccezioni() { return new HashMap<>(eccezioni); }
    public LocalDateTime getCreatoIl() { return creatoIl; }
    public LocalDateTime getModificatoIl() { return modificatoIl; }
    public String getNote() { return note; }
    public boolean isModificabile() { return modificabile; }
    
    // Setters
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
        updateModificatoIl();
    }
    
    public void setNote(String note) {
        this.note = note;
        updateModificatoIl();
    }
    
    // Business methods
    public void aggiungiTurno(String turnoId) {
        if (!modificabile) {
            throw new IllegalStateException("Il raggruppamento non è modificabile");
        }
        
        if (stato != StatoRaggruppamento.ATTIVO) {
            throw new IllegalStateException("Non è possibile modificare un raggruppamento non attivo");
        }
        
        if (!turniIds.contains(turnoId)) {
            turniIds.add(turnoId);
            updateModificatoIl();
        }
    }
    
    public boolean rimuoviTurno(String turnoId) {
        if (!modificabile) {
            throw new IllegalStateException("Il raggruppamento non è modificabile");
        }
        
        if (stato != StatoRaggruppamento.ATTIVO) {
            throw new IllegalStateException("Non è possibile modificare un raggruppamento non attivo");
        }
        
        if (turniIds.size() <= 1) {
            throw new IllegalStateException("Un raggruppamento deve contenere almeno un turno");
        }
        
        boolean rimosso = turniIds.remove(turnoId);
        if (rimosso) {
            updateModificatoIl();
        }
        return rimosso;
    }
    
    public void aggiungiEccezione(LocalDate data, String nuovoRaggruppamentoId) {
        if (tipo != TipoRaggruppamento.RICORRENTE) {
            throw new IllegalStateException("Le eccezioni sono disponibili solo per raggruppamenti ricorrenti");
        }
        
        if (!modificabile) {
            throw new IllegalStateException("Il raggruppamento non è modificabile");
        }
        
        if (data.isBefore(dataInizio) || data.isAfter(dataFine)) {
            throw new IllegalArgumentException("La data dell'eccezione deve essere nell'intervallo del raggruppamento");
        }
        
        eccezioni.put(data.toString(), nuovoRaggruppamentoId);
        updateModificatoIl();
    }
    
    public void rimuoviEccezione(LocalDate data) {
        if (!modificabile) {
            throw new IllegalStateException("Il raggruppamento non è modificabile");
        }
        
        eccezioni.remove(data.toString());
        updateModificatoIl();
    }
    
    public void sospendi() {
        if (stato == StatoRaggruppamento.TERMINATO) {
            throw new IllegalStateException("Non è possibile sospendere un raggruppamento terminato");
        }
        
        this.stato = StatoRaggruppamento.SOSPESO;
        updateModificatoIl();
    }
    
    public void riattiva() {
        if (stato == StatoRaggruppamento.TERMINATO) {
            throw new IllegalStateException("Non è possibile riattivare un raggruppamento terminato");
        }
        
        this.stato = StatoRaggruppamento.ATTIVO;
        updateModificatoIl();
    }
    
    public void termina() {
        this.stato = StatoRaggruppamento.TERMINATO;
        this.modificabile = false;
        updateModificatoIl();
    }
    
    public void bloccaModifiche() {
        this.modificabile = false;
        updateModificatoIl();
    }
    
    public void sbloccaModifiche() {
        if (stato == StatoRaggruppamento.TERMINATO) {
            throw new IllegalStateException("Non è possibile sbloccare un raggruppamento terminato");
        }
        
        this.modificabile = true;
        updateModificatoIl();
    }
    
    public boolean hasEccezione(LocalDate data) {
        return eccezioni.containsKey(data.toString());
    }
    
    public String getEccezione(LocalDate data) {
        return eccezioni.get(data.toString());
    }
    
    public boolean isAttivo() {
        return stato == StatoRaggruppamento.ATTIVO;
    }
    
    public boolean isScaduto() {
        if (dataFine == null) return false;
        return LocalDate.now().isAfter(dataFine);
    }
    
    public boolean isValido() {
        return isAttivo() && !isScaduto() && !turniIds.isEmpty();
    }
    
    public int getNumeroTurni() {
        return turniIds.size();
    }
    
    public boolean contieneData(LocalDate data) {
        if (tipo == TipoRaggruppamento.SINGOLO) {
            return true; // Per raggruppamenti singoli non ha senso il controllo sulla data
        }
        
        return !data.isBefore(dataInizio) && !data.isAfter(dataFine);
    }
    
    public String getInfoDettagliate() {
        StringBuilder info = new StringBuilder();
        info.append("Raggruppamento: ").append(nome).append("\n");
        info.append("Tipo: ").append(tipo.getDisplayName()).append("\n");
        info.append("Stato: ").append(stato.getDisplayName()).append("\n");
        info.append("Turni: ").append(turniIds.size()).append("\n");
        
        if (tipo == TipoRaggruppamento.RICORRENTE) {
            info.append("Periodo: ").append(dataInizio).append(" - ").append(dataFine).append("\n");
            info.append("Pattern: ").append(pattern).append("\n");
            if (!eccezioni.isEmpty()) {
                info.append("Eccezioni: ").append(eccezioni.size()).append("\n");
            }
        }
        
        if (descrizione != null) {
            info.append("Descrizione: ").append(descrizione).append("\n");
        }
        
        return info.toString();
    }
    
    private void updateModificatoIl() {
        this.modificatoIl = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RaggruppamentoTurni that = (RaggruppamentoTurni) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("RaggruppamentoTurni{id='%s', nome='%s', tipo=%s, stato=%s, turni=%d}", 
                           id, nome, tipo, stato, turniIds.size());
    }
}