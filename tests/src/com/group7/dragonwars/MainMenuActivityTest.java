package com.group7.dragonwars;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.group7.dragonwars.MainMenuActivityTest \
 * com.group7.dragonwars.tests/android.test.InstrumentationTestRunner
 */
public class MainMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

    public MainMenuActivityTest() {
        super("com.group7.dragonwars", MainMenuActivity.class);
    }

}
