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
package com.savoir.modulith.datastore.impl;

import com.savoir.modulith.datastore.api.GameStore;
import com.savoir.modulith.game.api.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStoreImpl implements GameStore {

    private Map<String, Game> games;
    private Map<String, String> boards;
    private int gameCount;

    public GameStoreImpl() {
        this.games = new HashMap<>();
        this.boards = new HashMap<>();
        this.gameCount = 0;
    }

    @Override
    public void put(String id, Game game) {
        this.games.put(id, game);
    }

    @Override
    public Game get(String id) {
        return this.games.get(id);
    }

    @Override
    public void remove(String id) {
       this.games.remove(id);
    }

    @Override
    public List values() {
        return new ArrayList<>(this.games.keySet());
    }

    @Override
    public void addBoard(String id, String board) {
        this.boards.put(id, board);
    }

    @Override
    public String getBoard(String id) {
        return this.boards.get(id);
    }

    @Override
    public void removeBoard(String id) {
        this.boards.remove(id);
    }

    @Override
    public int getGamesPlayedCount() {
        return gameCount;
    }

    @Override
    public int getActiveGamesCount() {
        return this.games.size();
    }

    @Override
    public void incrementGamesPlayedCount() {
        gameCount++;
    }
}
