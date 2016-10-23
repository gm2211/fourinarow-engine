package com.theaigames.bot;

import java.util.Optional;
import java.util.Scanner;
import java.util.function.Supplier;

public class BotCli {

    private static Optional<Bot> bot = Optional.empty();
    private static Board.Builder boardBuilder = Board.builder();
    private static Optional<Board> _board = Optional.empty();
    private static final Supplier<Board> board = () -> {
        if (!_board.isPresent()) {
            _board = Optional.of(boardBuilder.build());
        }

        return _board.get();
    };

    public static void main(String... args) {
        try(Scanner scan = new Scanner(System.in)) {

            while (scan.hasNextLine()) {
                String line = scan.nextLine();

                if (line.isEmpty()) {
                    continue;
                }

                processCommand(line);
            }
        }
    }

    private static Optional<String> processCommand(String line) {
        String[] parts = line.split(" ");

        switch (parts[0]) {
            case "settings":
                if (parts[1].equals("field_columns")) {
                    int columns = Integer.parseInt(parts[2]);
                    setColumns(boardBuilder, columns);
                }
                if (parts[1].equals("field_rows")) {
                    int rows = Integer.parseInt(parts[2]);
                    setRows(boardBuilder, rows);
                }
                if (parts[1].equals("your_botid")) {
                    createBot(parts[2]);
                }
                break;
            case "update":  /* new field data */
                if (parts[2].equals("field")) {
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
        bot = Optional.of(new Bot(Integer.parseInt(botId)));
    }

    public static void updateBoard(String boardData) {
        board.get().parseFromString(boardData);
    }

    public static Optional<String> move() {
        int column = bot.map(theBot -> theBot.selectColumn(board.get())).orElse(1);
        return Optional.of("place_disc " + column);
    }
}
