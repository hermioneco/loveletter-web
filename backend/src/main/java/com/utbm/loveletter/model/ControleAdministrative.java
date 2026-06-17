package com.utbm.loveletter.model;

public class ControleAdministrative extends Carte {

    public ControleAdministrative() {
        super(1, "Contrôleur administratif");
        this.necessiteCible = true;
    }

    @Override
    public void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible) {
        if (cible == null || cible.estProtege() || cible.estElimine()) return;

        Class<? extends Carte> devinee = manche.getCarteDevinee();
        if (devinee == null || devinee.equals(ControleAdministrative.class)) return;

        for (Carte c : cible.getMain()) {
            if (c.getClass().equals(devinee)) {
                cible.eliminer();
                return;
            }
        }
    }
}
