/*
 * Stream-Pi - Free, Open-Source, Modular, Cross-Platform and Programmable Macro Pad
 * Copyright (C) 2019-2022 Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)
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

import javafx.application.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StartupFlags
{
    public static String RUNNER_FILE_NAME = null;
    public static boolean SHOW_SHUT_DOWN_BUTTON = false;
    public static boolean X_MODE = true;
    public static boolean SCREEN_SAVER_FEATURE= false;
    public static boolean DEFAULT_FULLSCREEN_MODE=false;
    public static boolean SHOW_FULLSCREEN_TOGGLE_BUTTON=true;
    public static boolean APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION = false;
    public static boolean ALLOW_ROOT = false;
    public static boolean SET_FIXED_MIN_SIZE = true;

    private static final String RUNNER_FILE_NAME_ARG = "Stream-Pi.startupRunnerFileName";
    private static final String SHOW_SHUT_DOWN_BUTTON_ARG = "Stream-Pi.showShutDownButton";
    private static final String X_MODE_ARG = "Stream-Pi.xMode";
    private static final String SHOW_FULLSCREEN_TOGGLE_BUTTON_ARG = "Stream-Pi.showFullScreenToggleButton";
    private static final String DEFAULT_FULLSCREEN_MODE_ARG = "Stream-Pi.defaultFullScreenMode";
    private static final String SCREEN_SAVER_FEATURE_ARG = "Stream-Pi.screenSaverFeature";
    private static final String APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION_ARG = "Stream-Pi.appendPathBeforeRunnerFileToOvercomeJPackageLimitation";
    private static final String ALLOW_ROOT_ARG = "Stream-Pi.allowRoot";
    private static final String SET_FIXED_MIN_SIZE_ARG = "Stream-Pi.setFixedMinSize";

    public static void init(Application.Parameters parameters)
    {
        for (String arg : parameters.getRaw())
        {
            String[] arr = arg.split("=");

            if (arr.length == 2)
            {
                String val = arr[1].strip();
                switch(arr[0])
                {
                    case RUNNER_FILE_NAME_ARG: RUNNER_FILE_NAME = val; break;
                    case SHOW_SHUT_DOWN_BUTTON_ARG: SHOW_SHUT_DOWN_BUTTON = val.equals("true"); break;
                    case X_MODE_ARG: X_MODE = val.equals("true"); break;
                    case SHOW_FULLSCREEN_TOGGLE_BUTTON_ARG: SHOW_FULLSCREEN_TOGGLE_BUTTON = val.equals("true"); break;
                    case DEFAULT_FULLSCREEN_MODE_ARG: DEFAULT_FULLSCREEN_MODE = val.equals("true"); break;
                    case SCREEN_SAVER_FEATURE_ARG: SCREEN_SAVER_FEATURE = val.equals("true"); break;
                    case APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION_ARG: APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION = val.equals("true"); break;
                    case ALLOW_ROOT_ARG: ALLOW_ROOT = val.equals("true"); break;
                    case SET_FIXED_MIN_SIZE_ARG: SET_FIXED_MIN_SIZE = val.equals("true"); break;
                }
            }
        }
    }

    public static String[] generateRuntimeArgumentsForStartOnBoot()
    {
        List<String> arrayList = new ArrayList<>();

        if (RUNNER_FILE_NAME!=null)
        {
            arrayList.add(RUNNER_FILE_NAME_ARG+"='"+RUNNER_FILE_NAME+"'");
        }

        arrayList.add(SHOW_SHUT_DOWN_BUTTON_ARG+"="+SHOW_SHUT_DOWN_BUTTON);
        arrayList.add(X_MODE_ARG+"="+X_MODE);
        arrayList.add(SHOW_FULLSCREEN_TOGGLE_BUTTON_ARG+"="+SHOW_FULLSCREEN_TOGGLE_BUTTON);
        arrayList.add(DEFAULT_FULLSCREEN_MODE_ARG+"="+DEFAULT_FULLSCREEN_MODE);
        arrayList.add(SCREEN_SAVER_FEATURE_ARG+"="+SCREEN_SAVER_FEATURE);
        arrayList.add(APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION_ARG+"="+APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION);
        arrayList.add(ALLOW_ROOT_ARG+"="+StartupFlags.ALLOW_ROOT);
        arrayList.add(SET_FIXED_MIN_SIZE_ARG+"="+StartupFlags.SET_FIXED_MIN_SIZE);

        return arrayList.toArray(new String[0]);
    }
}
