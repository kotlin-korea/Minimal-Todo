package com.example.avjindersinghsekhon.minimaltodo

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_todo_test.*
import kotlinx.android.synthetic.main.base_toolbar.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hardyeats on 2017-07-22.
 */

class AddToDoActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private val mUserToDoItem: ToDoItem by lazy {
        intent.getSerializableExtra(MainActivity.TODOITEM) as ToDoItem
    }
    private lateinit var mUserEnteredText: String
    private var mUserHasReminder: Boolean = false
    private var mUserReminderDate: Date? = null
    private var mUserColor: Int = 0

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {

        val theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE)
                .getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)

        when(theme) {
            MainActivity.LIGHTTHEME -> {
                setTheme(R.style.CustomStyle_LightTheme)
                Log.d("OskarSchindler", "Light Theme")
            }
            else -> {
                setTheme(R.style.CustomStyle_DarkTheme)
                userToDoReminderIconImageButton?.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_add_white_24dp))
                userToDoRemindMeTextView?.setTextColor(Color.WHITE)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_test)

        //Show an X in place of <-
        val cross = resources.getDrawable(R.drawable.ic_clear_white_24dp)?.apply {
            setColorFilter(resources.getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            elevation = 0f
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(cross)

        }

        mUserToDoItem.let {
            mUserEnteredText = it.toDoText
            mUserHasReminder = it.hasReminder()
            mUserReminderDate = it.toDoDate
            mUserColor = it.todoColor
        }

        todoReminderAndDateContainerLayout.setOnClickListener { hideKeyboard(userToDoEditText) }

        mUserReminderDate?.let {
            if(mUserHasReminder) {
                setReminderTextView()
                setEnterDateLayoutVisibleWithAnimations(true)
            }
        } ?: let {
            toDoHasDateSwitchCompat.isChecked = false
            newToDoDateTimeReminderTextView.visibility = View.INVISIBLE
        }

        userToDoEditText.requestFocus()
        userToDoEditText.setText(mUserEnteredText)
        showKeyboard()
        userToDoEditText.setSelection(userToDoEditText.length())

        userToDoEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { mUserEnteredText = s.toString() }
            override fun afterTextChanged(s: Editable?) {}
        })

        setEnterDateLayoutVisible(toDoHasDateSwitchCompat.isChecked)
        toDoHasDateSwitchCompat.isChecked = mUserHasReminder && mUserReminderDate != null
        toDoHasDateSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            if(!isChecked) {
                mUserReminderDate = null
            }
            mUserHasReminder = isChecked
            setDateAndTimeEditText()
            setEnterDateLayoutVisibleWithAnimations(isChecked)
            hideKeyboard(userToDoEditText)
        }

        makeToDoFloatingActionButton.setOnClickListener {
            if(userToDoEditText.length() <= 0) {
                userToDoEditText.error = getString(R.string.todo_error)
            } else if(mUserReminderDate != null && mUserReminderDate!!.before(Date())) {
                makeResult(RESULT_CANCELED)
            } else {
                makeResult(RESULT_OK)
                finish()
            }
            hideKeyboard(userToDoEditText)
        }

        newTodoDateEditText.setOnClickListener {
            hideKeyboard(userToDoEditText)
            var date = Date()
            mUserToDoItem.toDoDate?.let { date = mUserReminderDate!! }
            val calendar = Calendar.getInstance().apply { time = date }
            val datePickerDialog = DatePickerDialog.newInstance(this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
            if(theme == MainActivity.DARKTHEME) {
                datePickerDialog.isThemeDark = true
            }
            datePickerDialog.show(fragmentManager, "DateFragment")
        }

        newTodoTimeEditText.setOnClickListener {
            hideKeyboard(userToDoEditText)
            var date = Date()
            mUserToDoItem.toDoDate?.let { date = it }

            val calendar = Calendar.getInstance().apply { time = date }
            val timePickerDialog = TimePickerDialog.newInstance(this,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(this))
            if (theme == MainActivity.DARKTHEME) {
                timePickerDialog.isThemeDark = true
            }
            timePickerDialog.show(fragmentManager, "TimeFragment")
        }

        setDateAndTimeEditText()

    } // onCreate()

    private fun setDateAndTimeEditText() {
        if(mUserHasReminder) {
            mUserReminderDate?.let {
                val userDate = formatDate("d MMM, yyyy", it)
                val formatToUse =
                        if (DateFormat.is24HourFormat(this)) "k:mm"
                        else "h:mm a"
                val userTime = formatDate(formatToUse, it)
                newTodoDateEditText.setText(userDate)
                newTodoTimeEditText.setText(userTime)
            }
        }
        else {
            newTodoDateEditText.setText(getString(R.string.date_reminder_default))
            val time24 = DateFormat.is24HourFormat(this)
            val cal = Calendar.getInstance()
            if(time24) {
                cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1)
            } else {
                cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1)
            }
            cal.set(Calendar.MINUTE, 0)
            mUserReminderDate = cal.time
            mUserReminderDate?.let {
                Log.d("OskarSchindler", "Imagined Date: " + it)
                val timeString =
                        if(time24) formatDate("k:mm", it)
                        else formatDate("h:mm a", it)
                newTodoTimeEditText.setText(timeString)
            }
        }
    }

    fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboard(et: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et.windowToken, 0)
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val hour: Int
        val minute: Int

        val reminderCalendar = Calendar.getInstance().apply { set(year, month, day) }

        if(reminderCalendar.before(calendar)) {
            Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show()
            return
        }

        mUserReminderDate?.let { calendar.time = it }

        when(DateFormat.is24HourFormat(this)) {
            true -> hour = calendar.get(Calendar.HOUR_OF_DAY)
            else -> hour = calendar.get(Calendar.HOUR)
        }
        minute = calendar.get(Calendar.MINUTE)

        calendar.set(year, month, day, hour, minute)
        mUserReminderDate = calendar.time
        setReminderTextView()
        setDateEditText()
    }


    fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        mUserReminderDate?.let { calendar.time = it }

        Log.d("OskarSchindler", "Time set: " + hour)
        calendar.set(Calendar.HOUR, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        mUserReminderDate = calendar.time

        setReminderTextView()
        setTimeEditView()
    }

    fun setDateEditText() {
        newTodoDateEditText.setText(formatDate("d MM yyyy", mUserReminderDate!!))
    }

    fun setTimeEditView() {
        val dateFormat =
                if (DateFormat.is24HourFormat(this)) "k:mm"
                else "h:mm a"
        newTodoTimeEditText.setText(formatDate(dateFormat, mUserReminderDate!!))
    }

    fun setReminderTextView() {
        mUserReminderDate?.let {
            newToDoDateTimeReminderTextView.visibility = View.VISIBLE
            if(mUserReminderDate!!.before(Date())) {
                Log.d("OskarSchindler", "DATE is " + mUserReminderDate)
                newToDoDateTimeReminderTextView.text = getString(R.string.date_error_check_again)
                newToDoDateTimeReminderTextView.setTextColor(Color.RED)
                return
            }

            val dateString = formatDate("d MMM yyyy", mUserReminderDate!!)
            val timeString =
                    if(DateFormat.is24HourFormat(this)) formatDate("k:mm", mUserReminderDate!!)
                    else formatDate("h:mm", mUserReminderDate!!)
            val amPmString =
                    if(DateFormat.is24HourFormat(this)) ""
                    else formatDate("a", mUserReminderDate!!)
            newToDoDateTimeReminderTextView.setTextColor(ContextCompat.getColor(this, R.color.secondary_text))
            newToDoDateTimeReminderTextView.text = getString(R.string.remind_date_and_time).format(dateString, timeString, amPmString)
        } ?:let { newToDoDateTimeReminderTextView.visibility = View.INVISIBLE }
    }

    fun makeResult(result: Int) {

        if(mUserEnteredText.isNotEmpty())
            mUserToDoItem.toDoText = mUserEnteredText.capitalize()
        else
            mUserToDoItem.toDoText = mUserEnteredText

        mUserReminderDate?.let { it.seconds = 0 }
        mUserToDoItem.let {
            it.setHasReminder(mUserHasReminder)
            it.toDoDate = mUserReminderDate
            it.todoColor = mUserColor
        }
        setResult(result, Intent().putExtra(MainActivity.TODOITEM, mUserToDoItem))
    }

    override fun onBackPressed() {
        mUserReminderDate?.let { if(it.before(Date())) { mUserToDoItem.toDoDate = null } }
        makeResult(RESULT_OK)
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                NavUtils.getParentActivityName(this)?.let {
                    makeResult(RESULT_CANCELED)
                    NavUtils.navigateUpFromSameTask(this)
                }
                hideKeyboard(userToDoEditText)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        @JvmStatic
        fun formatDate(formatString: String, dateToFormat: Date): String {
            return SimpleDateFormat(formatString).format(dateToFormat)
        }
    }

    override fun onTimeSet(view: RadialPickerLayout?, hour: Int, minute: Int) {
        setTime(hour, minute)
    }


    override fun onDateSet(view: DatePickerDialog?, year: Int, month: Int, day: Int) {
        setDate(year, month, day)
    }

    fun setEnterDateLayoutVisible(checked: Boolean) {
        if(checked) {
            toDoEnterDateLinearLayout.visibility = View.VISIBLE
        } else {
            toDoEnterDateLinearLayout.visibility = View.INVISIBLE
        }
    }

    fun setEnterDateLayoutVisibleWithAnimations(checked: Boolean) {
        if(checked) {
            toDoEnterDateLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(object: Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) { toDoEnterDateLinearLayout.visibility = View.VISIBLE }
                override fun onAnimationEnd(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}
            })
        } else {
            toDoEnterDateLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(object: Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) { toDoEnterDateLinearLayout.visibility = View.INVISIBLE }
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}
            })
        }
    }
}