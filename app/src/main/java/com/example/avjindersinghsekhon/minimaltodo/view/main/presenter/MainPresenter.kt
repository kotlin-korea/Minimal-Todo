package com.example.avjindersinghsekhon.minimaltodo.view.main.presenter

import android.content.Context
import android.util.Log
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.ReminderActivity
import com.example.avjindersinghsekhon.minimaltodo.base.presenter.CommonPresenter
import com.example.avjindersinghsekhon.minimaltodo.contract.Contract
import com.example.avjindersinghsekhon.minimaltodo.data.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.data.source.todo.StoreRetrieveData
import com.example.avjindersinghsekhon.minimaltodo.view.main.adapter.model.MainModel
import org.json.JSONException
import java.io.IOException

/**
 * Created by taehwankwon on 7/22/17.
 */

class MainPresenter(val context: Context) : CommonPresenter<MainContract.View>(), MainContract.Presenter {

    override lateinit var storeRetrieveData: StoreRetrieveData

    override lateinit var adapterModel: MainModel.Model
    override var adapterView: MainModel.View? = null
        set(value) {
            field = value
            field?.onClickItem = {
                view?.showAddToActivity(adapterModel.getItem(it))
            }
            field?.removeAt = {
                toDoItem, position ->
                view?.showRemoveItem(toDoItem, position)
            }
        }

    private var toDoItemsArrayList: ArrayList<ToDoItem>? = null
        get() = getLocallyStoredData()

    override val theme: String by lazy {
        context.getSharedPreferences(Contract.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(Contract.THEME_SAVED, Contract.LIGHT_THEME)
    }

    override fun updateAdapterItem() {
        toDoItemsArrayList?.forEach {
            adapterModel.addItem(it)
        }
        adapterView?.notifyDataSetChanged()
    }

    override fun updatePrefChangeOccured() {
        context.getSharedPreferences(Contract.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE).edit().apply {
            putBoolean(Contract.CHANGE_OCCURED, false)
        }.apply()
    }

    override fun updatePrefReminderExit(): Boolean {
        context.getSharedPreferences(Contract.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE).apply {
            if (getBoolean(ReminderActivity.EXIT, false)) {
                edit().apply {
                    putBoolean(ReminderActivity.EXIT, false)
                }.apply()
                return true
            }
        }
        return false
    }

    override fun updatePrefRecreate(): Boolean {
        context.getSharedPreferences(Contract.THEME_PREFERENCES, Context.MODE_PRIVATE).apply {
            if (getBoolean(Contract.RECREATE_ACTIVITY, false)) {
                edit().apply {
                    putBoolean(Contract.RECREATE_ACTIVITY, false)
                }.apply()
                return true
            }
        }
        return false
    }

    override fun updatePrefChangeOccuredAndItemUpdate() {
        context.getSharedPreferences(Contract.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE).apply {
            if (getBoolean(Contract.CHANGE_OCCURED, false)) {
                toDoItemsArrayList?.forEach {
                    view?.setAlarms(it)
                }
                edit().apply {
                    putBoolean(Contract.RECREATE_ACTIVITY, false)
                }.apply()
            }
        }
    }

    override fun updateTheme() {
        //We recover the theme we've set and setTheme accordingly
        if (theme == Contract.LIGHT_THEME) {
            view?.onUpdateTheme(R.style.CustomStyle_LightTheme)
        } else {
            view?.onUpdateTheme(R.style.CustomStyle_DarkTheme)
        }
    }

    override fun addToDataStore(item: ToDoItem) {
        toDoItemsArrayList?.add(item)
        adapterModel.addItem(item)
        adapterView?.notifyItemInserted(toDoItemsArrayList?.size ?: 1 - 1)
    }

    override fun saveToFile() {
        try {
            storeRetrieveData.saveToFile(toDoItemsArrayList)
        } catch (e: JSONException) {

        } catch (e: IOException) {

        }
    }

    // TODO Bug...
    override fun updateToDoItems(item: ToDoItem) {
        var existed = false
        toDoItemsArrayList?.forEachIndexed { index, toDoItem ->
            if (item.identifier == toDoItem.identifier) {
                toDoItemsArrayList?.add(index, item)
                existed = true
                adapterView?.notifyDataSetChanged()
            }
        }

        if (!existed) {
            addToDataStore(item)
        }
    }

    private fun getLocallyStoredData() = try {
        storeRetrieveData.loadFromFile()
    } catch (e: IOException) {
        java.util.ArrayList<ToDoItem>()
    } catch (e: JSONException) {
        java.util.ArrayList<ToDoItem>()
    }
}