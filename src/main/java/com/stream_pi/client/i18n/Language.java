package com.stream_pi.client.i18n;

import java.util.Locale;

public class Language
{
    private final String fullName;
    private final Locale locale;

    public Language(String fullName, Locale locale)
    {
        this.fullName = fullName;
        this.locale = locale;
    }

    public String getFullName()
    {
        return fullName;
    }

    public Locale getLocale()
    {
        return locale;
    }
}
