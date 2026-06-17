package com.utbm.loveletter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Paquet {

    private List<Carte> cartes;
    private List<Carte> cartesBrulees;
    private Carte CarteCachee;

    public Paquet() {
        cartes = new ArrayList<>();
        cartesBrulees = new ArrayList<>();
    }

    public void initialiserPaquet() {
        cartes.clear();
        cartesBrulees.clear();
        
        for (int i = 0; i < 5; i++) cartes.add(new ControleAdministrative());
        cartes.add(new TuteurStage());
        cartes.add(new TuteurStage());
        cartes.add(new JuryEvaluation());
        cartes.add(new JuryEvaluation());
        cartes.add(new ServiceScolarite());
        cartes.add(new ServiceScolarite());
        cartes.add(new ChargeAffairePedagogique());
        cartes.add(new ChargeAffairePedagogique());
        cartes.add(new DirecteurDepartement());
        cartes.add(new ReinscriptionAdministrative());
        cartes.add(new ValidationFinale());
    }

    public void melanger() {
        Collections.shuffle(cartes);
    }

    public Carte piocher() {
        return cartes.isEmpty() ? CarteCachee : cartes.remove(0);
    }

    public void cacherUneCarte() {
        if (!cartes.isEmpty()) {
            CarteCachee = cartes.remove(0);
        }
    }

    public void brulerTroisCartesVisibles() {
        for (int i = 0; i < 3; i++) {
            if (!cartes.isEmpty()) {
                cartesBrulees.add(cartes.remove(0));
            }
        }
    }

    public boolean estVide() {
        return cartes.isEmpty();
    }

    public List<Carte> getCartes() {
        return cartes;
    }

    public List<Carte> getCartesBrulees() {
        return cartesBrulees;
    }

    public int size() {
        return cartes.size();
    }
}
