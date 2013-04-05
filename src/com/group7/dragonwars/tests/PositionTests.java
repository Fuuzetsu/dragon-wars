package com.group7.dragonwars.tests;

import com.group7.dragonwars.engine.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import junit.framework.*;

import org.junit.*;
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
        assertEquals(posThree, posOne);
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

    public void testHashSetSame() {
        Set<Position> hSet = new HashSet<Position>();
        hSet.add(posOne);
        hSet.add(posThree);
        hSet.add(posTwo);
        hSet.add(posOne);
        hSet.add(posOne);
        hSet.add(posOne);
        assertTrue(hSet.size() == 2);
    }

    public void testListContainsSuccess() {
        List<Position> pList = new ArrayList<Position>();
        pList.add(posOne);
        pList.add(posTwo);
        assertTrue(pList.contains(posOne));
    }

    public void testListContainsSuccessDiff() {
        List<Position> pList = new ArrayList<Position>();
        pList.add(posOne);
        pList.add(posTwo);
        assertTrue(pList.contains(posThree));
    }

    public void testListContainsFail() {
        List<Position> pList = new ArrayList<Position>();
        pList.add(posOne);
        pList.add(posThree);
        assertFalse(pList.contains(posTwo));
    }

}
