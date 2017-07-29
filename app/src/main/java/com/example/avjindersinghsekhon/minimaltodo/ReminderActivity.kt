package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.example.avjindersinghsekhon.minimaltodo.R.array.snooze_options
import com.example.avjindersinghsekhon.minimaltodo.R.color.secondary_text
import com.example.avjindersinghsekhon.minimaltodo.R.drawable.ic_snooze_white_24dp
import com.example.avjindersinghsekhon.minimaltodo.R.id.toDoReminderDoneMenuItem
import com.example.avjindersinghsekhon.minimaltodo.R.layout.*
import com.example.avjindersinghsekhon.minimaltodo.R.menu.menu_reminder
import com.example.avjindersinghsekhon.minimaltodo.R.style.CustomStyle_DarkTheme
import com.example.avjindersinghsekhon.minimaltodo.R.style.CustomStyle_LightTheme
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.reminder_layout.*
import java.util.*

/**
 * Created by yongju on 2017. 7. 29..
 */
class ReminderActivity : AppCompatActivity() {

    private lateinit var storeRetrieveData: StoreRetrieveData
    private lateinit var toDoItems: ArrayList<ToDoItem>
    private lateinit var item: ToDoItem
    private lateinit var snoozeOptionsArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
        when(theme) {
            MainActivity.LIGHTTHEME -> setTheme(CustomStyle_LightTheme)
            else -> setTheme(CustomStyle_DarkTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(reminder_layout)

        storeRetrieveData = StoreRetrieveData(this, MainActivity.FILENAME)
        toDoItems = MainActivity.getLocallyStoredData(storeRetrieveData)

        setSupportActionBar(toolbar)

        val id = intent.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID
        toDoItems.first {
            it.identifier == id
        }.let {
            item = it
        }

        snoozeOptionsArray = resources.getStringArray(snooze_options)
        toDoReminderTextViewBody.text = item.toDoText

        when(theme) {
            MainActivity.LIGHTTHEME -> toDoReminderTextViewBody.setTextColor(resources.getColor(secondary_text))
            else -> toDoReminderTextViewBody.run {
                setTextColor(Color.WHITE)
                setCompoundDrawablesWithIntrinsicBounds(ic_snooze_white_24dp, 0, 0, 0)
            }
        }

        toDoReminderRemoveButton.setOnClickListener {
            toDoItems.remove(item)
            changeOccurred()
            saveData()
            closeApp()
        }

        todoReminderSnoozeSpinner.adapter = ArrayAdapter<String>(this, spinner_text_view, snoozeOptionsArray).apply {
            setDropDownViewResource(spinner_dropdown_item)
        }
    }

    private fun closeApp() {
        getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE).edit().apply {
            putBoolean(EXIT, true)
        }.apply()

        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(menu_reminder, menu)
        return true
    }

    private fun changeOccurred() {
        getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE).edit().apply {
            putBoolean(MainActivity.CHANGE_OCCURED, true)
        }.apply()
    }

    private fun addTimeToDate(mins: Int): Date
        = Calendar.getInstance().apply {
            time = Date()
            add(Calendar.MINUTE, mins)
        }.time

    private fun valueFromSpinner(): Int =
        when(todoReminderSnoozeSpinner.selectedItemPosition) {
            0 -> 10
            1 -> 30
            2 -> 60
            else -> 0
        }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            toDoReminderDoneMenuItem -> {
                this.item.run {
                    toDoDate = addTimeToDate(valueFromSpinner())
                    setHasReminder(true)
                    changeOccurred()
                    saveData()
                    closeApp()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    private fun saveData() {
        try {
            storeRetrieveData.saveToFile(toDoItems)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val EXIT = "com.avjindersekhon.exit"
    }
}