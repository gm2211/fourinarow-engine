// Copyright 2015 theaigames.com (developers@theaigames.com)

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

import java.util.Optional;
import java.util.Scanner;

public class BotCli {

    public static void main(String... args) {
        Scanner scan = new Scanner(System.in);
        Board board = new Board(0, 0);
        Optional<Bot> bot = Optional.empty();

        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(" ");

            switch (parts[0]) {
                case "settings":
                    if (parts[1].equals("field_columns")) {
                        board.setColumns(Integer.parseInt(parts[2]));
                    }
                    if (parts[1].equals("field_rows")) {
                        board.setRows(Integer.parseInt(parts[2]));
                    }
                    if (parts[1].equals("your_botid")) {
                        int mBotId = Integer.parseInt(parts[2]);
                        bot = Optional.of(new Bot(mBotId));
                    }
                    break;
                case "update":  /* new field data */
                    if (parts[2].equals("field")) {
                        String data = parts[3];
                        board.parseFromString(data); /* Parse Board with data */
                    }
                    break;
                case "action":
                    if (parts[1].equals("move")) { /* move requested */
                        int column = bot.map(theBot -> theBot.selectColumn(board)).orElse(1);
                        System.out.println("place_disc " + column);
                    }
                    break;
                default:
                    System.out.println("unknown command");
                    break;
            }
        }
    }
}
