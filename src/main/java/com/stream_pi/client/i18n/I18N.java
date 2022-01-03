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

package com.stream_pi.client.i18n;

import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.i18n.language.Language;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class I18N
{
    public static Locale BASE_LOCALE = new Locale("en");
    private static ResourceBundle RESOURCE_BUNDLE;
    private static ResourceBundle BASE_RESOURCE_BUNDLE;

    public static void init(Locale locale)
    {
        RESOURCE_BUNDLE = ResourceBundle.getBundle(I18N.class.getPackageName()+".lang", locale);
    }

    public static String getString(String key, Object... args)
    {
        String result;

        if (RESOURCE_BUNDLE.containsKey(key))
        {
            result = RESOURCE_BUNDLE.getString(key);
        }
        else
        {
            if (BASE_RESOURCE_BUNDLE == null)
            {
                BASE_RESOURCE_BUNDLE = ResourceBundle.getBundle(I18N.class.getPackageName()+".lang", new Locale("en"));
            }

            result = BASE_RESOURCE_BUNDLE.getString(key);
        }

        if (args.length == 0)
        {
            return result;
        }
        else
        {
            return String.format(result, args);
        }
    }

    private static List<Language> languages;

    public static void initAvailableLanguages() throws SevereException
    {
        try
        {
            languages = new ArrayList<>();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(I18N.class.getResourceAsStream("i18n.properties")),
                    StandardCharsets.UTF_8
            ));

            while(true)
            {
                String line = bufferedReader.readLine();

                if(line == null)
                {
                    break;
                }
                else if (line.startsWith("#") || line.startsWith("!") || !line.contains("="))
                {
                    continue;
                }


                String[] lineParts = line.split("=");

                String localeStr = lineParts[0].strip();
                String displayName = lineParts[1].strip();

                String[] localeArr = localeStr.split("_");

                Locale locale;

                if(localeArr.length == 1)
                {
                    locale = new Locale(localeArr[0]);
                }
                else if(localeArr.length == 2)
                {
                    locale = new Locale(localeArr[0], localeArr[1]);
                }
                else
                {
                    locale = new Locale(localeArr[0], localeArr[1], localeArr[2]);
                }


                languages.add(new Language(locale, displayName));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new SevereException("Unable to parse initialise i18n.\n"+e.getMessage());
        }
    }

    public static boolean isLanguageAvailable(Locale locale)
    {
        return getLanguage(locale) != null;
    }

    public static Language getLanguage(Locale locale)
    {
        for (Language eachLanguage : languages)
        {
            if(eachLanguage.getLocale().equals(locale))
            {
                return eachLanguage;
            }
        }

        return null;
    }

    public static List<Language> getLanguages()
    {
        return languages;
    }
}
