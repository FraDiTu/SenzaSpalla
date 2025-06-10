package it.catering.catring.model.entities;

public class PersonaleServizio extends User {
    private String ruoloSpecifico;
    private String competenze;
    
    public PersonaleServizio() {
        super();
    }
    
    public PersonaleServizio(String username, String password, String nome, String cognome, 
                           String email, String ruoloSpecifico) {
        super(username, password, nome, cognome, email);
        this.ruoloSpecifico = ruoloSpecifico;
    }
    
    public String getRuoloSpecifico() { return ruoloSpecifico; }
    public void setRuoloSpecifico(String ruoloSpecifico) { this.ruoloSpecifico = ruoloSpecifico; }
    public String getCompetenze() { return competenze; }
    public void setCompetenze(String competenze) { this.competenze = competenze; }
    
    @Override
    public String toString() {
        return "Personale Servizio: " + getNomeCompleto() + " (" + ruoloSpecifico + ")";
    }
}