package com.stream_pi.client.i18n;

import com.stream_pi.util.exception.SevereException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.stream_pi.util.i18n.language.Language;

public class I18N
{
    public static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(I18N.class.getPackageName()+".lang");

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

    private static HashMap<Locale, Language> languages;

    public static void initAvailableLanguages() throws SevereException
    {
        try
        {

            languages = new HashMap<>();

            InputStream inputStream = I18N.class.getResourceAsStream("i18n.properties");
            if (inputStream != null)
            {
                Properties properties = new Properties();
                properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                inputStream.close();


                for (String key : properties.stringPropertyNames())
                {
                    String fullName = properties.getProperty(key);

                    if (!key.isBlank() && !fullName.isBlank())
                    {
                        Locale locale = Locale.forLanguageTag(key);
                        languages.put(locale, new Language(fullName, locale));
                    }
                }
            }
            else
            {
                throw new SevereException("Unable to open i18n.properties file.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new SevereException("Unable to open i18n.properties file.\n"+e.getMessage());
        }
    }

    public static boolean isLanguageAvailable(Locale locale)
    {
        return getLanguage(locale) != null;
    }

    public static Language getLanguage(Locale locale)
    {
        return languages.getOrDefault(locale, null);
    }

    public static List<Language> getLanguages()
    {
        return new ArrayList<>(languages.values());
    }
}
