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

package com.stream_pi.client.window.firsttimeuse;

import com.stream_pi.client.info.ClientInfo;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LicensePane extends VBox {
    public LicensePane()
    {
        getStyleClass().add("first_time_use_pane_license");

        TextArea licenseTextArea = new TextArea(ClientInfo.getInstance().getLicense());
        licenseTextArea.setWrapText(false);
        licenseTextArea.setEditable(false);

        licenseTextArea.prefWidthProperty().bind(widthProperty());
        VBox.setVgrow(licenseTextArea, Priority.ALWAYS);

        getChildren().addAll(licenseTextArea);
    }
}
