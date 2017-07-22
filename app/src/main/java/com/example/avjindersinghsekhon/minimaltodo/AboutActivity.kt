package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context;
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.TextView

class AboutActivity : BaseActivity() {
    private var mVersionTextView: TextView? = null
    private var appVersion = "0.1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_layout)

        val i = intent
        //        mId = (UUID)i.getSerializableExtra(TodoNotificationService.TODOUUID);

        val backArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            appVersion = info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mVersionTextView = findViewById(R.id.aboutVersionTextView) as TextView
        mVersionTextView!!.text = String.format(resources.getString(R.string.app_version), appVersion)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val contactMe = findViewById(R.id.aboutContactMe) as TextView

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setHomeAsUpIndicator(backArrow)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
