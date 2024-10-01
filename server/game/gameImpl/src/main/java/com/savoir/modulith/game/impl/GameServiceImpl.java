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

import com.savoir.modulith.game.api.GameService;
import com.savoir.modulith.game.api.Game;

import java.util.List;
import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class GameServiceImpl implements GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);


    @Override
    @Path("/newGame")
    @Produces("application/json")
    @GET
    public Game newGame() {
        LOGGER.info("newGame");
        return new Game();
    }

    @Override
    @Path("/joinGame")
    @Produces("application/json")
    @GET
    public Game joinGame(UUID gameId) {
        LOGGER.info("Join game: " + gameId.toString());
        return null;
    }

    @Override
    @Path("/getActiveGames")
    @Produces("application/json")
    @GET
    public List<Game> getActiveGames() {
        LOGGER.info("Get active games.");
        return List.of();
    }
}
