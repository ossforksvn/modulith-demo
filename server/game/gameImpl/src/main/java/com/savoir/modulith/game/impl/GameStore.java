package com.savoir.modulith.game.impl;

import com.savoir.modulith.game.api.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStore {

    private Map<String, Game> games;
    private Map<String, String> boards;

    public GameStore() {
        this.games = new HashMap<>();
        this.boards = new HashMap<>();
    }

    public void put(String id, Game game) {
        this.games.put(id, game);
    }

    public Game get(String id) {
        return this.games.get(id);
    }

    public void remove(String id) {
       this.games.remove(id);
    }

    public List values() {
        return new ArrayList<>(this.games.keySet());
    }

    public void addBoard(String id, String board) {
        this.boards.put(id, board);
    }

    public String getBoard(String id) {
        return this.boards.get(id);
    }

    public void removeBoard(String id) {
        this.boards.remove(id);
    }
}
