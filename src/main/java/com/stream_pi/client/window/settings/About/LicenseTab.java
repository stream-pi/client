// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.window.settings.About;

import com.stream_pi.client.info.License;
import javafx.scene.control.TextArea;

public class LicenseTab extends TextArea
{
    public LicenseTab() {
        this.setText(License.getLicense());
        this.getStyleClass().add((Object)"about_license_text_area");
        this.setWrapText(false);
        this.setEditable(false);
    }
}
