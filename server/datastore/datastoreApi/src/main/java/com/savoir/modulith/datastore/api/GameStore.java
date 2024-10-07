package com.savoir.modulith.datastore.api;

import java.util.List;
import com.savoir.modulith.game.api.Game;

public interface GameStore {

    public void put(String id, Game game);

    public Game get(String id);

    public void remove(String id);

    public List values();

    public void addBoard(String id, String board);

    public String getBoard(String id);

    public void removeBoard(String id);

    public int getGamesPlayedCount();

    public int getActiveGamesCount();

    public void incrementGamesPlayedCount();
}
