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
package com.savoir.modulith.game.impl;

import com.savoir.modulith.game.api.ActiveGames;
import com.savoir.modulith.game.api.GameBoard;
import com.savoir.modulith.game.api.GameMessage;
import com.savoir.modulith.game.api.GameService;
import com.savoir.modulith.game.api.Game;

import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class GameServiceImpl implements GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);

    private GameStore gameStore;
    private MessageStore messageStore;

    public GameServiceImpl(GameStore gameStore, MessageStore messageStore) {
        this.gameStore = gameStore;
        this.messageStore = messageStore;
    }

    @Override
    @Path("/newGame")
    @Produces("application/json")
    @GET
    public Game newGame() {
        LOGGER.info("newGame");
        Game game = new Game();
        game.setId(UUID.randomUUID().toString());
        gameStore.put(game.getId(), game);
        return game;
    }

    @Override
    @Path("/joinActiveGame")
    @Produces("application/json")
    @GET
    public Game joinActiveGame(String gameId) {
        LOGGER.info("Join game: " + gameId.toString());
        Game game = gameStore.get(gameId);
        return game;
    }

    @Override
    @Path("/getActiveGames")
    @Produces("application/json")
    @GET
    public ActiveGames getActiveGames() {
        LOGGER.info("Get active games.");
        ActiveGames activeGames = new ActiveGames(gameStore.values());
        return activeGames;
    }

    @Override
    @Path("/updateGame")
    @PUT
    public void updateGame(Game game) {
        LOGGER.info("Update game: " + game.getId());
        Game runningGame = gameStore.get(game.getId());
        if (runningGame != null) {
            gameStore.remove(game.getId());
            gameStore.put(game.getId(), game);
        }
    }

    @Override
    @Path("/gameState")
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public Game gameState(String gameId) {
        LOGGER.info("Find game: " + gameId);
        if (gameId == null) {
            LOGGER.info("Must supply non-null game id");
        }
        LOGGER.info("Game: " + gameStore.get(gameId));
        return gameStore.get(gameId);
    }

    @Override
    @Path("/endGame")
    @DELETE
    public void endGame(String gameId) {
        LOGGER.info("End game: " + gameId);
        gameStore.remove(gameId);
    }

    @Override
    @Path("/sendGameMessage")
    @Consumes("application/json")
    @POST
    public void sendGameMessage(GameMessage gameMessage) {
        LOGGER.info("Send game: " + gameMessage.getGameId() + " message: " + gameMessage.getMessage());
        messageStore.putMessage(gameMessage.getGameId(), gameMessage.getMessage());
    }

    @Override
    @Path("/gameMessage")
    @Produces("text/plain")
    @Consumes("application/json")
    @POST
    public String getGameMessage(String gameId) {
        LOGGER.info("Get game message for: " + gameId);
        return messageStore.getMessage(gameId);
    }

    @Override
    @Path("/registerGameBoard")
    @Consumes("application/json")
    @POST
    public void registerGameBoard(GameBoard gameBoard) {
        LOGGER.info("Register game board: " + gameBoard.getGameId() + " board: " + gameBoard.getBoard());
        gameStore.addBoard(gameBoard.getGameId(), gameBoard.getBoard());
    }

    @Override
    @Path("/gameBoard")
    @Produces("text/plain")
    @Consumes("application/json")
    @POST
    public String gameBoard(String gameId) {
        LOGGER.info("Get game board for: " + gameId);
        LOGGER.info("Board: " + gameStore.getBoard(gameId));
        return gameStore.getBoard(gameId);
    }

    @Override
    @Path("/removeGameBoard")
    @DELETE
    public void removeGameBoard(String id) {
        LOGGER.info("Remove board: " + id);
        gameStore.removeBoard(id);
    }
}
