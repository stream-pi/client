package com.stream_pi.client.window.settings.About;


import com.stream_pi.client.info.License;
import javafx.scene.control.TextArea;

public class LicenseTab extends TextArea
{
    public LicenseTab()
    {
        setText(License.getLicense());
        getStyleClass().add("about_license_text_area");
        setWrapText(false);
        setEditable(false);
    }
}
