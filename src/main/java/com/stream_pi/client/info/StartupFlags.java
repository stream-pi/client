// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.info;

public class StartupFlags
{
    public static String RUNNER_FILE_NAME;
    public static boolean IS_SHOW_SHUT_DOWN_BUTTON;
    public static boolean IS_X_MODE;
    public static boolean SCREEN_SAVER_FEATURE;
    public static boolean DEFAULT_FULLSCREEN_MODE;
    public static boolean SHOW_FULLSCREEN_TOGGLE_BUTTON;
    public static boolean APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION;
    
    static {
        StartupFlags.RUNNER_FILE_NAME = null;
        StartupFlags.IS_SHOW_SHUT_DOWN_BUTTON = false;
        StartupFlags.IS_X_MODE = true;
        StartupFlags.SCREEN_SAVER_FEATURE = false;
        StartupFlags.DEFAULT_FULLSCREEN_MODE = false;
        StartupFlags.SHOW_FULLSCREEN_TOGGLE_BUTTON = true;
        StartupFlags.APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION = false;
    }
}
