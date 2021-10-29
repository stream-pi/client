package com.stream_pi.client.i18n;

import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.i18n.Language;

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
    private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(I18N.class.getPackageName()+".lang");


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
            result = key;
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


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(I18N.class.getResourceAsStream("i18n.properties"))));

            bufferedReader.lines().forEachOrdered(line->
            {
                if(!line.startsWith("#") && !line.startsWith("!") && line.contains("="))
                {
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


            });
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
