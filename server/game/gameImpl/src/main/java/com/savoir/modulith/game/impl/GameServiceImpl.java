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
import com.savoir.modulith.game.api.GameService;
import com.savoir.modulith.game.api.Game;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
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

    private ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();

    public GameServiceImpl() {
        //Empty Constructor
    }

    @Override
    @Path("/newGame")
    @Produces("application/json")
    @GET
    public Game newGame() {
        LOGGER.info("newGame");
        Game game = new Game();
        games.put(game.getId(), game);
        return game;
    }

    @Override
    @Path("/joinActiveGame")
    @Produces("application/json")
    @GET
    public Game joinActiveGame(String gameId) {
        LOGGER.info("Join game: " + gameId.toString());
        Game game = games.get(gameId);
        return game;
    }

    @Override
    @Path("/getActiveGames")
    @Produces("application/json")
    @GET
    public ActiveGames getActiveGames() {
        LOGGER.info("Get active games.");
        ActiveGames activeGames = new ActiveGames(new ArrayList<>(games.values()));
        return activeGames;
    }

    @Override
    @Path("/updateGame")
    @PUT
    public void updateGame(Game game) {
        LOGGER.info("Update game: " + game.getId());
        Game runningGame = games.get(game.getId());
        if (runningGame != null) {
            //Update game
        }
    }

    @Override
    @Path("/endGame")
    @DELETE
    public void endGame(String gameId) {
        LOGGER.info("End game: " + gameId);
        games.remove(gameId);
    }

    @Override
    @Path("/sendGameMessage")
    @Consumes("application/json")
    @POST
    public void sendGameMessage(String gameId, String message) {
        LOGGER.info("Send game: " + gameId + " message: " + message);
        //TODO Send to some service.
    }

    @Override
    @Path("/getGameMessage")
    @Produces("text/plain")
    @GET
    public String getGameMessage(String gameId) {
        LOGGER.info("Get game message for: " + gameId);
        //TODO get from some service.
        return "Message for game: " + gameId;
    }
}
