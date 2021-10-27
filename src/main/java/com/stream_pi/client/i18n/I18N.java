package com.stream_pi.client.i18n;

import com.stream_pi.util.exception.SevereException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    private static ArrayList<Locale> languages;

    public static void initAvailableLanguages() throws SevereException
    {
        try
        {
            languages = new ArrayList<>();

            Files.list(Path.of(Objects.requireNonNull(I18N.class.getResource("lang_en.properties")).toURI()).getParent())
                    .filter(path -> (path.getFileName().toString().startsWith("lang") && path.getFileName().toString().endsWith(".properties")))
                    .sorted().forEach(path ->
                    {
                        String fileName = path.getFileName().toString();
                        String[] localeArr = fileName.substring(0, fileName.lastIndexOf(".")).split("_");

                        Locale locale;

                        if(localeArr.length == 2)
                        {
                            locale = new Locale(localeArr[1]);
                        }
                        else if(localeArr.length == 3)
                        {
                            locale = new Locale(localeArr[1], localeArr[2]);
                        }
                        else
                        {
                            locale = new Locale(localeArr[1], localeArr[2], localeArr[3]);
                        }

                        languages.add(locale);
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

    public static Locale getLanguage(Locale locale)
    {
        for (Locale eachLocale : languages)
        {
            if(eachLocale.equals(locale))
            {
                return eachLocale;
            }
        }

        return null;
    }

    public static List<Locale> getLanguages()
    {
        return languages;
    }
}
