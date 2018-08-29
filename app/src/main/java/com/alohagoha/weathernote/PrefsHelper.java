package com.alohagoha.weathernote;


public interface PrefsHelper {

    String getSharedPreferences(String keyPref, String value);

    void saveSharedPreferences(String keyPref, String value);

    void deleteSharedPreferences(String keyPref);

}
