// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.window.firsttimeuse;

import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import com.stream_pi.client.info.License;
import javafx.scene.layout.VBox;

public class LicensePane extends VBox
{
    public LicensePane() {
        this.getStyleClass().add((Object)"first_time_use_pane_license");
        final TextArea licenseTextArea = new TextArea(License.getLicense());
        licenseTextArea.setWrapText(false);
        licenseTextArea.setEditable(false);
        licenseTextArea.prefWidthProperty().bind((ObservableValue)this.widthProperty());
        VBox.setVgrow((Node)licenseTextArea, Priority.ALWAYS);
        this.getChildren().addAll((Object[])new Node[] { (Node)licenseTextArea });
    }
}
