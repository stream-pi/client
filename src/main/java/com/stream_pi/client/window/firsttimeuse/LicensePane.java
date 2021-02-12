package com.stream_pi.client.window.firsttimeuse;

import com.stream_pi.client.info.License;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LicensePane extends VBox {
    public LicensePane()
    {
        getStyleClass().add("first_time_use_pane_license");

        TextArea licenseTextArea = new TextArea(License.getLicense());
        licenseTextArea.setWrapText(false);
        licenseTextArea.setEditable(false);

        licenseTextArea.prefWidthProperty().bind(widthProperty());
        VBox.setVgrow(licenseTextArea, Priority.ALWAYS);

        getChildren().addAll(licenseTextArea);
    }
}
