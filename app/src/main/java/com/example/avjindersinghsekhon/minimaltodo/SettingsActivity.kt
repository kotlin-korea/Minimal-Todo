package com.example.avjindersinghsekhon.minimaltodo

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by hardyeats on 2017-07-22.
 */

class SettingsActivity : AppCompatActivity() {

    override fun onResume() { super.onResume() }

    override fun onCreate(savedInstanceState: Bundle?) {

        val theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
        if(theme == MainActivity.LIGHTTHEME) {
            setTheme(R.style.CustomStyle_LightTheme)
        } else {
            setTheme(R.style.CustomStyle_DarkTheme)
        }
        super.onCreate(savedInstanceState)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        val backArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(backArrow)

        fragmentManager.beginTransaction().replace(R.id.mycontent, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if(NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}