package com.example.avjindersinghsekhon.minimaltodo

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.about_layout.*

/**
 * Created by hardyeats on 2017-07-22.
 */
class AboutActivity : AppCompatActivity() {
    val appVersion: String by lazy {
        try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch(e: Exception) {
            e.printStackTrace()
            "0.1"
        }
    }

    val theme: String by lazy {
        getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
    }

    val toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(theme == MainActivity.DARKTHEME) {
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_DarkTheme)
        } else {
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_LightTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_layout)

        val backArrow : Drawable? = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

        aboutVersionTextView.text = String.format(getString(R.string.app_version), appVersion)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(backArrow)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when(item?.itemId) {
        R.id.home -> consume { NavUtils.navigateUpFromSameTask(this) }
        else -> super.onOptionsItemSelected(item)
    }

    inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

}