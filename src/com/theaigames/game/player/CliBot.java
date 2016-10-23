package com.theaigames.game.player;

public interface CliBot {
    String getResponse(long timeout);
    void writeToBot(String message);
}
