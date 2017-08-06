package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceFragment

/**
 * Created by hardyeats on 2017-07-22.
 */

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_layout)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preferenceKeys = PreferenceKeys(resources)
        if(key == preferenceKeys.night_mode_pref_key) {
            val themePreferences = activity.getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE)
            themePreferences.edit().apply {
                //We tell our MainLayout to recreate itself because mode has changed
                putBoolean(MainActivity.RECREATE_ACTIVITY, true)
                val checkBoxPreference  = findPreference(preferenceKeys.night_mode_pref_key) as CheckBoxPreference
                if(checkBoxPreference.isChecked)
                    putString(MainActivity.THEME_SAVED, MainActivity.DARKTHEME)
                else
                    putString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
                apply()
            }
            activity.recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}