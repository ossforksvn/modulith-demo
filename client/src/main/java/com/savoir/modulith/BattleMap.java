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

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class BattleMap extends Parent {

    private VBox rows = new VBox();
    private boolean player = false;
    public int ships = 5;
    private List placedShips = new ArrayList<Location>();

    /**
     * BattleMap - the game board for player.
     * @param player
     * @param handler
     */
    public BattleMap(boolean player, EventHandler<? super MouseEvent> handler) {
        this.player = player;
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            for (int x = 0; x < 10; x++) {
                Tile t = new Tile(x, y, this);
                t.setOnMouseClicked(handler);
                row.getChildren().add(t);
            }
            rows.getChildren().add(row);
        }
        getChildren().add(rows);
    }

    public JSONArray getBoard() {
        JSONArray board = new JSONArray();
        for (int i = 0; i < ships; i++) {
            board.put(placedShips.get(i));
        }
        return board;
    }

    public void setupBoard(String board) {
        try {
            JSONArray array = new JSONArray(board);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = new JSONObject(String.valueOf(array.get(i)));
                JSONObject jsonShip = jsonObject.getJSONObject("ship");
                ShipType shipType = ShipType.valueOf(jsonShip.getString("shipType"));
                boolean vertical = jsonShip.getBoolean("vertical");
                Ship ship = new Ship(shipType, vertical);
                Location location = new Location(ship, jsonObject.getInt("x"), jsonObject.getInt("y"));
                placeShip(location.ship, location.x, location.y);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean placeShip(Ship ship, int x, int y) {
        if (canPlaceShip(ship, x, y)) {
            if (ship.vertical) {
                placeShipVerticallyOnBattleMap(ship, x, y);
            } else {
                placeShipHorizontallyOnBattleMap(ship, x, y);
            }
            placedShips.add(new Location(ship, x, y));
            return true;
        }
        return false;
    }

    private void placeShipVerticallyOnBattleMap(Ship ship, int x, int y) {
        for (int i = y; i < y + ship.armor; i++) {
            Tile tile = getTile(x, i);
            tile.ship = ship;
            if (!player) {
                tile.setFill(Color.WHITE);
                tile.setStroke(Color.GREEN);
            }
        }
    }

    private void placeShipHorizontallyOnBattleMap(Ship ship, int x, int y) {
        for (int i = x; i < x + ship.armor; i++) {
            Tile tile = getTile(i, y);
            tile.ship = ship;
            if (!player) {
                tile.setFill(Color.WHITE);
                tile.setStroke(Color.GREEN);
            }
        }
    }

    private Tile[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[] {
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Tile> neighbors = new ArrayList<Tile>();

        for (Point2D p : points) {
            if (isValidPoint(p)) {
                neighbors.add(getTile((int)p.getX(), (int)p.getY()));
            }
        }

        return neighbors.toArray(new Tile[0]);
    }

    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.armor;
        if (ship.vertical) {
            return processVerticalPlacemen(length, x, y);
        } else {
            return processHorizontalPlacemen(length, x, y);
        }
    }

    private boolean processVerticalPlacemen(int length, int x, int y) {
        for (int i = y; i < y + length; i++) {
            if (!isValidPoint(x, i))
                return false;

            Tile tile = getTile(x, i);
            if (tile.ship != null)
                return false;

            for (Tile neighbor : getNeighbors(x, i)) {
                if (!isValidPoint(x, i))
                    return false;

                if (neighbor.ship != null)
                    return false;
            }
        }
        return true;
    }

    private boolean processHorizontalPlacemen(int length, int x, int y) {
        for (int i = x; i < x + length; i++) {
            if (!isValidPoint(i, y))
                return false;

            Tile tile = getTile(i, y);
            if (tile.ship != null)
                return false;

            for (Tile neighbor : getNeighbors(i, y)) {
                if (!isValidPoint(i, y))
                    return false;

                if (neighbor.ship != null)
                    return false;
            }
        }
        return true;
    }

    public Tile getTile(int x, int y) {
        return (Tile)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }

    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    /**
     * Tile - a map tile.
     */
    public static class Tile extends Rectangle {
        public int x, y;
        public Ship ship = null;
        public boolean wasShot = false;

        private BattleMap battleMap;

        public Tile(int x, int y, BattleMap battleMap) {
            super(30, 30);
            this.x = x;
            this.y = y;
            this.battleMap = battleMap;
            if (battleMap.player) {
                setFill(Color.ALICEBLUE); //Challenger
            } else {
                setFill(Color.CADETBLUE);//Player
            }
            setStroke(Color.BLACK);
        }

        public boolean shoot() {
            wasShot = true;
            setFill(Color.BLACK);

            if (ship != null) {
                ship.hit();
                setFill(Color.RED);
                if (!ship.isAfloat()) {
                    battleMap.ships--;
                }
                return true;
            }

            return false;
        }
    }

    public static class Location {
        public int x, y;
        public Ship ship;
        public Location(Ship ship, int x, int y) {
            this.ship = ship;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "{" +
                    "x=" + x +
                    ", y=" + y +
                    ", ship=" + ship +
                    '}';
        }
    }
}
