package com.utbm.loveletter.model;

public class JuryEvaluation extends Carte {

    public JuryEvaluation() {
        super(3, "Jury UTBM");
        this.necessiteCible = true;
    }

    @Override
    public void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible) {
        if (cible == null || cible.estProtege() || cible.estElimine()) return;

        Carte carteJoueur = joueur.getCarteEnMain();
        Carte carteCible = cible.getCarteEnMain();

        if (carteJoueur == null || carteCible == null) return;

        if (carteJoueur.getValeur() > carteCible.getValeur()) {
            cible.eliminer();
            manche.addLog(joueur.getNom() + " a comparé sa main avec " + cible.getNom() + ". " + cible.getNom() + " est éliminé !");
        } else if (carteJoueur.getValeur() < carteCible.getValeur()) {
            joueur.eliminer();
            manche.addLog(joueur.getNom() + " a comparé sa main avec " + cible.getNom() + ". " + joueur.getNom() + " est éliminé !");
        } else {
            manche.addLog(joueur.getNom() + " a comparé sa main avec " + cible.getNom() + ". Égalité !");
        }
    }
}
