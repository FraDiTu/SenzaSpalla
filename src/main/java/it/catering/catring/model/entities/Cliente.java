package it.catering.catring.model.entities;

import java.util.Objects;

public class Cliente {
    private Long id;
    private String nome;
    private String tipo; // privato/azienda
    private String contatti;
    private String email;
    private String telefono;
    private String indirizzo;
    
    public Cliente() {
        this.id = System.currentTimeMillis() + (long) (Math.random() * 1000);
    }
    
    public Cliente(String nome, String tipo, String contatti) {
        this();
        this.nome = nome;
        this.tipo = tipo;
        this.contatti = contatti;
    }
    
    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getContatti() { return contatti; }
    public void setContatti(String contatti) { this.contatti = contatti; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    
    @Override
    public String toString() {
        return nome + " (" + tipo + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}