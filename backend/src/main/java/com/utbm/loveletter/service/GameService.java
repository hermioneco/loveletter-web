package com.utbm.loveletter.service;

import com.utbm.loveletter.dto.GameStateDTO;
import com.utbm.loveletter.dto.PlayerDTO;
import com.utbm.loveletter.dto.PlayCardRequest;
import com.utbm.loveletter.model.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, Jeu> activeGames = new ConcurrentHashMap<>();

    public GameStateDTO createGame(List<String> playerNames) {
        String gameId = UUID.randomUUID().toString();
        Jeu jeu = new Jeu(playerNames);
        jeu.lancerNouvelleManche();
        activeGames.put(gameId, jeu);
        return mapToDTO(gameId, jeu);
    }

    public GameStateDTO getGameState(String gameId) {
        Jeu jeu = activeGames.get(gameId);
        if (jeu == null) {
            throw new IllegalArgumentException("Partie introuvable : " + gameId);
        }
        return mapToDTO(gameId, jeu);
    }

    public GameStateDTO drawCard(String gameId) {
        Jeu jeu = activeGames.get(gameId);
        if (jeu == null) {
            throw new IllegalArgumentException("Partie introuvable : " + gameId);
        }

        Manche manche = jeu.getMancheCourante();
        if (manche == null || manche.estFinDeManche()) {
            throw new IllegalStateException("Impossible de piocher : la manche est terminée.");
        }

        Etudiant joueur = manche.getJoueurCourant();
        if (joueur.getMain().size() >= 2) {
            throw new IllegalStateException("Le joueur a déjà pioché.");
        }

        Carte piochee = manche.piocherCarte();
        joueur.recevoirCarte(piochee);
        manche.addLog(joueur.getNom() + " a pioché une carte.");

        return mapToDTO(gameId, jeu);
    }

    public GameStateDTO playCard(String gameId, PlayCardRequest request) {
        Jeu jeu = activeGames.get(gameId);
        if (jeu == null) {
            throw new IllegalArgumentException("Partie introuvable : " + gameId);
        }

        Manche manche = jeu.getMancheCourante();
        if (manche == null || manche.estFinDeManche()) {
            throw new IllegalStateException("La manche est terminée.");
        }

        Etudiant joueur = manche.getJoueurCourant();
        
        // Trouver la carte dans la main
        Carte carteJouee = null;
        for (Carte c : joueur.getMain()) {
            if (c.getNom().equalsIgnoreCase(request.getCardNom())) {
                carteJouee = c;
                break;
            }
        }

        if (carteJouee == null) {
            throw new IllegalArgumentException("Le joueur ne possède pas cette carte : " + request.getCardNom());
        }

        // Trouver la cible
        Etudiant cible = null;
        if (carteJouee.isNecessiteCible() && request.getTargetPlayerName() != null) {
            for (Etudiant e : manche.getJoueurs()) {
                if (e.getNom().equalsIgnoreCase(request.getTargetPlayerName())) {
                    cible = e;
                    break;
                }
            }
        }

        // Configurer la devinette si c'est un Contrôleur administratif
        if (carteJouee instanceof ControleAdministrative && request.getGuessedCardNom() != null) {
            Class<? extends Carte> guessedClass = getCardClassByName(request.getGuessedCardNom());
            manche.definirCarteDevinee(guessedClass);
        }

        // Jouer la carte
        try {
            manche.jouerCarte(carteJouee, cible);
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }

        // Vérifier si la manche se termine
        if (manche.estFinDeManche()) {
            jeu.terminerManche();
        }

        return mapToDTO(gameId, jeu);
    }

    public GameStateDTO nextTurn(String gameId) {
        Jeu jeu = activeGames.get(gameId);
        if (jeu == null) {
            throw new IllegalArgumentException("Partie introuvable : " + gameId);
        }

        Manche manche = jeu.getMancheCourante();
        if (manche == null) {
            throw new IllegalStateException("Manche introuvable.");
        }

        if (manche.estFinDeManche()) {
            throw new IllegalStateException("La manche est finie. Veuillez en lancer une nouvelle.");
        }

        manche.passerAuSuivant();
        
        // Journaliser le début du tour
        Etudiant suivant = manche.getJoueurCourant();
        manche.addLog("C'est au tour de " + suivant.getNom() + ".");

        return mapToDTO(gameId, jeu);
    }

    public GameStateDTO startNewRound(String gameId) {
        Jeu jeu = activeGames.get(gameId);
        if (jeu == null) {
            throw new IllegalArgumentException("Partie introuvable : " + gameId);
        }

        if (jeu.estPartieTerminee()) {
            throw new IllegalStateException("La partie est terminée !");
        }

        jeu.lancerNouvelleManche();
        return mapToDTO(gameId, jeu);
    }

    /* =================================
       Helpers de Mapping DTO
       ================================= */

    private GameStateDTO mapToDTO(String gameId, Jeu jeu) {
        GameStateDTO dto = new GameStateDTO();
        dto.setGameId(gameId);
        dto.setPartieTerminee(jeu.estPartieTerminee());

        List<PlayerDTO> joueursDTO = new ArrayList<>();
        for (Etudiant e : jeu.getJoueurs()) {
            joueursDTO.add(new PlayerDTO(
                e.getNom(),
                e.estElimine(),
                e.estProtege(),
                e.getNombrePions(),
                e.getMain().size()
            ));
        }
        dto.setJoueurs(joueursDTO);

        Manche manche = jeu.getMancheCourante();
        if (manche != null) {
            dto.setFinDeManche(manche.estFinDeManche());
            dto.setDeckCount(manche.getPaquet().size());
            dto.setDefausse(manche.getDefausse());
            dto.setLogs(manche.getLogs());
            dto.setTuteurStageEffectResult(manche.getTuteurStageEffectResult());
            
            Etudiant joueurCourant = manche.getJoueurCourant();
            dto.setActivePlayerName(joueurCourant.getNom());
            dto.setActivePlayerHand(joueurCourant.getMain());

            if (manche.estFinDeManche() && manche.getGagnants() != null) {
                List<String> winList = new ArrayList<>();
                for (Etudiant g : manche.getGagnants()) {
                    winList.add(g.getNom());
                }
                dto.setGagnantsManche(winList);
            }
        }

        if (jeu.estPartieTerminee()) {
            List<String> winPartie = new ArrayList<>();
            for (Etudiant gp : jeu.getGagnantsPartie()) {
                winPartie.add(gp.getNom());
            }
            dto.setGagnantsPartie(winPartie);
        }

        return dto;
    }

    private Class<? extends Carte> getCardClassByName(String nom) {
        if (nom == null) return null;
        switch (nom) {
            case "Contrôleur administratif":
            case "ControleAdministrative":
                return ControleAdministrative.class;
            case "Tuteur UTBM":
            case "TuteurStage":
                return TuteurStage.class;
            case "Jury UTBM":
            case "JuryEvaluation":
                return JuryEvaluation.class;
            case "Secrétariat UTBM":
            case "ServiceScolarite":
                return ServiceScolarite.class;
            case "Chargé d’Affaires Pédagogiques":
            case "ChargeAffairePedagogique":
                return ChargeAffairePedagogique.class;
            case "Directeur de département":
            case "DirecteurDepartement":
                return DirecteurDepartement.class;
            case "Réinscription administrative":
            case "ReinscriptionAdministrative":
                return ReinscriptionAdministrative.class;
            case "Validation finale":
            case "ValidationFinale":
                return ValidationFinale.class;
            default:
                return null;
        }
    }
}
