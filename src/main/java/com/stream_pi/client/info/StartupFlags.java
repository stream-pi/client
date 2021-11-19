/*
 * Stream-Pi - Free & Open-Source Modular Cross-Platform Programmable Macro Pad
 * Copyright (C) 2019-2021  Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.stream_pi.client.info;

public class StartupFlags
{
    public static String RUNNER_FILE_NAME = null;
    public static boolean IS_SHOW_SHUT_DOWN_BUTTON = false;
    public static boolean IS_X_MODE = true;
    public static boolean SCREEN_SAVER_FEATURE= false;
    public static boolean DEFAULT_FULLSCREEN_MODE=false;
    public static boolean SHOW_FULLSCREEN_TOGGLE_BUTTON=true;
    public static boolean APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION = false;
    public static boolean ALLOW_ROOT = false;
    public static boolean SET_FIXED_MIN_SIZE = true;

    public static void init()
    {
        String startupRunnerFileName = System.getProperty("Stream-Pi.startupRunnerFileName");
        RUNNER_FILE_NAME = (startupRunnerFileName == null) ? RUNNER_FILE_NAME : startupRunnerFileName;

        String showShutDownButton = System.getProperty("Stream-Pi.showShutDownButton");
        IS_SHOW_SHUT_DOWN_BUTTON = (showShutDownButton == null) ? IS_SHOW_SHUT_DOWN_BUTTON : showShutDownButton.equals("true");

        String isXMode = System.getProperty("Stream-Pi.isXMode");
        IS_X_MODE = (isXMode == null) ? IS_X_MODE : isXMode.equals("true");

        String isShowFullScreenToggleButton = System.getProperty("Stream-Pi.isShowFullScreenToggleButton");
        SHOW_FULLSCREEN_TOGGLE_BUTTON = (isShowFullScreenToggleButton == null) ? SHOW_FULLSCREEN_TOGGLE_BUTTON : isShowFullScreenToggleButton.equals("true");

        String defaultFullScreenMode = System.getProperty("Stream-Pi.defaultFullScreenMode");
        DEFAULT_FULLSCREEN_MODE = (defaultFullScreenMode == null) ? DEFAULT_FULLSCREEN_MODE : defaultFullScreenMode.equals("true");

        String enableScreenSaverFeature = System.getProperty("Stream-Pi.enableScreenSaverFeature");
        SCREEN_SAVER_FEATURE = (enableScreenSaverFeature == null) ? SCREEN_SAVER_FEATURE : enableScreenSaverFeature.equals("true");

        String appendPathBeforeRunnerFileToOvercomeJPackageLimitation = System.getProperty("Stream-Pi.appendPathBeforeRunnerFileToOvercomeJPackageLimitation");
        APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION = (appendPathBeforeRunnerFileToOvercomeJPackageLimitation == null) ? APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION : appendPathBeforeRunnerFileToOvercomeJPackageLimitation.equals("true");

        String allowRoot = System.getProperty("Stream-Pi.allowRoot");
        ALLOW_ROOT = (allowRoot == null) ? ALLOW_ROOT : allowRoot.equals("true");

        String setFixedMinSize = System.getProperty("Stream-Pi.setFixedMinSize");
        SET_FIXED_MIN_SIZE = (setFixedMinSize == null) ? SET_FIXED_MIN_SIZE : setFixedMinSize.equals("true");
    }
}
