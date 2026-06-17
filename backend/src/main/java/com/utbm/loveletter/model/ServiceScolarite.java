package com.utbm.loveletter.model;

public class ServiceScolarite extends Carte {

    public ServiceScolarite() {
        super(4, "Secrétariat UTBM");
        this.necessiteCible = false;
    }

    @Override
    public void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible) {
        joueur.activerProtection();
        manche.addLog(joueur.getNom() + " a joué Secrétariat UTBM et est protégé jusqu'à son prochain tour.");
    }
}
