package com.example.avjindersinghsekhon.minimaltodo.view.main.presenter

import com.example.avjindersinghsekhon.minimaltodo.base.presenter.BasePresenter
import com.example.avjindersinghsekhon.minimaltodo.base.presenter.BaseView
import com.example.avjindersinghsekhon.minimaltodo.data.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.data.source.todo.StoreRetrieveData
import com.example.avjindersinghsekhon.minimaltodo.view.main.adapter.model.MainModel

/**
 * Created by taehwankwon on 7/22/17.
 */

interface MainContract {
    interface View : BaseView {
        fun onUpdateTheme(theme: Int)

        fun setAlarms(item: ToDoItem)
        fun showAddToActivity(item: ToDoItem)
        fun showRemoveItem(toDoItem: ToDoItem, position: Int)
    }

    interface Presenter : BasePresenter<View> {

        var storeRetrieveData: StoreRetrieveData

        val theme: String

        var adapterModel: MainModel.Model
        var adapterView: MainModel.View?

        fun updateAdapterItem()

        fun updateTheme()

        fun updatePrefChangeOccured()
        fun updatePrefReminderExit(): Boolean
        fun updatePrefRecreate(): Boolean
        fun updatePrefChangeOccuredAndItemUpdate()

        fun addToDataStore(item: ToDoItem)
        fun saveToFile()

        fun updateToDoItems(item: ToDoItem)
    }
}