package com.group7.dragonwars.tests;

import org.junit.extensions.cpsuite.ClasspathSuite;
import static org.junit.extensions.cpsuite.SuiteType.*;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.TestSuite;
import junit.framework.Test;
import junit.runner.Version;



@RunWith(Suite.class)
//@SuiteTypes(JUNIT38_TEST_CLASSES)
@Suite.SuiteClasses({PositionTests.class, PlayerTests.class})
public class AllTests {

	public static Test suite() {
        System.out.println("JUnit version is: " + Version.id());
		TestSuite suite = new TestSuite(PositionTests.class);
		suite.addTestSuite(PlayerTests.class);
        suite.addTestSuite(UnitTests.class);
		return suite;
	}

}
