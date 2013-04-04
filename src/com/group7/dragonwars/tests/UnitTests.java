package com.group7.dragonwars.tests;

import com.group7.dragonwars.engine.Player;
import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.Unit;

import org.junit.*;
import junit.framework.TestCase;


public class UnitTests extends TestCase {

    private Unit u;
    private Unit p;

    public UnitTests() {
    }

    public void setUp() {
        Player s = new Player("Shana");
        Player r = new Player("Rori");
        this.u = new Unit("Nietono", 2.0, 2, 2.0,
                          2.0, 2.0, false, 2, "fake",
                          "fakeDir", "fake.pack");
        u.setOwner(s);
        this.p = new Unit("Alastor", 5.0, 6, 2.0,
                          4.0, 3.0, true, 2, "fakeAla",
                          "fakeDir", "fake.pack");
        p.setOwner(r);

    }

    public void tearDown() {
        this.u = null;
        this.p = null;
    }

    public void testSetPositionSuccess() {
        Position t = new Position(7, 3);
        u.setPosition(t);
        assertEquals(u.getPosition(), t);
    }

    public void testSetPositionFail() {
        Position t = new Position(7, 3);
        Position o = new Position(7, 4);
        u.setPosition(t);
        assertFalse(u.getPosition().equals(o));
    }

    public void testSetOwnerSuccess() {
        Player n = new Player("Akarin");
        u.setOwner(n);
        assertEquals(u.getOwner(), n);
    }

    public void testSetOwnerFail() {
        Player n = new Player("Akarin");
        Player x = u.getOwner();
        u.setOwner(n);
        assertFalse(u.getOwner().equals(x));
    }

    public void testReduceHealthNormal() {
        Double h = u.getHealth();
        Double r = 1.0;
        u.reduceHealth(r);
        assertEquals(u.getHealth(), h - r);
    }

    public void testReduceHealthOver() {
        Double h = u.getHealth();
        Double r = 999.0;
        u.reduceHealth(r);
        assertEquals(u.getHealth(), (Double) 0.0);
    }

    public void testRestoreHealthNormal() {
        u.reduceHealth(1.5);
        Double h = u.getHealth();
        Double r = 1.0;
        u.restoreHealth(r);
        assertEquals(u.getHealth(), h + r);
    }

    public void testRestoreHealthOver() {
        u.reduceHealth(1.5);
        Double h = u.getHealth();
        Double r = 9999.0;
        u.restoreHealth(r);
        assertEquals(u.getHealth(), u.getMaxHealth());
    }

    public void testFlyingSuccess() {
        assertEquals(u.isFlying(), (Boolean) false);
        assertEquals(p.isFlying(), (Boolean) true);
    }

    public void testFlyingFail() {
        assertFalse(u.isFlying() ==  (Boolean) true);
        assertFalse(p.isFlying() ==  (Boolean) false);
    }

}
