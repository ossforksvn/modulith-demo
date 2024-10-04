/*
 * Copyright (c) 2012-2024 Savoir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.savoir.modulith;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.savoir.modulith.game.api.ActiveGames;
import com.savoir.modulith.game.api.Game;
import com.savoir.modulith.game.api.GameBoard;
import com.savoir.modulith.game.api.GameMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

/**
 * Modulith Game Demo
 */
public class ModulithGame extends Application {

    private ChoiceDialog<String> choiceDialog = new ChoiceDialog();
    private TextInputDialog td = new TextInputDialog("Name Here");
    private TextInputDialog settingsDialog = new TextInputDialog("http://localhost:8181");
    private TextArea output = new TextArea();
    private TextField input = new TextField("Chat here...");
    private HBox gameMaps = new HBox();
    private String userName = null;
    private String gameId = null;
    private String host = "http://localhost:8181";
    private final static String HOST = "HOST";
    private final static String CHALLENGER = "CHALLENGER";

    //Game State
    private Game ourGame = null;
    private BattleMap player, target;
    private boolean running = false;
    private int shipsToPlace = 5;
    private boolean isChallenger = false;
    private boolean firstRound = true;

    //Initialize Modulith Client
    @Override
    public void init() throws Exception {
        super.init();

        //Make ProgressIndicator spin...
        for (int i = 0; i < 100; i++) {
            double progress = (i + 1) / 100.0;
            notifyPreloader(new Preloader.ProgressNotification(progress));
            Thread.sleep(10); // Simulate loading delay
        }
    }

    private Parent createContent() {
        Menu m = buildMenu();
        MenuBar mb = new MenuBar();
        mb.getMenus().add(m);

        VBox chat = new VBox();
        output.setPadding(Insets.EMPTY);
        output.setPrefSize(400, 400); //Sets size of Chat
        output.setText("Welcome to Modulith!");
        input.setPadding(Insets.EMPTY);
        input.setVisible(false);
        chat.setPadding(new Insets(15));
        chat.setSpacing(10);
        chat.getChildren().addAll(output, input);

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                sendChatMessage();
                input.setText("");
            }
        };
        input.setOnAction(event);

        HBox hb = new HBox();
        gameMaps.setPadding(new Insets(15));
        gameMaps.setBorder(Border.stroke(Color.BLACK));
        gameMaps.setPrefSize(600, 400);
        gameMaps.setVisible(false);
        gameMaps.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        player = new BattleMap(false, mouseEvent -> {
            if (running) {
                return;
            }

            BattleMap.Tile tile = (BattleMap.Tile) mouseEvent.getSource();
            //Ship placement is Vertical if Mouse Primary button used.
            if (player.placeShip(new Ship(Ship.getShipTypeByArmor(shipsToPlace), mouseEvent.getButton() == MouseButton.PRIMARY), tile.x, tile.y)) {
                if (--shipsToPlace == 0) {
                    //Player's map is set. Now we need challenger to make their map.
                    if (!isChallenger) {
                        output.appendText("Host has setup Battlemap, awaiting challenger\n");
                        startTwoPlayerGame();
                        output.appendText("Challenger has joined !!!");
                        registerGameBoard(HOST + gameId, player.getBoard().toString());
                    } else {
                        ourGame.setChallengerName(userName);
                        ourGame.setShipsPlaced(true);
                        updateGameState();
                        output.appendText("Host is ready to play you, Challenger.\n");
                        running = true;
                        registerGameBoard(CHALLENGER + gameId, player.getBoard().toString());
                    }
                }
            }
        });

        target = new BattleMap(true, mouseEvent -> {
            if (!running) //Ignore events
                return;
            if (!isOurTurn()) {//Ignore events
                output.appendText("Not our turn!\n");
                return;
            }

            if (firstRound ) { //First round
                String board;
                if (isChallenger) {
                    board = getGameBoard(HOST + gameId);
                    processShotOnPlayer();
                } else {
                    board = getGameBoard(CHALLENGER + gameId);
                }
                target.setupBoard(board);
                firstRound = false;
            } else {
                processShotOnPlayer();
            }

            //Handle this click
            BattleMap.Tile tile = (BattleMap.Tile) mouseEvent.getSource();
            ourGame.setX(tile.x);
            ourGame.setY(tile.y);
            if (isChallenger) {
                ourGame.setHostTurn(true);
            } else {
                ourGame.setHostTurn(false);
            }
            updateGameState();
            if (tile.wasShot) {
                return;
            }
            tile.shoot();
            if (tile.ship != null && !tile.ship.isAfloat()) {
                output.appendText(tile.ship.shipType.name() + " was sunk! \n");
            }
            if (target.ships == 0) {
                output.appendText(userName + " WINS!!!!\n");
            }
        });

        gameMaps.getChildren().add(player);
        gameMaps.getChildren().add(target);
        gameMaps.setSpacing(15);
        hb.setPadding(new Insets(15));
        hb.getChildren().addAll(gameMaps, chat);

        VBox root = new VBox(mb, hb);
        root.setPadding(new Insets(15));
        root.setPrefSize(1200, 480);
        return root;
    }

    private void processShotOnPlayer() {
        //Apply any update from server
        Game thisRound = getGameState();
        BattleMap.Tile lastTurn = player.getTile(thisRound.getX(), thisRound.getY());
        if (lastTurn.wasShot) {
            return;
        }
        lastTurn.shoot();
        if (lastTurn.ship != null && !lastTurn.ship.isAfloat()) {
            output.appendText(lastTurn.ship.shipType.name() + " was sunk! \n");
        }
        if (player.ships == 0) {
            output.appendText(userName + " LOST!");
        }
    }

    private boolean isOurTurn() {
        if (ourGame == null) {
            return false;
        }
        Game rightNow = getGameState();
        if (isChallenger) {
            if (rightNow.isHostTurn()) {
                return false;
            } else {
                return true;
            }
        }
        return rightNow.isHostTurn();
    }

    //CXF CALLS
    private void sendChatMessage() {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/sendGameMessage")
                    .accept(MediaType.APPLICATION_JSON)
                    .type(MediaType.APPLICATION_JSON);;
            GameMessage gameMessage = new GameMessage(gameId, input.getText());
            ObjectMapper mapper = new ObjectMapper();
            webClient.post(mapper.writeValueAsString(gameMessage));
        } catch (Exception ignored) {
            //Ignore
        }
    }

    private Game getGameState() {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/gameState")
                    .accept(MediaType.APPLICATION_JSON)
                    .type(MediaType.APPLICATION_JSON);
            Response resp = webClient.post(gameId);
            return resp.readEntity(Game.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void getNewGame() {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/newGame")
                    .accept(MediaType.APPLICATION_JSON);
            Response respGet = webClient.get();
            ourGame =  respGet.readEntity(Game.class);
            gameId = ourGame.getId();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private ActiveGames getActiveGames() {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/getActiveGames")
                    .accept(MediaType.APPLICATION_JSON);
            Response respGet = webClient.get();
            return respGet.readEntity(ActiveGames.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void endGame() {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/endGame");
            webClient.invoke("DELETE", gameId);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void updateGameState() {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/updateGame")
                    .accept(MediaType.APPLICATION_JSON)
                    .type(MediaType.APPLICATION_JSON);
            ObjectMapper mapper = new ObjectMapper();
            webClient.put(mapper.writeValueAsString(ourGame));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void registerGameBoard(String boardId, String board) {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/registerGameBoard")
                    .accept(MediaType.APPLICATION_JSON)
                    .type(MediaType.APPLICATION_JSON);
            GameBoard gameBoard = new GameBoard(boardId, board);
            webClient.post(gameBoard);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getGameBoard(String boardId) {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/gameBoard")
                    .accept(MediaType.TEXT_PLAIN)
                    .type(MediaType.APPLICATION_JSON);
            Response respGet = webClient.post(boardId);
            return respGet.readEntity(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void removeGameBoard(String boardId) {
        try {
            WebClient webClient = WebClient.create(host + "/cxf/game/removeGameBoard");
            webClient.invoke("DELETE", boardId);
        } catch (Exception exception) {
            //Ignore
        }
    }

    /**
     * Player is ready to start game,
     * Get second player's ship placement.
     */
    private void startTwoPlayerGame() {
        while (!isGameReadyToStart()) {
            Dialog<String> dialog = new Dialog<String>();
            dialog.setTitle("Await Challenger");
            if (ourGame != null) {
                if (ourGame.getChallengerName() == null) {
                    dialog.setContentText("Please wait for Challenger to join and setup their map.");
                } else {
                    dialog.setContentText("Please wait for Challenger " + ourGame.getChallengerName() + "to setup their map.");
                }
            } else {
                dialog.setContentText("Our game is NULL (oh no!)");
            }
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(type);
            dialog.showAndWait();
            output.appendText(ourGame.toString() + "\n");
        }
        running = true;
    }

    private boolean isGameReadyToStart() {
        ourGame = getGameState();
        if (ourGame == null) {
            return false;
        }
        if (ourGame.getChallengerName() == null) {
            return false;
        }
        return ourGame.isShipsPlaced();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Create a scene with the layoutManager as the root
        Scene scene = new Scene(createContent());
        scene.setFill(Color.BLACK);
        stage.setTitle("Modulith Game Service Demo");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        // Specify the custom preloader class
        System.setProperty("javafx.preloader", IntroScreen.class.getName());
        launch(args);
    }

    public Menu buildMenu() {
        Menu m = new Menu("Menu");
        MenuItem m1 = new MenuItem("New Game");
        MenuItem m2 = new MenuItem("Join Game");
        MenuItem m3 = new MenuItem("Exit Game");
        MenuItem m4 = new MenuItem("Settings");
        m.getItems().add(m1);
        m.getItems().add(m2);
        m.getItems().add(m4);
        m.getItems().add(m3);

        EventHandler<ActionEvent> newGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (userName == null) {
                    td.setTitle("Set Player Name");
                    td.setHeaderText("Enter player name");
                    td.showAndWait();
                    userName = td.getEditor().getText();
                }
                getNewGame();
                ourGame.setHostTurn(true);
                updateGameState();
                output.clear();
                output.appendText("New Game for " + userName + "\n");
                output.appendText("GameId: " + gameId + "\n");
                output.appendText("Awaiting additional players...\n");
                gameMaps.setVisible(true);
                input.setVisible(true);
                isChallenger = false;
            }
        };

        EventHandler<ActionEvent> joinGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (userName == null) {
                    td.setTitle("Set Player Name");
                    td.setHeaderText("Enter player name");
                    td.showAndWait();
                    userName = td.getEditor().getText();
                }
                choiceDialog.setTitle("Find Game");
                ActiveGames activeGames = getActiveGames();
                choiceDialog = buildChoiceDialog(activeGames);
                Optional<String> result = choiceDialog.showAndWait();
                if (result.isPresent()) {
                    result.ifPresent(s -> gameId = s);
                    output.clear();
                    output.appendText(userName + " Joining Game: " + gameId + "\n");
                    gameMaps.setVisible(true);
                    input.setVisible(true);
                    isChallenger = true;
                    ourGame = getGameState();
                } else {
                    output.appendText(userName + " Decided to not join a game.\n");
                }
            }
        };

        EventHandler<ActionEvent> exitGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (gameId != null) {
                    if (isChallenger) {
                        removeGameBoard(CHALLENGER + gameId);
                    } else {
                        removeGameBoard(HOST + gameId);
                    }
                    endGame();
                }
                System.exit(0);
            }
        };

        EventHandler<ActionEvent> gameSettings = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (userName == null) {
                    settingsDialog.setTitle("Settings");
                    settingsDialog.setHeaderText("Please supply server address");
                    settingsDialog.showAndWait();
                    host = settingsDialog.getEditor().getText();
                }
            }
        };

        m1.setOnAction(newGame);
        m2.setOnAction(joinGame);
        m3.setOnAction(exitGame);
        m4.setOnAction(gameSettings);
        return m;
    }

    private ChoiceDialog<String> buildChoiceDialog(ActiveGames activeGames) {
        if (activeGames == null || activeGames.getActiveGames().isEmpty()) {
            return new ChoiceDialog<>();
        }
        if (activeGames.getActiveGames().size() == 1) {
            return new ChoiceDialog<>(activeGames.getActiveGames().get(0));
        }
        String firstId = activeGames.getActiveGames().get(0);
        List<String> remaingGames = activeGames.getActiveGames().subList(1, activeGames.getActiveGames().size());
        List<String> otherGames = new ArrayList<>();
        otherGames.addAll(remaingGames);
        return new ChoiceDialog<>(firstId, otherGames);
    }
}
