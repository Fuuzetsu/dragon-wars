package com.group7.dragonwars.tests;

import com.group7.dragonwars.engine.Building;
import com.group7.dragonwars.engine.Player;
import com.group7.dragonwars.engine.Unit;

import static junit.framework.Assert.*;
import junit.framework.TestCase;


public class PlayerTests extends TestCase {

    private Player p;
    private Player r;

    public PlayerTests() {
    }

    public void setUp() {
        this.p = new Player("Shana", 0);
        this.r = new Player("Yukari", 0);
    }

    public void tearDown() {
        this.p = null;
        this.r = null;
    }

    public void testNameSuccess() {
        assertEquals(p.getName(), "Shana");
        assertEquals(r.getName(), "Yukari");
    }

    public void testNameFail() {
        assertFalse(p.getName().equals("Fake"));
    }

    public void testLossSucces() {
        assertTrue(p.hasLost());
    }

    public void testLostUnitFail() {
        Unit u = new Unit("Nietono", 2.0, 2, 2.0,
                          2.0, 2.0, false, 2, "fake",
                          "fakeDir", "fake.pack");
        p.addUnit(u);
        assertFalse(p.hasLost());
    }

    public void testLostBuildingFail() {
        Building b = new Building("Misaki", 2, 2.0, 2.0,
                                  false, 2, "fake", "fakeDir",
                                  "fake.pack");
        r.addBuilding(b);
        assertFalse(r.hasLost());
    }

    public void testMoveableUnitsSuccess() {
        Unit u = new Unit("Nietono", 2.0, 2, 2.0,
                          2.0, 2.0, false, 2, "fake",
                          "fakeDir", "fake.pack");
        p.addUnit(u);
        assertTrue(p.hasMoveableUnits());
    }

    public void testMoveableUnitsFail() {
        Unit u = new Unit("Nietono", 2.0, 0, 2.0,
                          2.0, 2.0, false, 2, "fake",
                          "fakeDir", "fake.pack");
        u.setFinishedTurn(true);
        p.addUnit(u);
        assertFalse(p.hasMoveableUnits());
    }

    public void testGoldAmount() {
        p.setGoldAmount(6);
        assertEquals(p.getGoldAmount(), (Integer) 6);
    }
}
