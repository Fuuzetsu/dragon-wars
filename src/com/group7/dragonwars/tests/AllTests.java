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
