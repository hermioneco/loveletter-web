package com.utbm.loveletter.dto;

import com.utbm.loveletter.model.Carte;
import java.util.List;

public class GameStateDTO {
    private String gameId;
    private List<PlayerDTO> joueurs;
    private String activePlayerName;
    private List<Carte> activePlayerHand;
    private int deckCount;
    private List<String> defausse;
    private List<String> logs;
    private String tuteurStageEffectResult;
    private List<String> gagnantsManche;
    private List<String> gagnantsPartie;
    private boolean isPartieTerminee;
    private boolean isFinDeManche;

    // Getters and Setters

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public List<PlayerDTO> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(List<PlayerDTO> joueurs) {
        this.joueurs = joueurs;
    }

    public String getActivePlayerName() {
        return activePlayerName;
    }

    public void setActivePlayerName(String activePlayerName) {
        this.activePlayerName = activePlayerName;
    }

    public List<Carte> getActivePlayerHand() {
        return activePlayerHand;
    }

    public void setActivePlayerHand(List<Carte> activePlayerHand) {
        this.activePlayerHand = activePlayerHand;
    }

    public int getDeckCount() {
        return deckCount;
    }

    public void setDeckCount(int deckCount) {
        this.deckCount = deckCount;
    }

    public List<String> getDefausse() {
        return defausse;
    }

    public void setDefausse(List<String> defausse) {
        this.defausse = defausse;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public String getTuteurStageEffectResult() {
        return tuteurStageEffectResult;
    }

    public void setTuteurStageEffectResult(String tuteurStageEffectResult) {
        this.tuteurStageEffectResult = tuteurStageEffectResult;
    }

    public List<String> getGagnantsManche() {
        return gagnantsManche;
    }

    public void setGagnantsManche(List<String> gagnantsManche) {
        this.gagnantsManche = gagnantsManche;
    }

    public List<String> getGagnantsPartie() {
        return gagnantsPartie;
    }

    public void setGagnantsPartie(List<String> gagnantsPartie) {
        this.gagnantsPartie = gagnantsPartie;
    }

    public boolean isPartieTerminee() {
        return isPartieTerminee;
    }

    public void setPartieTerminee(boolean partieTerminee) {
        isPartieTerminee = partieTerminee;
    }

    public boolean isFinDeManche() {
        return isFinDeManche;
    }

    public void setFinDeManche(boolean finDeManche) {
        isFinDeManche = finDeManche;
    }
}
