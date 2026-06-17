package com.utbm.loveletter.model;

public class ValidationFinale extends Carte {

    public ValidationFinale() {
        super(8, "Validation finale");
        this.necessiteCible = false;
    }

    @Override
    public void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible) {
        joueur.eliminer();
        manche.addLog(joueur.getNom() + " a défaussé Validation finale et est éliminé !");
    }
}
