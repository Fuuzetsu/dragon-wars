/* This file is part of Dragon Wars.
 *
 * Dragon Wars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragon Wars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dragon Wars.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.group7.dragonwars.tests;

import com.group7.dragonwars.engine.Building;
import com.group7.dragonwars.engine.GameField;
import com.group7.dragonwars.engine.Unit;
import com.group7.dragonwars.engine.Player;

import static junit.framework.Assert.*;
import junit.framework.TestCase;


public class GameFieldTests extends TestCase {

    private GameField gf;

    public GameFieldTests() {
    }

    public void setUp() {
        this.gf = new GameField("Grass", 2.0, 2.0, 2.0,
                                true, false, "fake", "fakeDir",
                                "fake.pack");
    }

    public void tearDown() {
        this.gf = null;
    }

    public void testDefenseModifierPlain() {
        assertEquals(gf.getDefenseModifier(), (Double) 2.0);
    }

    public void testDefenseModifierBuilding() {
        Double bMod = 3.5;
        Building b = new Building("Misaki", 2, 2.0, bMod,
                                  false, 2, "fake", "fakeDir",
                                  "fake.pack");
        Double result = (2.0 + bMod) / 2;
        gf.setBuilding(b);
        assertEquals(gf.getDefenseModifier(), result);
    }

    public void testAttackModifierPlain() {
        assertEquals(gf.getAttackModifier(), (Double) 2.0);
    }

    public void testAttackModifierBuilding() {
        Double bMod = 3.5;
        Building b = new Building("Misaki", 2, bMod, 2.0,
                                  false, 2, "fake", "fakeDir",
                                  "fake.pack");
        Double result = (2.0 + bMod) / 2;
        gf.setBuilding(b);
        assertEquals(gf.getAttackModifier(), result);
    }

    public void testUnitHostSuccess() {
        Player s = new Player("Shana", 0);
        Unit u = new Unit("Nietono", 2.0, 2, 2.0,
                          2.0, 2.0, false, 2, "fake",
                          "fakeDir", "fake.pack");
        u.setOwner(s);
        gf.setUnit(u);
        assertTrue(gf.hostsUnit());
        assertEquals(gf.getUnit(), u);
    }

    public void testUnitHostFail() {
        assertFalse(gf.hostsUnit());
        assertTrue(gf.getUnit() == null);
    }

    public void testAcceptsUnit() {
        Player s = new Player("Shana", 0);
        Unit u = new Unit("Nietono", 2.0, 2, 2.0,
                          2.0, 2.0, false, 2, "fake",
                          "fakeDir", "fake.pack");
        u.setOwner(s);
        assertTrue(gf.doesAcceptUnit(u));
    }


}
