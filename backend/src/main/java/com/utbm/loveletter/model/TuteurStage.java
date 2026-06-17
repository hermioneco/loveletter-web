package com.utbm.loveletter.model;

public class TuteurStage extends Carte {

    public TuteurStage() {
        super(2, "Tuteur UTBM");
        this.necessiteCible = true;
    }

    @Override
    public void appliquerEffet(Manche manche, Etudiant joueur, Etudiant cible) {
        if (cible != null && !cible.estProtege()) {
            String cardName = cible.getCarteEnMain() != null ? cible.getCarteEnMain().getNom() : "Aucune";
            manche.setTuteurStageEffectResult("La carte de " + cible.getNom() + " est : " + cardName);
        }
    }
}
