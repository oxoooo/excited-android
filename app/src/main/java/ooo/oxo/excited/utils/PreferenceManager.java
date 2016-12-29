package ooo.oxo.excited.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zsj on 2016/11/7.
 */

public class PreferenceManager {

    private static final String PREFS_NAME = "aja";

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return preferences(context).edit();
    }

    public static void putString(Context context, String key, String value) {
        edit(context).putString(key, value).apply();
    }

    public static String getValue(Context context, String key) {
        return preferences(context).getString(key, null);
    }

    public static String getValue(Context context, String key, String defaultValue) {
        return preferences(context).getString(key, defaultValue);
    }
}
