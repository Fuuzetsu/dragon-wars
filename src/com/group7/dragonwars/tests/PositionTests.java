package com.group7.dragonwars.tests;

import com.group7.dragonwars.engine.Position;

import org.junit.*;
import junit.framework.*;
import static org.junit.Assert.*;


public class PositionTests extends TestCase {

    private Position posOne;
    private Position posTwo;
    private Position posThree;

    public PositionTests() {
        posOne = new Position(7, 6);
        posTwo = new Position(12, 7);
        posThree = new Position(7, 6);
    }

    public void testCreationX() {
        assertEquals(posOne.getX(), (Integer) 7);
    }

    public void testCreationY() {
        assertEquals(posOne.getY(), (Integer) 6);
    }

    public void testEquality() {
        assertTrue(posOne.equals(posThree));
    }

    public void testInequality() {
        assertFalse(posOne.equals(posTwo));
    }

    public void testXEquality() {
        assertEquals(posOne.getX(), posThree.getX());
    }

    public void testYEquality() {
        assertEquals(posOne.getY(), posThree.getY());
    }

    public void testSymmTrue() {
        assertTrue(posOne.equals(posThree) == posThree.equals(posOne));
    }

    public void testSymmFalse() {
        assertTrue(posOne.equals(posTwo) == posThree.equals(posTwo));
    }
}
