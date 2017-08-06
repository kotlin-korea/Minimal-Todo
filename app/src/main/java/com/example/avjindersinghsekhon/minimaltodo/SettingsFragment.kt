package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceFragment

/**
 * Created by patternoid on 2017. 8. 4..
 */
class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_layout)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        val preferenceKeys = PreferenceKeys(resources)

        if( key.equals(preferenceKeys.night_mode_pref_key)){

            var themeEditor : SharedPreferences.Editor?

            activity.getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE).apply {

                themeEditor  = edit()
                themeEditor?.putBoolean(MainActivity.RECREATE_ACTIVITY, true)

                val checkBoxPreference : CheckBoxPreference = findPreference(preferenceKeys.night_mode_pref_key) as CheckBoxPreference

                if( checkBoxPreference.isChecked ){
                    themeEditor?.putString(MainActivity.THEME_SAVED, MainActivity.DARKTHEME)
                }else{
                    themeEditor?.putString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
                }

                themeEditor?.apply()
            }

            activity.recreate()
        }
    }


    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this@SettingsFragment)
    }


    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this@SettingsFragment)
    }
}