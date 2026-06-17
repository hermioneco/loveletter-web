package com.utbm.loveletter.model;

public class ChargeAffairePedagogique extends Carte {

    public ChargeAffairePedagogique() {
        super(5, "Chargé d’Affaires Pédagogiques");
        this.necessiteCible = true;
    }

    @Override
    public void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible) {
        if (cible == null || cible.estProtege() || cible.estElimine()) return;

        Carte carteDefaussee = cible.getCarteEnMain();
        if (carteDefaussee != null) {
            cible.retirerCarte(carteDefaussee);
            manche.addDiscard(cible.getNom(), carteDefaussee);
            
            if (carteDefaussee instanceof ValidationFinale) {
                cible.eliminer();
                manche.addLog(cible.getNom() + " a été forcé de défausser Validation finale et est éliminé !");
            } else {
                manche.addLog(cible.getNom() + " défausse " + carteDefaussee.getNom() + " et pioche une nouvelle carte.");
                Carte nouvelle = manche.getPaquet().piocher();
                if (nouvelle != null) {
                    cible.recevoirCarte(nouvelle);
                }
            }
        }
    }
}
