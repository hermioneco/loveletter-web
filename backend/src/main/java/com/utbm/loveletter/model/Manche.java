package com.utbm.loveletter.model;

import java.util.ArrayList;
import java.util.List;

public class Manche {

    private Paquet paquet;
    private List<Etudiant> joueurs;
    private int indexCourant;
    private Class<? extends Carte> carteDevinee;
    private List<Etudiant> gagnants;
    
    // Web app state expansions
    private List<String> logs;
    private List<String> defausse;
    private String tuteurStageEffectResult;

    public Manche(List<Etudiant> joueurs) {
        this.joueurs = joueurs;
        this.paquet = new Paquet();
        this.indexCourant = 0;
        this.gagnants = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.defausse = new ArrayList<>();
        this.tuteurStageEffectResult = null;
    }

    public void initialiserManche() {
        paquet.initialiserPaquet();
        paquet.melanger();

        if (joueurs.size() == 2) paquet.brulerTroisCartesVisibles();
        paquet.cacherUneCarte();

        // Réinitialiser l'état des joueurs
        for (Etudiant e : joueurs) {
            e.resetPourNouvelleManche();
        }

        // Distribuer 1 carte par joueur
        for (Etudiant e : joueurs) {
            e.recevoirCarte(paquet.piocher());
        }
        
        logs.add("La manche a commencé !");
    }

    public Etudiant getJoueurCourant() {
        return joueurs.get(indexCourant);
    }

    public List<Etudiant> getJoueurs() {
        return joueurs;
    }

    public Paquet getPaquet() {
        return paquet;
    }

    public Carte piocherCarte() {
        Etudiant joueur = getJoueurCourant();
        joueur.desactiverProtection();
        Carte c = paquet.piocher();
        return c;
    }

    public void definirCarteDevinee(Class<? extends Carte> carte) {
        this.carteDevinee = carte;
    }

    public Class<? extends Carte> getCarteDevinee() {
        return carteDevinee;
    }

    public void jouerCarte(Carte carteJouee, Etudiant cible) {
        Etudiant joueur = getJoueurCourant();

        // Règle Réinscription
        if (joueur.possedeCarte(ReinscriptionAdministrative.class)) {
            if ((joueur.possedeCarte(DirecteurDepartement.class)
                    || joueur.possedeCarte(ChargeAffairePedagogique.class))
                    && !(carteJouee instanceof ReinscriptionAdministrative)) {
                throw new IllegalStateException("Réinscription obligatoire");
            }
        }

        joueur.retirerCarte(carteJouee);
        addDiscard(joueur.getNom(), carteJouee);
        
        // Appliquer effet
        carteJouee.appliquerEffet(this, joueur, cible);

        if (carteJouee instanceof ValidationFinale) {
            joueur.eliminer();
        }
    }

    public void passerAuSuivant() {
        // Nettoyer les effets temporaires comme le résultat du Tuteur UTBM
        this.tuteurStageEffectResult = null;
        
        int debut = indexCourant;
        do {
            indexCourant = (indexCourant + 1) % joueurs.size();
        } while (joueurs.get(indexCourant).estElimine() && indexCourant != debut);
    }

    public boolean estFinDeManche() {
        long actifs = joueurs.stream().filter(j -> !j.estElimine()).count();
        return actifs <= 1 || paquet.estVide();
    }

    public void determinerGagnants() {
        gagnants.clear();

        // joueurs encore en lice
        List<Etudiant> actifs = joueurs.stream()
                .filter(j -> !j.estElimine())
                .toList();

        if (actifs.isEmpty()) return;

        // valeur maximale parmi les cartes restantes
        int valeurMax = actifs.stream()
                .mapToInt(j -> j.getCarteEnMain() != null ? j.getCarteEnMain().getValeur() : 0)
                .max()
                .orElse(0);

        // tous les joueurs ayant cette valeur gagnent la manche
        for (Etudiant j : actifs) {
            if (j.getCarteEnMain() != null && j.getCarteEnMain().getValeur() == valeurMax) {
                gagnants.add(j);
            }
        }
        
        StringBuilder winMsg = new StringBuilder("Fin de la manche ! Gagnant(s) : ");
        for (Etudiant w : gagnants) {
            winMsg.append(w.getNom()).append(" ");
        }
        logs.add(winMsg.toString());
    }

    public List<Etudiant> getGagnants() {
        return gagnants;
    }

    public void distribuerPions() {
        if (gagnants.isEmpty()) {
            determinerGagnants();
        }

        for (Etudiant e : gagnants) {
            e.ajouterPion();
        }
    }

    // --- Web additions ---
    
    public void addLog(String message) {
        logs.add(message);
    }

    public List<String> getLogs() {
        return logs;
    }

    public void addDiscard(String playerName, Carte carte) {
        defausse.add(playerName + " a joué " + carte.getNom() + " (" + carte.getValeur() + ")");
    }

    public List<String> getDefausse() {
        return defausse;
    }

    public String getTuteurStageEffectResult() {
        return tuteurStageEffectResult;
    }

    public void setTuteurStageEffectResult(String tuteurStageEffectResult) {
        this.tuteurStageEffectResult = tuteurStageEffectResult;
    }
}
