package com.theaigames.game.player;

public interface Player {
    int getId();

    String getName();

    long getTimeBank();

    void setTimeBank(long time);

    void updateTimeBank(long timeElapsed);

    void sendSetting(String type, String value);

    default void sendSetting(String type, long value) {
        sendSetting(type, String.valueOf(value));
    }

    default void sendSetting(String type, int value) {
        sendSetting(type, (long) value);
    }

    void sendUpdate(String type, Player player, String value);

    void sendUpdate(String type, Player player, int value);

    void sendUpdate(String type, String value);

    void sendUpdate(String type, int value);

    String requestMove(String moveType);

    void run();

    void finish();

    void addToDump(String format);
}
