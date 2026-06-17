package com.utbm.loveletter.model;

import java.util.ArrayList;
import java.util.List;

public class Jeu {

    private List<Etudiant> joueurs;
    private Manche mancheCourante;
    private int pionsPourGagner;

    public Jeu(List<String> noms) {
        joueurs = new ArrayList<>();
        for (String n : noms) {
            joueurs.add(new Etudiant(n));
        }

        // règle Love Letter classique
        this.pionsPourGagner = determinerSeuilVictoire(noms.size());
    }

    /* =======================
       Gestion des manches
       ======================= */

    public void lancerNouvelleManche() {
        mancheCourante = new Manche(joueurs);
        mancheCourante.initialiserManche();
    }

    public void terminerManche() {
        if (mancheCourante == null || !mancheCourante.estFinDeManche()) return;

        mancheCourante.determinerGagnants();
        mancheCourante.distribuerPions();
    }

    public Manche getMancheCourante() {
        return mancheCourante;
    }

    /* =======================
       Fin de partie globale
       ======================= */

    public boolean estPartieTerminee() {
        for (Etudiant e : joueurs) {
            if (e.getNombrePions() >= pionsPourGagner) {
                return true;
            }
        }
        return false;
    }

    public List<Etudiant> getGagnantsPartie() {
        List<Etudiant> gagnants = new ArrayList<>();
        for (Etudiant e : joueurs) {
            if (e.getNombrePions() >= pionsPourGagner) {
                gagnants.add(e);
            }
        }
        return gagnants;
    }

    /* =======================
       Accesseurs utiles UI
       ======================= */

    public List<Etudiant> getJoueurs() {
        return joueurs;
    }

    public int getPionsPourGagner() {
        return pionsPourGagner;
    }

    /* =======================
       Règles métier
       ======================= */

    private int determinerSeuilVictoire(int nbJoueurs) {
        if (nbJoueurs == 2) return 7;
        if (nbJoueurs == 3) return 5;
        if (nbJoueurs == 4) return 4;
        return 3;
    }
}
