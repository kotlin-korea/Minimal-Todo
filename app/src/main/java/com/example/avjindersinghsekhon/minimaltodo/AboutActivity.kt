package com.example.avjindersinghsekhon.minimaltodo

import android.content.pm.PackageInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by patternoid on 2017. 8. 3..
 */
class AboutActivity : AppCompatActivity(){

    private var appVersion  : String = "0.1"

    var theme       : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)

        if( theme.equals(MainActivity.DARKTHEME) ){
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_DarkTheme)
        }
        else{
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_LightTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_layout)

        val backArrow : Drawable? = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow?.let{
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }

        try{
            val info : PackageInfo = packageManager.getPackageInfo(packageName, 0)
            appVersion = info.versionName
        } catch( e : Exception ){
            e.printStackTrace()
        }


        setSupportActionBar( toolbar )
        supportActionBar?.let{
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(backArrow)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when( item?.itemId ){
            android.R.id.home ->{
                NavUtils.getParentActivityName(this@AboutActivity)?.let{
                    NavUtils.navigateUpFromSameTask(this@AboutActivity)
                }
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}