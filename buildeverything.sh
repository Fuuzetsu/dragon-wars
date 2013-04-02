#!/bin/sh
ant debug && cd tests && ant debug && adb uninstall com.group7.dragonwars.tests && adb install bin/DragonTest-debug.apk && cd .. && adb install bin/DragonWars-debug.apk
