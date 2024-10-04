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
package com.savoir.modulith.game.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "game")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(value = "game")
@JsonRootName(value = "game")
public class Game {

    @XmlElement(required = true)
    private String id;

    @XmlElement(required = false)
    private String challengerName;

    @XmlElement(required = true)
    private boolean shipsPlaced;

    @XmlElement(required = true)
    private boolean hostTurn;

    @XmlElement(required = false)
    private int x;

    @XmlElement(required = false)
    private int y;

    public Game() {
    }

    public String getId() {
        return id;
    }

    public String getChallengerName() {
        return challengerName;
    }

    public boolean isShipsPlaced() {
        return shipsPlaced;
    }

    public boolean isHostTurn() {
        return hostTurn;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChallengerName(String challengerName) {
        this.challengerName = challengerName;
    }

    public void setShipsPlaced(boolean shipsPlaced) {
        this.shipsPlaced = shipsPlaced;
    }

    public void setHostTurn(boolean hostTurn) {
        this.hostTurn = hostTurn;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", challengerName='" + challengerName + '\'' +
                ", shipsPlaced=" + shipsPlaced +
                ", hostTurn=" + hostTurn +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
