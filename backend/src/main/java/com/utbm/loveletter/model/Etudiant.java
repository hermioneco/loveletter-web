package com.utbm.loveletter.model;

import java.util.ArrayList;
import java.util.List;

public class Etudiant {

    private String nom;
    private List<Carte> main;
    private boolean elimine;
    private boolean protege;
    private int NombrePions;

    public Etudiant(String nom) {
        this.nom = nom;
        this.main = new ArrayList<>();
        this.elimine = false;
        this.protege = false;
        this.NombrePions = 0;
    }

    public String getNom() {
        return nom;
    }

    public void resetPourNouvelleManche() {
        this.elimine = false;
        this.protege = false;
        this.main.clear();
    }

    /* ===== Cartes ===== */

    public void recevoirCarte(Carte carte) {
        main.add(carte);
    }

    public void retirerCarte(Carte carte) {
        main.remove(carte);
    }

    public Carte jouerCarte(int index) {
        return main.remove(index);
    }

    public List<Carte> getMain() {
        return new ArrayList<>(main);
    }

    public Carte getCarteEnMain() {
        return main.isEmpty() ? null : main.get(0);
    }

    public boolean possedeCarte(Class<? extends Carte> type) {
        return main.stream().anyMatch(c -> c.getClass().equals(type));
    }

    /* ===== Etat ===== */

    public boolean estElimine() {
        return elimine;
    }

    public void eliminer() {
        elimine = true;
        main.clear();
    }

    public boolean estProtege() {
        return protege;
    }

    public void activerProtection() {
        protege = true;
    }

    public void desactiverProtection() {
        protege = false;
    }

    public void ajouterPion() {
        NombrePions += 1;
    }

    public int getNombrePions() {
        return NombrePions;
    }
}
