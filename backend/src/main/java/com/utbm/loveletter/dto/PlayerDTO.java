package com.utbm.loveletter.dto;

public class PlayerDTO {
    private String nom;
    private boolean elimine;
    private boolean protege;
    private int nombrePions;
    private int mainSize;

    public PlayerDTO(String nom, boolean elimine, boolean protege, int nombrePions, int mainSize) {
        this.nom = nom;
        this.elimine = elimine;
        this.protege = protege;
        this.nombrePions = nombrePions;
        this.mainSize = mainSize;
    }

    public String getNom() {
        return nom;
    }

    public boolean isElimine() {
        return elimine;
    }

    public boolean isProtege() {
        return protege;
    }

    public int getNombrePions() {
        return nombrePions;
    }

    public int getMainSize() {
        return mainSize;
    }
}
