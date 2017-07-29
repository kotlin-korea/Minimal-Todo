package reminder

import android.content.Context
import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import com.example.avjindersinghsekhon.minimaltodo.MainActivity
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.StoreRetrieveData
import com.example.avjindersinghsekhon.minimaltodo.TodoNotificationService
import data.ToDoItem
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.reminder_layout.*
import org.json.JSONException
import java.io.IOException

/**
 * Created by jeonghyeonji on 2017. 7. 22..
 */
class ReminderActivity : AppCompatActivity() {

    private val mtoDoTextTextView: TextView by lazy { toDoReminderTextViewBody }
    private val mRdemoveToDoButton: Button by lazy { toDoReminderRemoveButton }
    private val mSnoozeSpinner: MaterialSpinner by lazy { todoReminderSnoozeSpinner }
    private val mSnoozeTextView: TextView by lazy { reminderViewSnoozeTextView }
    private val storeRetrieveData: StoreRetrieveData by lazy { StoreRetrieveData(this, MainActivity.FILENAME) }
    private val snoozeOptionsArray: Array<String> by lazy { resources.getStringArray(R.array.snooze_options) }

    lateinit var mItem: com.example.avjindersinghsekhon.minimaltodo.ToDoItem
    lateinit var theme: String
    //lateinit var snoozeOptionsArray : Array<String>

    //private val mToDoItems : ArrayList<ToDoItem> by lazy { ToDoItem } 이걸로 할랬는데 아직 다 안바껴서 그런가여.ㅠ
    lateinit var mToDoItems: ArrayList<com.example.avjindersinghsekhon.minimaltodo.ToDoItem>


    companion object {
        val EXIT = "com.avjindersekhon.exit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 음.... 테마 넣기... 음....찜찜
        theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE)
                .getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)

        if (theme == MainActivity.LIGHTTHEME) {
            setTheme(R.style.CustomStyle_LightTheme)
        } else {
            setTheme(R.style.CustomStyle_DarkTheme)
        }

        super.onCreate(sav edInstanceState)
        setContentView(R.layout.reminder_layout)
        mToDoItems = MainActivity.getLocallyStoredData(storeRetrieveData)
        setSupportActionBar(toolbar)

        var i = intent
        var id = i.getSerializableExtra(TodoNotificationService.TODOUUID)

        for (toDoItem in mToDoItems) {
            if (toDoItem.identifier == id) {
                mItem = toDoItem
                break
            }
        }

        mtoDoTextTextView.text = mItem.toDoText


        // 자바스러운거 아닌가요.. 찜ㅂ찜......
        if (theme == MainActivity.LIGHTTHEME) {
            mSnoozeTextView.textColors = resources.getColor(R.color.secondary_text)
        } else {
            //  mSnoozeTextView.textColors = Color.WHITE 요렇게 하고싶은데 말이죠.. error..위에랑 다른데 뭐지..
            mSnoozeTextView.setTextColor(Color.WHITE)
            mSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_snooze_white_24dp, 0, 0, 0
            )
        }

        mRdemoveToDoButton.setOnClickListener {
            mToDoItems.remove(mItem)
            saveData()
//            changeOccurred()
//            closeApp()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_reminder, menu)
        return true
    }

    private fun saveData() {
        try {
            storeRetrieveData.saveToFile(mToDoItems)
            // 좋은 코틀 표현식 없나요
            //java 1.7 추가 multiple catch : (JSONException | IOException e)
        } catch (e: Exception) {
            when (e) {
                is JSONException,
                is IOException -> {
                    e.printStackTrace()
                }
                else -> throw e
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}