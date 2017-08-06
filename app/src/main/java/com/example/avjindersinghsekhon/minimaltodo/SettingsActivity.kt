package com.example.avjindersinghsekhon.minimaltodo

import android.app.FragmentManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by patternoid on 2017. 8. 4..
 */
class SettingsActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        val theme : String = getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
        if(theme.equals(MainActivity.LIGHTTHEME)){
            setTheme(R.style.CustomStyle_LightTheme)
        }else{
            setTheme(R.style.CustomStyle_DarkTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        val backArrow : Drawable? = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow?.let{
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(backArrow)
        }

        fragmentManager.beginTransaction().replace(
                R.id.mycontent,
                SettingsFragment()
        ).commit()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when( item?.itemId ){
            android.R.id.home -> {
                NavUtils.getParentActivityName(this@SettingsActivity)?.let{
                    NavUtils.navigateUpFromSameTask(this@SettingsActivity)
                }
                return true
            }
            else ->  return super.onOptionsItemSelected(item)
        }
    }
}