package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by ganadist on 17. 7. 29.
 */
open class BaseActivity : AppCompatActivity() {
    protected override fun onCreate(bundle: Bundle?) {
        if (getThemeString() == MainActivity.LIGHTTHEME) {
            setTheme(R.style.CustomStyle_LightTheme)
        } else {
            setTheme(R.style.CustomStyle_DarkTheme)
        }
        super.onCreate(bundle)
    }

    fun getThemeString(): String {
        return getPref(MainActivity.THEME_PREFERENCES,
                MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
    }
}

fun Context.getPref(name: String, key: String, def: String ): String {
    return this.getSharedPreferences(name, Context.MODE_PRIVATE).getString(key, def)
}