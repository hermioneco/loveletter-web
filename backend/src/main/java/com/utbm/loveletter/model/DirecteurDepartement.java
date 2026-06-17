package com.utbm.loveletter.model;

public class DirecteurDepartement extends Carte {

    public DirecteurDepartement() {
        super(6, "Directeur de département");
        this.necessiteCible = true;
    }

    @Override
    public void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible) {
        if (cible == null || cible.estProtege() || cible.estElimine()) return;

        Carte carteJoueur = joueur.getCarteEnMain();
        Carte carteCible = cible.getCarteEnMain();

        if (carteJoueur == null || carteCible == null) return;

        joueur.retirerCarte(carteJoueur);
        cible.retirerCarte(carteCible);

        joueur.recevoirCarte(carteCible);
        cible.recevoirCarte(carteJoueur);

        manche.addLog(joueur.getNom() + " a échangé sa main avec " + cible.getNom() + ".");
    }
}
