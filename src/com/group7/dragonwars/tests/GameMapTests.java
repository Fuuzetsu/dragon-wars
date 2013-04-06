package com.group7.dragonwars.tests;

import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.MapReader;
import com.group7.dragonwars.engine.GameMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import junit.framework.TestCase;


public class GameMapTests extends TestCase {

    private GameMap gm;

    public GameMapTests() {
        /* TODO map loading */
    }

    public void testMapWidth() {
        assertEquals(gm.getWidth(), (Integer) 10);
    }

    public void testMapHeight() {
        assertEquals(gm.getWidth(), (Integer) 5);
    }

    public void testFieldValiditySuccess() {
        Position p = new Position(1, 2);
        assertTrue(gm.isValidField(p));
        assertTrue(gm.isValidField(1, 2));
    }

    public void testFieldValidityFail() {
        Position p = new Position(1, 99);
        assertFalse(gm.isValidField(p));
        assertFalse(gm.isValidField(1, 99));
    }

    public void testUnitSize() {
        assertEquals(gm.getUnitMap().size(), 2);
    }

    public void testGameFieldSize() {
        assertEquals(gm.getGameFieldMap().size(), 2);
    }

    public void testBuildingSize() {
        assertEquals(gm.getBuildingMap().size(), 2);
    }

    public void testPlayerSize() {
        /* Include Gaia */
        assertEquals(gm.getPlayers().size(), 3);
    }

    private List<String> readFile(final String filename) {
        List<String> text = new ArrayList<String>();

        try {
            BufferedReader in = new BufferedReader(
                new FileReader(filename));
            String line;

            while ((line = in.readLine()) != null) {
                text.add(line);
            }

            in.close();
        } catch (FileNotFoundException fnf) {
            System.err.println("Couldn't find " + fnf.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Couldn't read " + ioe.getMessage());
            System.exit(1);
        }

        return text;
    }


}
