package com.theaigames.bot;

import com.theaigames.fourinarow.FourInARow;
import com.theaigames.fourinarow.Processor;
import com.theaigames.game.player.CliBot;

import java.util.Optional;
import java.util.Scanner;
import java.util.function.Supplier;

public class MyBotCli implements CliBot {

    private static Optional<MyBot> bot = Optional.empty();
    private static final Board.Builder boardBuilder = Board.builder();
    private static Optional<Board> _board = Optional.empty();
    private static final Supplier<Board> board = () -> {
        if (!_board.isPresent()) {
            _board = Optional.of(boardBuilder.build());
        }

        return _board.get();
    };

    private final StringBuilder dump = new StringBuilder();

    public static void main(String... args) {
        try(Scanner scan = new Scanner(System.in)) {

            while (scan.hasNextLine()) {
                String line = scan.nextLine();

                if (!line.isEmpty()) {
                    processCommand(line);
                }
            }
        }
    }

    private static Optional<String> processCommand(String line) {
        String[] parts = line.split(" ");

        switch (parts[0]) {
            case "settings":
                if (parts[1].equals(FourInARow.FIELD_COLUMNS_SETTING_NAME)) {
                    int columns = Integer.parseInt(parts[2]);
                    setColumns(boardBuilder, columns);
                }
                if (parts[1].equals(FourInARow.FIELD_ROWS_SETTING_NAME)) {
                    int rows = Integer.parseInt(parts[2]);
                    setRows(boardBuilder, rows);
                }
                if (parts[1].equals(FourInARow.YOUR_BOTID)) {
                    createBot(parts[2]);
                }
                break;
            case "update":  /* new field data */
                if (parts[2].equals(Processor.FIELD)) {
                    updateBoard(parts[3]);
                }
                break;
            case "action":
                if (parts[1].equals("move")) {
                    return move();
                }
                break;
            default:
                return Optional.of("unknown command");
        }

        return Optional.empty();
    }

    private static void setRows(Board.Builder boardBuilder, int rows) {
        boardBuilder.setRows(rows);
    }

    private static void setColumns(Board.Builder boardBuilder, int columns) {
        boardBuilder.setColumns(columns);
    }

    private static void createBot(String botId) {
        bot = Optional.of(new MyBot(Integer.parseInt(botId)));
    }

    private static void updateBoard(String boardData) {
        board.get().parseFromString(boardData);
    }

    private static Optional<String> move() {
        int column = bot.map(theBot -> theBot.selectColumn(board.get())).orElse(1);
        return Optional.of("place_disc " + column);
    }

    @Override
    public String getResponse(long timeout) {
        return move().orElseThrow(() -> new IllegalStateException("Cannot move"));
    }

    @Override
    public void writeToBot(String message) {
        processCommand(message);
    }

    @Override
    public void finish() {

    }

    @Override
    public int getId() {
        return bot.map(MyBot::botId).orElse(0);
    }

    @Override
    public void addToDump(String dumpy) {
        dump.append(dumpy).append(System.lineSeparator());
    }

    @Override
    public String getDump() {
        return dump.toString();
    }

    @Override
    public void run() {

    }
}
