#!/bin/sh
ant clean && ant debug && adb uninstall com.group7.dragonwars && adb install bin/DragonWars-debug.apk
