package com.group7.dragonwars.tests;

import com.group7.dragonwars.engine.Player;

import org.junit.*;
import junit.framework.TestCase;

public class PlayerTests extends TestCase {

    public PlayerTests() {
    }

    public void testNameSuccess() {
        Player p = new Player("Shana");
        Player r = new Player("Yukari");
        assertEquals(p.getName(), "Shana");
        assertEquals(r.getName(), "Yukari");
    }
}
