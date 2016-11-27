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

import com.theaigames.mybot.MyBotCli;
import com.theaigames.fourinarow.field.Field;
import com.theaigames.game.AbstractGame;
import com.theaigames.game.player.BotPlayer;
import com.theaigames.game.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FourInARow extends AbstractGame {

    private static final long TIMEBANK_MAX = 10000L;
    private static final long TIME_PER_MOVE = 500L;
    private static final int FIELD_COLUMNS = 7;
    private static final int FIELD_ROWS = 6;

    public static final String FIELD_ROWS_SETTING_NAME = "field_rows";
    public static final String FIELD_COLUMNS_SETTING_NAME = "field_columns";
    public static final String YOUR_BOTID = "your_botid";
    private List<Player> players;

    @Override
    public void setupGame(Collection<? extends Player> players) throws Exception {
        // create all the players and everything they need
        this.players = new ArrayList<>();

        // create the playing field
        Field mField = new Field(FIELD_COLUMNS, FIELD_ROWS);

        this.players = new ArrayList<>(players);
        this.players.forEach(this::sendSettings);

        // create the processor
        processor = new Processor(this.players, mField);
    }

    @Override
    public void sendSettings(Player player) {
        player.sendSetting("timebank", TIMEBANK_MAX);
        player.sendSetting("time_per_move", TIME_PER_MOVE);
        player.sendSetting("player_names", this.players.get(0).getName() + "," + this.players.get(1).getName());
        player.sendSetting("your_bot", player.getName());
        player.sendSetting(YOUR_BOTID, player.getId()); /* this is fundamentally screwed up, but whatever */
        player.sendSetting(FIELD_COLUMNS_SETTING_NAME, FIELD_COLUMNS);
        player.sendSetting(FIELD_ROWS_SETTING_NAME, FIELD_ROWS);
    }

    @Override
    protected long getTimeBankMax() {
        return TIMEBANK_MAX;
    }

    @Override
    protected long getTimePerMove() {
        return TIME_PER_MOVE;
    }

    @Override
    protected void runEngine() throws Exception {
        engine.setLogic(this);
        engine.start();
    }

    // DEV_MODE can be turned on to easily test the
    // engine from eclipse
    public static void main(String args[]) throws Exception {
        FourInARow game = new FourInARow();

//        // DEV_MODE settings
//        game.TEST_BOT = "java -cp /home/jim/workspace/fourinarow-starterbot-java/bin/ bot.BotStarter";
//        game.NUM_TEST_BOTS = 2;
//        game.DEV_MODE = true;
//
//        game.setupEngine(args);
        BotPlayer player1 = new BotPlayer(1, new MyBotCli(), TIMEBANK_MAX, TIME_PER_MOVE);
        BotPlayer player2 = new BotPlayer(2, new MyBotCli(), TIMEBANK_MAX, TIME_PER_MOVE);

        game.setupGame(Arrays.asList(player1, player2));
        game.setupEngine(game.players);
        game.runEngine();
    }
}
