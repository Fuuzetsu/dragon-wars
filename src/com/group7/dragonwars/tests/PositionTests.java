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

import com.group7.dragonwars.engine.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import junit.framework.TestCase;
import static junit.framework.Assert.*;


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
