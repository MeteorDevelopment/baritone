/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.api.utils;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class BaritoneI18n {

    private static final String LANG_RESOURCE_PATH = "/baritone/lang/";
    private static final String DEFAULT_LANG = "en_us";
    private static final Map<String, Properties> langCache = new HashMap<>();

    private BaritoneI18n() {}

    public static String getCurrentLang() {
        try {
            Settings settings = BaritoneAPI.getSettings();
            if (settings != null && settings.locale != null && settings.locale.value != null) {
                String lang = settings.locale.value;
                if (!lang.isEmpty()) {
                    return lang;
                }
            }
        } catch (Throwable ignored) {
        }
        return DEFAULT_LANG;
    }

    public static String translate(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        String lang = getCurrentLang();
        String result = tryTranslate(lang, key);
        if (result == null && !lang.equalsIgnoreCase(DEFAULT_LANG)) {
            result = tryTranslate(DEFAULT_LANG, key);
        }
        return result != null ? result : key;
    }

    public static String translate(String key, Object... args) {
        return String.format(translate(key), args);
    }

    public static String translateSettingDescription(String settingName) {
        return translate("setting." + settingName);
    }

    public static boolean hasTranslation(String key) {
        String lang = getCurrentLang();
        if (tryTranslate(lang, key) != null) {
            return true;
        }
        return !lang.equalsIgnoreCase(DEFAULT_LANG) && tryTranslate(DEFAULT_LANG, key) != null;
    }

    private static String tryTranslate(String lang, String key) {
        if (lang == null || lang.isEmpty()) {
            return null;
        }
        Properties props = langCache.get(lang);
        if (props == null) {
            props = loadLang(lang);
            if (props == null) {
                return null;
            }
            langCache.put(lang, props);
        }
        return props.getProperty(key);
    }

    private static Properties loadLang(String lang) {
        String path = LANG_RESOURCE_PATH + lang + ".lang";
        InputStream is = BaritoneI18n.class.getResourceAsStream(path);
        if (is == null) {
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(reader);
            return props;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void clearCache() {
        langCache.clear();
    }
}
