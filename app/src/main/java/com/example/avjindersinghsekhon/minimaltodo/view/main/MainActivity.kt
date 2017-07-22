package com.example.avjindersinghsekhon.minimaltodo.view.main

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.example.avjindersinghsekhon.minimaltodo.*
import com.example.avjindersinghsekhon.minimaltodo.base.view.BasePresenterActivity
import com.example.avjindersinghsekhon.minimaltodo.contract.Contract
import com.example.avjindersinghsekhon.minimaltodo.data.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.data.source.todo.StoreRetrieveData
import com.example.avjindersinghsekhon.minimaltodo.view.main.adapter.MainAdapter
import com.example.avjindersinghsekhon.minimaltodo.view.main.presenter.MainContract
import com.example.avjindersinghsekhon.minimaltodo.view.main.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class MainActivity : BasePresenterActivity<MainContract.View, MainContract.Presenter>(), MainContract.View {

    private val alarmManager: AlarmManager
        get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private lateinit var mainAdapter: MainAdapter

    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onCreatePresenter() = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.updateTheme()

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        presenter.storeRetrieveData = StoreRetrieveData(this, Contract.FILENAME)
        presenter.updatePrefChangeOccured()

        mainAdapter = MainAdapter(this).apply {
            sharedPreferences = getSharedPreferences(Contract.THEME_PREFERENCES, MODE_PRIVATE);
        }
        presenter.adapterModel = mainAdapter
        presenter.adapterView = mainAdapter

        addToDoItemFAB.setOnClickListener {
            val newTodoIntent = Intent(this@MainActivity, AddToDoActivity::class.java)
            val item = ToDoItem("", false, null)
            item.todoColor = ColorGenerator.MATERIAL.randomColor
            newTodoIntent.putExtra(Contract.TODO_ITEM, item)
            startActivityForResult(newTodoIntent, Contract.REQUEST_ID_TODO_ITEM)
        }

        toDoRecyclerView.run {
            if (presenter.theme == Contract.LIGHT_THEME) {
                toDoRecyclerView.setBackgroundColor(resources.getColor(R.color.primary_lightest))
            }
            setEmptyView(toDoEmptyView)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(customRecyclerScrollViewListener)
            adapter = mainAdapter
        }

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperClass(mainAdapter))
        itemTouchHelper.attachToRecyclerView(toDoRecyclerView)
    }

    private val customRecyclerScrollViewListener = object : CustomRecyclerScrollViewListener() {

        override fun show() {
            addToDoItemFAB
                    .animate()
                    .translationY(0f)
                    .setInterpolator(DecelerateInterpolator(2f)).start()
        }

        override fun hide() {
            val margin = (addToDoItemFAB.layoutParams as CoordinatorLayout.LayoutParams).bottomMargin
            addToDoItemFAB
                    .animate()
                    .translationY((addToDoItemFAB.height + margin).toFloat())
                    .setInterpolator(AccelerateInterpolator(2f)).start()
        }
    }

    override fun onUpdateTheme(theme: Int) {
        setTheme(theme)
    }

    override fun showRemoveItem(toDoItem: ToDoItem, position: Int) {
        val intent = Intent(this@MainActivity, TodoNotificationService::class.java)
        deleteAlarm(intent, toDoItem.identifier.hashCode())
        mainAdapter.notifyItemRemoved(position)

        val toShow = "Todo"
        Snackbar.make(myCoordinatorLayout, "Deleted " + toShow, Snackbar.LENGTH_SHORT)
                .setAction("UNDO") {
                    //Comment the line below if not using Google Analytics
                    presenter.adapterModel.addItem(toDoItem, position)
                    if (toDoItem.hasReminder()) {
                        val i = Intent(this@MainActivity, TodoNotificationService::class.java)
                        i.putExtra(TodoNotificationService.TODOTEXT, toDoItem.toDoText)
                        i.putExtra(TodoNotificationService.TODOUUID, toDoItem.identifier)
                        createAlarm(i, toDoItem.identifier.hashCode(), toDoItem.toDoDate.time)
                    }
                    mainAdapter.notifyItemInserted(position)
                }.show()
    }

    private fun deleteAlarm(i: Intent, requestCode: Int) {
        if (doesPendingIntentExist(i, requestCode)) {
            val pi = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_NO_CREATE)
            pi.cancel()
            alarmManager.cancel(pi)
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode))
        }
    }

    private fun doesPendingIntentExist(i: Intent, requestCode: Int): Boolean {
        val pi = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_NO_CREATE)
        return pi != null
    }

    override fun onResume() {
        super.onResume()

        if (presenter.updatePrefReminderExit()) {
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

        if (presenter.updatePrefRecreate()) {
            recreate()
        }
    }

    override fun onStart() {
        super.onStart()

        presenter.updatePrefChangeOccuredAndItemUpdate()
        presenter.updateAdapterItem()
    }

    override fun setAlarms(item: ToDoItem) {
        if (item.hasReminder()) {
            if (item.toDoDate?.before(Date()) ?: false) {
                item.toDoDate = null
                return
            }
            val intent = Intent(this, TodoNotificationService::class.java).apply {
                putExtra(TodoNotificationService.TODOUUID, item.identifier)
                putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
            }
            createAlarm(intent, item.identifier.hashCode(), item.toDoDate.time)
        }
    }

    override fun showAddToActivity(item: ToDoItem) {
        val i = Intent(this@MainActivity, AddToDoActivity::class.java)
        i.putExtra(Contract.TODO_ITEM, item)
        startActivityForResult(i, Contract.REQUEST_ID_TODO_ITEM)
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
        if (resultCode != Activity.RESULT_CANCELED && requestCode == Contract.REQUEST_ID_TODO_ITEM) {
            val item = data.getSerializableExtra(Contract.TODO_ITEM) as ToDoItem
            if (item.toDoText.isEmpty()) {
                return
            }

            if (item.hasReminder() && item.toDoDate != null) {
                val i = Intent(this, TodoNotificationService::class.java)
                i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                createAlarm(i, item.identifier.hashCode(), item.toDoDate.time)
//                Log.d("OskarSchindler", "Alarm Created: "+item.getToDoText()+" at "+item.getToDoDate());
            }

            presenter.updateToDoItems(item)
        }
    }

    private fun createAlarm(intent: Intent, requestCode: Int, timeInMillis: Long) {
        val pi = PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
    }

//    inner class BasicListAdapter internal constructor(private val items: ArrayList<ToDoItem>) : RecyclerView.Adapter<BasicListAdapter.ViewHolder>(), ItemTouchHelperClass.ItemTouchHelperAdapter {
//
//        override fun onItemMoved(fromPosition: Int, toPosition: Int) {
//            if (fromPosition < toPosition) {
//                for (i in fromPosition..toPosition - 1) {
//                    Collections.swap(items, i, i + 1)
//                }
//            } else {
//                for (i in fromPosition downTo toPosition + 1) {
//                    Collections.swap(items, i, i - 1)
//                }
//            }
//            notifyItemMoved(fromPosition, toPosition)
//        }
//
//        override fun onItemRemoved(position: Int) {
//            //Remove this line if not using Google Analytics
//
//            mJustDeletedToDoItem = items.removeAt(position)
//            mIndexOfDeletedToDoItem = position
//            val i = Intent(this@MainActivity, TodoNotificationService::class.java)
//            deleteAlarm(i, mJustDeletedToDoItem!!.identifier.hashCode())
//            notifyItemRemoved(position)
//
//            //            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
//            val toShow = "Todo"
//            Snackbar.make(mCoordLayout!!, "Deleted " + toShow, Snackbar.LENGTH_SHORT)
//                    .setAction("UNDO") {
//                        //Comment the line below if not using Google Analytics
//                        items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem)
//                        if (mJustDeletedToDoItem!!.toDoDate != null && mJustDeletedToDoItem!!.hasReminder()) {
//                            val i = Intent(this@MainActivity, TodoNotificationService::class.java)
//                            i.putExtra(TodoNotificationService.TODOTEXT, mJustDeletedToDoItem!!.toDoText)
//                            i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem!!.identifier)
//                            createAlarm(i, mJustDeletedToDoItem!!.identifier.hashCode(), mJustDeletedToDoItem!!.toDoDate.time)
//                        }
//                        notifyItemInserted(mIndexOfDeletedToDoItem)
//                    }.show()
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicListAdapter.ViewHolder {
//            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_circle_try, parent, false)
//            return ViewHolder(v)
//        }


    override fun onPause() {
        super.onPause()
        presenter.saveToFile()
    }

    override fun onDestroy() {
        super.onDestroy()
        toDoRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener)
    }
}


