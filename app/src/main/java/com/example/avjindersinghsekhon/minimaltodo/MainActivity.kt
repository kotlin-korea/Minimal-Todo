package com.example.avjindersinghsekhon.minimaltodo

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator

import org.json.JSONException

import java.io.IOException
import java.util.Collections
import java.util.Date

class MainActivity : BaseActivity() {
    private lateinit var mRecyclerView: RecyclerViewEmptySupport
    private lateinit var mAddToDoItemFAB: FloatingActionButton
    private var mToDoItemsArrayList : MutableList<ToDoItem> = ArrayList<ToDoItem>(0)
    private lateinit var mCoordLayout: CoordinatorLayout
    private lateinit var adapter: BasicListAdapter
    private var mJustDeletedToDoItem: ToDoItem? = null
    private var mIndexOfDeletedToDoItem: Int = 0
    private lateinit var storeRetrieveData: StoreRetrieveData
    lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var customRecyclerScrollViewListener: CustomRecyclerScrollViewListener
    private val testStrings = arrayOf("Clean my room", "Water the plants", "Get car washed", "Get my dry cleaning")

    override fun onResume() {
        super.onResume()

        if (getPref(SHARED_PREF_DATA_SET_CHANGED, ReminderActivity.EXIT)) {
            setPref(SHARED_PREF_DATA_SET_CHANGED, ReminderActivity.EXIT, false)
            finish()
        }
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */
        if (getPref(THEME_PREFERENCES, RECREATE_ACTIVITY)) {
            recreate()
            setPref(THEME_PREFERENCES, RECREATE_ACTIVITY, false)
        }
    }

    override fun onStart() {
        super.onStart()
        if (getPref(SHARED_PREF_DATA_SET_CHANGED, CHANGE_OCCURED)) {
            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
            adapter = BasicListAdapter(mToDoItemsArrayList)
            mRecyclerView.adapter = adapter
            setAlarms()

            setPref(SHARED_PREF_DATA_SET_CHANGED, CHANGE_OCCURED, false)
        }
    }

    private fun setAlarms() {
        val today = Date()
        mToDoItemsArrayList.filter {
            it.hasReminder() && it.toDoDate != null
        }.forEach {
            if (it.toDoDate.before(today)) {
                it.toDoDate = null
            } else {
                val i = Intent(this, TodoNotificationService::class.java)
                i.putExtra(TodoNotificationService.TODOUUID, it.identifier)
                        .putExtra(TodoNotificationService.TODOTEXT, it.toDoText)
                createAlarm(i, it.identifier.hashCode(), it.toDoDate.time)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setPref(SHARED_PREF_DATA_SET_CHANGED, CHANGE_OCCURED, false)

        storeRetrieveData = StoreRetrieveData(this, FILENAME)
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
        adapter = BasicListAdapter(mToDoItemsArrayList)
        setAlarms()

        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)

        mCoordLayout = findViewById(R.id.myCoordinatorLayout) as CoordinatorLayout
        mAddToDoItemFAB = findViewById(R.id.addToDoItemFAB) as FloatingActionButton

        mAddToDoItemFAB.setOnClickListener {
            val newTodo = Intent(this@MainActivity, AddToDoActivity::class.java)
            val item = ToDoItem("", false, null)
            val color = ColorGenerator.MATERIAL.randomColor
            item.todoColor = color

            newTodo.putExtra(TODOITEM, item)

            startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM)
        }

        customRecyclerScrollViewListener = object : CustomRecyclerScrollViewListener() {
            override fun show() {

                mAddToDoItemFAB.animate().translationY(0f).setInterpolator(DecelerateInterpolator(2f)).start()
                //                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }

            override fun hide() {

                val lp = mAddToDoItemFAB.layoutParams as CoordinatorLayout.LayoutParams
                val fabMargin = lp.bottomMargin
                mAddToDoItemFAB.animate().translationY((mAddToDoItemFAB.height + fabMargin).toFloat()).setInterpolator(AccelerateInterpolator(2.0f)).start()
            }
        }

        //        mRecyclerView = (RecyclerView)findViewById(R.id.toDoRecyclerView);
        mRecyclerView = findViewById(R.id.toDoRecyclerView) as RecyclerViewEmptySupport
        mRecyclerView.apply {
            if (getThemeString() == LIGHTTHEME) {
                setBackgroundColor(resources.getColor(R.color.primary_lightest))
            }
            setEmptyView(this@MainActivity.findViewById(R.id.toDoEmptyView))
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@MainActivity)
            addOnScrollListener(customRecyclerScrollViewListener)

            val callback = ItemTouchHelperClass(this@MainActivity.adapter)
            itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(this)

            adapter = this@MainActivity.adapter
            //        setUpTransitions();
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.aboutMeMenuItem -> {
                val i = Intent(this, AboutActivity::class.java)
                startActivity(i)
                return true
            }

            R.id.preferences -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode != Activity.RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {
            val item = data.getSerializableExtra(TODOITEM) as ToDoItem
            if (item.toDoText.isEmpty()) {
                return
            }
            var existed = false

            if (item.hasReminder() && item.toDoDate != null) {
                val i = Intent(this, TodoNotificationService::class.java)
                i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                createAlarm(i, item.identifier.hashCode(), item.toDoDate.time)
                //                Log.d("OskarSchindler", "Alarm Created: "+item.getToDoText()+" at "+item.getToDoDate());
            }

            mToDoItemsArrayList.filter {
                item.identifier == it.identifier
            }.firstOrNull()?.let {
                existed = true
                adapter.notifyDataSetChanged()
            }
            if (!existed) {
                addToDataStore(item)
            }
        }
    }

    private val alarmManager: AlarmManager
        get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun doesPendingIntentExist(i: Intent, requestCode: Int): Boolean {
        val pi = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_NO_CREATE)
        return pi != null
    }

    private fun createAlarm(i: Intent, requestCode: Int, timeInMillis: Long) {
        val am = alarmManager
        val pi = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT)
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
        //        Log.d("OskarSchindler", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }

    private fun deleteAlarm(i: Intent, requestCode: Int) {
        if (doesPendingIntentExist(i, requestCode)) {
            val pi = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_NO_CREATE)
            pi.cancel()
            alarmManager.cancel(pi)
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode))
        }
    }

    private fun addToDataStore(item: ToDoItem) {
        mToDoItemsArrayList.add(item)
        adapter.notifyItemInserted(mToDoItemsArrayList.size - 1)

    }

    inner class BasicListAdapter internal constructor(private val items: MutableList<ToDoItem>) : RecyclerView.Adapter<BasicListAdapter.ViewHolder>(), ItemTouchHelperClass.ItemTouchHelperAdapter {

        override fun onItemMoved(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition..toPosition - 1) {
                    Collections.swap(items, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(items, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemRemoved(position: Int) {
            //Remove this line if not using Google Analytics

            mJustDeletedToDoItem = items.removeAt(position)
            mIndexOfDeletedToDoItem = position
            val i = Intent(this@MainActivity, TodoNotificationService::class.java)
            deleteAlarm(i, mJustDeletedToDoItem!!.identifier.hashCode())
            notifyItemRemoved(position)

            //            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
            val toShow = "Todo"
            Snackbar.make(mCoordLayout, "Deleted " + toShow, Snackbar.LENGTH_SHORT)
                    .setAction("UNDO") {
                        //Comment the line below if not using Google Analytics
                        mJustDeletedToDoItem?.let {
                            items.add(mIndexOfDeletedToDoItem, it)
                            if (it.toDoDate != null && it.hasReminder()) {
                                val i = Intent(this@MainActivity, TodoNotificationService::class.java)
                                i.putExtra(TodoNotificationService.TODOTEXT, it.toDoText)
                                        .putExtra(TodoNotificationService.TODOUUID, it.identifier)
                                createAlarm(i, it.identifier.hashCode(),
                                        it.toDoDate.time)
                            }
                        }
                        notifyItemInserted(mIndexOfDeletedToDoItem)
                    }.show()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicListAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_circle_try, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: BasicListAdapter.ViewHolder, position: Int) {
            val item = items[position]

            //Background color for each to-do item. Necessary for night/day mode
            val bgColor: Int
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            val todoTextColor: Int
            if (getThemeString() == LIGHTTHEME) {
                bgColor = Color.WHITE
                todoTextColor = resources.getColor(R.color.secondary_text)
            } else {
                bgColor = Color.DKGRAY
                todoTextColor = Color.WHITE
            }
            holder.linearLayout.setBackgroundColor(bgColor)

            if (item.hasReminder() && item.toDoDate != null) {
                holder.mToDoTextview.maxLines = 1
                holder.mTimeTextView.visibility = View.VISIBLE
                //                holder.mToDoTextview.setVisibility(View.GONE);
            } else {
                holder.mTimeTextView.visibility = View.GONE
                holder.mToDoTextview.maxLines = 2
            }
            holder.mToDoTextview.text = item.toDoText
            holder.mToDoTextview.setTextColor(todoTextColor)
            val myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.toDoText.substring(0, 1), item.todoColor)

            //            TextDrawable myDrawable = TextDrawable.builder().buildRound(item.getToDoText().substring(0,1),holder.color);
            holder.mColorImageView.setImageDrawable(myDrawable)
            if (item.toDoDate != null) {
                val timeToShow: String
                if (android.text.format.DateFormat.is24HourFormat(this@MainActivity)) {
                    timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_24_HOUR, item.toDoDate)
                } else {
                    timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_12_HOUR, item.toDoDate)
                }
                holder.mTimeTextView.text = timeToShow
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class ViewHolder
        //            int color = -1;

        (internal var mView: View) : RecyclerView.ViewHolder(mView) {
            internal var linearLayout: LinearLayout
            internal var mToDoTextview: TextView
            //            TextView mColorTextView;
            internal var mColorImageView: ImageView
            internal var mTimeTextView: TextView

            init {
                mView.setOnClickListener {
                    val item = items[this@ViewHolder.adapterPosition]
                    val i = Intent(this@MainActivity, AddToDoActivity::class.java)
                    i.putExtra(TODOITEM, item)
                    startActivityForResult(i, REQUEST_ID_TODO_ITEM)
                }
                mToDoTextview = mView.findViewById<View>(R.id.toDoListItemTextview) as TextView
                mTimeTextView = mView.findViewById<View>(R.id.todoListItemTimeTextView) as TextView
                //                mColorTextView = (TextView)v.findViewById(R.id.toDoColorTextView);
                mColorImageView = mView.findViewById<View>(R.id.toDoListItemColorImageView) as ImageView
                linearLayout = mView.findViewById<View>(R.id.listItemLinearLayout) as LinearLayout
            }
        }
    }

    private fun saveDate() {
        try {
            storeRetrieveData.saveToFile(java.util.ArrayList<ToDoItem>(mToDoItemsArrayList))
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

    override fun onPause() {
        super.onPause()
        saveDate()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener)
    }

    companion object {
        const val TODOITEM = "com.avjindersinghsekhon.com.avjindersinghsekhon.minimaltodo.MainActivity"
        private val REQUEST_ID_TODO_ITEM = 100
        val DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a"
        val DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm"
        const val FILENAME = "todoitems.json"
        const val SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged"
        const val CHANGE_OCCURED = "com.avjinder.changeoccured"
        const val THEME_PREFERENCES = "com.avjindersekhon.themepref"
        const val RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity"
        const val THEME_SAVED = "com.avjindersekhon.savedtheme"
        const val DARKTHEME = "com.avjindersekon.darktheme"
        const val LIGHTTHEME = "com.avjindersekon.lighttheme"

        fun getLocallyStoredData(storeRetrieveData: StoreRetrieveData): MutableList<ToDoItem> {
            try {
                return storeRetrieveData.loadFromFile().toMutableList()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return ArrayList<ToDoItem>(0)
        }
    }
}
