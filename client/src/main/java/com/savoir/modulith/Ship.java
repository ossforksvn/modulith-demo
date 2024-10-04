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

import javafx.scene.Parent;

public class Ship extends Parent {

    public ShipType shipType;
    public boolean vertical;
    public int armor;

    public Ship(ShipType shipType, boolean vertical) {
        this.shipType = shipType;
        this.vertical = vertical;
        this.armor = getArmorByShipType(shipType);
    }

    public void hit() {
        armor--;
    }

    public boolean isAfloat() {
        return armor > 0;
    }

    private int getArmorByShipType(ShipType type) {
        if (ShipType.CARRIER.equals(type)) {
            return 5;
        } else if (ShipType.BATTLESHIP.equals(type)) {
            return 4;
        } else if (ShipType.DESTROYER.equals(type)) {
            return 3;
        } else if (ShipType.SUBMARINE.equals(type)) {
            return 2;
        } else  {
            return 1;
        }
    }

    public static ShipType getShipTypeByArmor(int armor) {
        if (armor == 5) {
            return ShipType.CARRIER;
        } else if (armor == 4) {
            return ShipType.BATTLESHIP;
        } else if (armor == 3) {
            return ShipType.DESTROYER;
        } else if (armor == 2) {
            return ShipType.SUBMARINE;
        } else {
            return ShipType.CORVETTE;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "shipType=" + shipType +
                ", vertical=" + vertical +
                ", armor=" + armor +
                '}';
    }
}
