package com.utbm.loveletter.dto;

import java.util.List;

public class GameCreationRequest {
    private List<String> playerNames;

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }
}
