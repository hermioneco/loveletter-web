package com.utbm.loveletter.dto;

public class PlayCardRequest {
    private String cardNom;
    private String targetPlayerName;
    private String guessedCardNom; // type de carte deviné (ex: "Tuteur UTBM")

    public String getCardNom() {
        return cardNom;
    }

    public void setCardNom(String cardNom) {
        this.cardNom = cardNom;
    }

    public String getTargetPlayerName() {
        return targetPlayerName;
    }

    public void setTargetPlayerName(String targetPlayerName) {
        this.targetPlayerName = targetPlayerName;
    }

    public String getGuessedCardNom() {
        return guessedCardNom;
    }

    public void setGuessedCardNom(String guessedCardNom) {
        this.guessedCardNom = guessedCardNom;
    }
}
