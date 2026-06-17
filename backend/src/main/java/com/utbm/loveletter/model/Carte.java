package com.utbm.loveletter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
public abstract class Carte {

    protected int valeur;
    protected String nom;
    protected boolean necessiteCible;

    public Carte(int valeur, String nom) {
        this.valeur = valeur;
        this.nom = nom;
        this.necessiteCible = false;
    }

    public int getValeur() {
        return valeur;
    }

    public String getNom() {
        return nom;
    }

    public boolean isNecessiteCible() {
        return necessiteCible;
    }

    public void setNecessiteCible(boolean necessiteCible) {
        this.necessiteCible = necessiteCible;
    }

    /**
     * Applique l'effet de la carte
     */
    @JsonIgnore
    public abstract void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible);

    @Override
    public String toString() {
        return nom + " (" + valeur + ")";
    }
}
