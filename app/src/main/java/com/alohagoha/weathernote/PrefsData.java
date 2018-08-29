package com.alohagoha.weathernote;



import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PrefsData implements PrefsHelper {
    private SharedPreferences sharedPreferences;

    public PrefsData(BaseActivity baseActivity) {
        this.sharedPreferences = baseActivity.getPreferences(MODE_PRIVATE);
    }

    @Override
    public String getSharedPreferences(String keyPref, String defaultValue) {
        return sharedPreferences.getString(keyPref, defaultValue);
    }

    @Override
    public void saveSharedPreferences(String keyPref, String value) {
        sharedPreferences.edit().putString(keyPref, value).apply();
    }

    @Override
    public void deleteSharedPreferences(String keyPref) {
        sharedPreferences.edit().remove(keyPref).apply();
    }
}
