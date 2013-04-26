package com.group7.dragonwars.tests;

import junit.framework.TestSuite;
import junit.framework.Test;
import junit.runner.Version;

public class AllTests {

    public static Test suite() {
        System.out.println("JUnit version is: " + Version.id());
        TestSuite suite = new TestSuite(PositionTests.class);
        suite.addTestSuite(PlayerTests.class);
        suite.addTestSuite(UnitTests.class);
        suite.addTestSuite(GameFieldTests.class);
        //suite.addTestSuite(GameMapTests.class);
        return suite;
    }

}
