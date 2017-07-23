package com.example.avjindersinghsekhon.minimaltodo.view.main.adapter.model

import android.content.SharedPreferences
import com.example.avjindersinghsekhon.minimaltodo.data.ToDoItem

/**
 * Created by taehwankwon on 7/22/17.
 */
interface MainModel {
    interface View {

        var onClickItem: (Int) -> Unit
        var sharedPreferences: SharedPreferences
        var removeAt: (Int) -> Unit

        fun notifyDataSetChanged()
        fun notifyItemInserted(position: Int)
        fun notifyItemRemoved(position: Int)
    }

    interface Model {

        fun addItem(position: Int, toDoItem: ToDoItem)
        fun addItem(toDoItem: ToDoItem)

        fun getItem(position: Int): ToDoItem
        fun removeItem(toDoItem: ToDoItem)
        fun removeItemAt(position: Int): ToDoItem
    }
}