package com.stream_pi.client.combobox;

import com.stream_pi.client.i18n.I18N;
import com.stream_pi.util.combobox.StreamPiComboBox;
import com.stream_pi.util.combobox.StreamPiComboBoxFactory;
import com.stream_pi.util.i18n.language.Language;

import java.util.Locale;

public class LanguageChooserComboBox extends StreamPiComboBox<Language>
{
    public LanguageChooserComboBox()
    {
        setStreamPiComboBoxFactory(new StreamPiComboBoxFactory<>() {
            @Override
            public String getOptionDisplayText(Language language) {
                return language.getFullName();
            }
        });

        setOptions(I18N.getLanguages());

    }

    public Locale getSelectedLocale()
    {
        return getCurrentSelectedItem().getLocale();
    }
}
