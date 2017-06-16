package hakito.carclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
    private static final String PREF_INTERVAL = "interval";
    private static final String PREF_ADDRESS = "address";
    private static final String PREF_LEFT = "left";
    private static final String PREF_RIGHT = "right";
    private static final String PREF_LIGHT = "light";

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

    public int getLeft() {
        return preferences.getInt(PREF_LEFT, 80);
    }

    public void setLeft(int left) {
        putInt(PREF_LEFT, left);
    }

    public int getRight() {
        return preferences.getInt(PREF_RIGHT, 100);
    }

    public void setRight(int right) {
        putInt(PREF_RIGHT, right);
    }

    public int getInterval() {
        return preferences.getInt(PREF_INTERVAL, 100);
    }

    public void setInterval(int interval) {
        putInt(PREF_INTERVAL, interval);
    }

    public String getAddress() {
        return preferences.getString(PREF_ADDRESS, "192.168.4.1:81");
    }

    public void setAddress(String address) {
        putString(PREF_ADDRESS, address);
    }

    public int getLight() {
        return preferences.getInt(PREF_LIGHT, 0);
    }

    public void setLight(int light) {
        putInt(PREF_LIGHT, light);
    }
}
