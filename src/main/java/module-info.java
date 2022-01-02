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

module com.stream_pi.client
{
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.kordamp.ikonli.javafx;

    requires com.gluonhq.attach.lifecycle;
    requires com.gluonhq.attach.util;
    requires com.gluonhq.attach.storage;
    requires com.gluonhq.attach.browser;
    requires com.gluonhq.attach.vibration;
    requires com.gluonhq.attach.orientation;

    requires eu.hansolo.medusa;

    requires java.management;

    requires java.xml;

    opens com.stream_pi.client.window.settings.about to javafx.base;

    requires com.stream_pi.util;
    requires com.stream_pi.theme_api;
    requires com.stream_pi.action_api;

    requires org.kordamp.ikonli.fontawesome5;

    requires org.controlsfx.controls;

    exports com.stream_pi.client;
}