package com.stream_pi.client.combobox;

import com.stream_pi.client.i18n.I18N;
import com.stream_pi.util.combobox.StreamPiComboBox;
import com.stream_pi.util.combobox.StreamPiComboBoxFactory;
import com.stream_pi.util.i18n.language.Language;

import java.util.Locale;

public class LanguageChooserComboBox extends StreamPiComboBox<Locale>
{
    public LanguageChooserComboBox()
    {
        setStreamPiComboBoxFactory(new StreamPiComboBoxFactory<>() {
            @Override
            public String getOptionDisplayText(Locale locale) {
                return locale.getDisplayName();
            }
        });

        setOptions(I18N.getLanguages());

    }

    public Locale getSelectedLocale()
    {
        return getCurrentSelectedItem();
    }
}
