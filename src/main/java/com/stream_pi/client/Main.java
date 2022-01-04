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

package com.stream_pi.client;

import com.stream_pi.client.controller.Controller;
import com.stream_pi.client.info.ClientInfo;

import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.util.exception.GlobalUncaughtExceptionHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage stage)
    {
        StartupFlags.init(getParameters());
        GlobalUncaughtExceptionHandler.init();
        Controller d = new Controller();
        Scene s = new Scene(d);
        stage.setScene(s);
        d.setHostServices(getHostServices());
        d.init();
    }


    public static void main(String[] args) 
    {
        launch(args);
    }
}
