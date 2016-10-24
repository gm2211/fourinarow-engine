package com.theaigames.game.player;

public interface CliBot extends Runnable {
    String getResponse(long timeout);
    void writeToBot(String message);
    void finish();
    int getId();

    /**
     * Adds a string to the bot dump
     * @param dumpy : string to add to the dump
     */
    void addToDump(String dumpy);

    /**
     * @return : the dump of all the IO
     */
    String getDump();
}
