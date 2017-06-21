package hakito.carclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
    private static final String PREF_INTERVAL = "interval";
    private static final String PREF_LEFT = "left";
    private static final String PREF_RIGHT = "right";
    private static final String PREF_LIGHT = "light";
    private static final String PREF_THROTTLE_LIMIT = "throttle_limit";

    private SharedPreferences preferences;

    public PreferenceHelper(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void putString(String key, String s) {
        preferences.edit()
                .putString(key, s)
                .apply();
    }

    private void putInt(String key, int i) {
        preferences.edit()
                .putInt(key, i)
                .apply();
    }

    private int  getAsInt(String key, int defaultVal){
        return Integer.valueOf(preferences.getString(key, String.valueOf(defaultVal)));
    }

    public int getLeft() {
        return getAsInt(PREF_LEFT, 80);
    }



    public int getRight() {
        return getAsInt(PREF_RIGHT, 100);
    }



    public int getInterval() {
        return getAsInt(PREF_INTERVAL, 100);
    }



    public int getLight() {
        return preferences.getInt(PREF_LIGHT, 0);
    }

    public void setLight(int light) {
        putInt(PREF_LIGHT, light);
    }

    public boolean isThrottleLimited() {
        return preferences.getBoolean(PREF_THROTTLE_LIMIT, true);
    }
}
