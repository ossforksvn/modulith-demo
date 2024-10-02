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

import com.savoir.modulith.game.api.ActiveGames;
import com.savoir.modulith.game.api.Game;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;

/**
 * Modulith Game Demo
 */
public class ModulithGame extends Application {

    private ChoiceDialog<String> choiceDialog = new ChoiceDialog();
    private TextInputDialog td = new TextInputDialog("Name Here");
    private TextArea output = new TextArea();
    private TextField input = new TextField("Chat here...");
    private Pane pane = new Pane();
    private String userName = null;
    private String gameId = null;

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
        output.setPrefSize(400, 300);
        output.setText("Welcome to Modulith!");
        input.setPadding(Insets.EMPTY);
        input.setVisible(false);
        chat.setPadding(new Insets(15));
        chat.setSpacing(10);
        chat.getChildren().addAll(output, input);

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                //TODO Send to server
                output.appendText(userName + ": " + input.getText() + "\n");
            }
        };
        input.setOnAction(event);

        //TODO Pane will contain the game
        HBox hb = new HBox();
        pane.setPadding(new Insets(15));
        pane.setBorder(Border.stroke(Color.BLACK));
        pane.setPrefSize(600, 400);
        pane.setVisible(false);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        hb.setPadding(new Insets(15));
        hb.getChildren().addAll(pane, chat);

        VBox root = new VBox(mb, hb);
        root.setPadding(new Insets(15));
        root.setPrefSize(640, 480);
        return root;
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Create a scene with the layoutManager as the root
        Scene scene = new Scene(createContent());
        scene.setFill(Color.BLACK);
        stage.setTitle("Modulith Game Demo");
        stage.setScene(scene);
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
        m.getItems().add(m1);
        m.getItems().add(m2);
        m.getItems().add(m3);

        EventHandler<ActionEvent> newGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (userName == null) {
                    td.setHeaderText("Enter player name");
                    td.showAndWait();
                    userName = td.getEditor().getText();
                }
                try {
                    WebClient webClient = WebClient.create("http://localhost:8181/cxf/game/newGame");
                    Response respGet = webClient.get();
                    Game game =  respGet.readEntity(Game.class);
                    gameId = game.getId();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                output.clear();
                output.appendText("New Game for " + userName + "\n");
                output.appendText("GameId: " + gameId + "\n");
                output.appendText("Awaiting additional players...\n");
                pane.setVisible(true);
                input.setVisible(true);
            }
        };

        EventHandler<ActionEvent> joinGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (userName == null) {
                    td.setHeaderText("Enter player name");
                    td.showAndWait();
                    userName = td.getEditor().getText();
                }
                choiceDialog.setTitle("Find Game");
                //TODO get available gets from server
                ActiveGames activeGames = null;
                try {
                    WebClient webClient = WebClient.create("http://localhost:8181/cxf/game/getActiveGames");
                    Response respGet = webClient.get();
                    activeGames = respGet.readEntity(ActiveGames.class);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                choiceDialog = buildChoiceDialog(activeGames);
                Optional<String> result = choiceDialog.showAndWait();
                result.ifPresent(s -> gameId = s);
                output.clear();
                output.appendText(userName + " Joining Game: " + gameId + "\n");
                pane.setVisible(true);
                input.setVisible(true);
            }
        };

        EventHandler<ActionEvent> exitGame = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        };

        m1.setOnAction(newGame);
        m2.setOnAction(joinGame);
        m3.setOnAction(exitGame);
        return m;
    }

    private ChoiceDialog<String> buildChoiceDialog(ActiveGames activeGames) {
        if (activeGames.getActiveGames().isEmpty()) {
            return new ChoiceDialog<>();
        }
        if (activeGames.getActiveGames().size() == 1) {
            return new ChoiceDialog<>(activeGames.getActiveGames().get(0).getId());
        }
        String firstId = activeGames.getActiveGames().get(0).getId();
        List<Game> remaingGames = activeGames.getActiveGames().subList(1, activeGames.getActiveGames().size());
        List<String> otherGames = new ArrayList<>();
        remaingGames.stream().map(Game::getId).forEach(otherGames::add);
        return new ChoiceDialog<>(firstId, otherGames);
    }
}
