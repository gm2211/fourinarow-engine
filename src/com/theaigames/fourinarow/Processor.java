// Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.theaigames.fourinarow;

import com.theaigames.fourinarow.field.Field;
import com.theaigames.fourinarow.moves.Move;
import com.theaigames.fourinarow.moves.MoveResult;
import com.theaigames.game.GameHandler;
import com.theaigames.game.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Processor implements GameHandler {

    private static final Logger log = Logger.getLogger(Processor.class.getName());

    public static final String FIELD = "field";
    private static final String FIRST_TRY = " (first try)";
    private static final String SECOND_TRY = " (second try)";
    public static final String LAST_TRY = " (last try)";
    public static final String MOVE = "move";

    private int mRoundNumber = 1;
    private final List<Player> mPlayers;
    private final List<Move> mMoves;
    private final List<MoveResult> mMoveResults;
    private final Field board;
    private int mGameOverByPlayerErrorPlayerId = 0;

    public Processor(List<Player> players, Field field) {
        mPlayers = new ArrayList<>(players);
        board = field;
        mMoves = new ArrayList<>();
        mMoveResults = new ArrayList<>();

        /* Create first move with empty field */
        Move move = new Move(mPlayers.get(0));
        MoveResult moveResult = new MoveResult(mPlayers.get(0), board, mPlayers.get(0).getId());
        mMoves.add(move);
        mMoveResults.add(moveResult);
    }


    @Override
    public void playRound(int roundNumber) {
        for (Player player : mPlayers) {
            player.sendUpdate("round", mRoundNumber);
            player.sendUpdate(FIELD, board.toString());
            if (getWinner() == null) {


                List<String> retryMessages = Arrays.asList("", FIRST_TRY, SECOND_TRY, LAST_TRY);
                AtomicInteger attemptCount = new AtomicInteger(0);

                boolean unsuccessful;

                do {
                    final String response = player.requestMove(MOVE);
                    log.info(() -> String.format("Player %s played: %s", player.getName(), response));

                    unsuccessful = !parseResponse(response, player);

                    if (unsuccessful) {
                        log.info(() -> String.format("Invalid move: %s", board.getLastError()));

                        if (attemptCount.get() == 0) { attemptCount.getAndIncrement(); }
                    }

                    // Select FIRST_TRY instead of empty string if not successful on first attempt

                    persistMoves(player, retryMessages.get(attemptCount.getAndIncrement()));
                    player.sendUpdate(FIELD, board.toString());
                } while (unsuccessful && (attemptCount.get() < retryMessages.size()));

                if (unsuccessful) { mGameOverByPlayerErrorPlayerId = player.getId(); }

                mRoundNumber++;
            }
        }
    }

    private void persistMoves(Player player, String attemptCountMessage) {
        if (!attemptCountMessage.isEmpty()) {
            log.info(() -> String.format("Attempt #: %s", attemptCountMessage));
        }

        Move move = new Move(player);

        move.setColumn(board.getLastColumn());
        move.setIllegalMove(board.getLastError() + attemptCountMessage);
        mMoves.add(move);
        MoveResult moveResult = new MoveResult(player, board, player.getId());
        moveResult.setColumn(board.getLastColumn());
        moveResult.setIllegalMove(board.getLastError() + attemptCountMessage);
        mMoveResults.add(moveResult);

        log.info(() -> String.format("Board now looks like: %s", board.prettyString()));
    }

    private void move(Player player) {
        Move move;
        MoveResult moveResult;
        move = new Move(player);
        moveResult = new MoveResult(player, board, player.getId());
        move.setColumn(board.getLastColumn());
        mMoves.add(move);
        moveResult.setColumn(board.getLastColumn());
        mMoveResults.add(moveResult);
    }

    /**
     * Parses player response and inserts disc in field
     *
     * @return : true if valid move, otherwise false
     */
    private Boolean parseResponse(String r, Player player) {
        String[] parts = r.split(" ");
        if ((parts.length >= 2) && parts[0].equals("place_disc")) {
            int column = Integer.parseInt(parts[1]);
            if (board.addDisc(column, player.getId())) {
                return true;
            }
        }
        board.mLastError = "Unknown command";
        return false;
    }

    @Override
    public int getRoundNumber() {
        return this.mRoundNumber;
    }

    @Override
    public Player getWinner() {
        int winner = board.getWinner();
        if (mGameOverByPlayerErrorPlayerId > 0) { /* Game over due to too many player errors. Look up the other player, which became the winner */
            for (Player player : mPlayers) {
                if (player.getId() != mGameOverByPlayerErrorPlayerId) {
                    return player;
                }
            }
        }
        if (winner != 0) {
            for (Player player : mPlayers) {
                if (player.getId() == winner) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public String getPlayedGame() {
        return "";
    }

    /**
     * Returns a List of Moves played in this game
     *
     * @return : List with Move objects
     */
    public List<Move> getMoves() {
        return mMoves;
    }

    public Field getField() {
        return board;
    }

    @Override
    public boolean isGameOver() {
        return (getWinner() != null || board.isFull());
    }
}
