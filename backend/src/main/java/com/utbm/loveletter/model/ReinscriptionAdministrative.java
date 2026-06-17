package com.utbm.loveletter.model;

public class ReinscriptionAdministrative extends Carte {

    public ReinscriptionAdministrative() {
        super(7, "Réinscription administrative");
        this.necessiteCible = false;
    }

    @Override
    public void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible) {
        manche.addLog(joueur.getNom() + " a joué Réinscription administrative (pas d'effet direct).");
    }
}
