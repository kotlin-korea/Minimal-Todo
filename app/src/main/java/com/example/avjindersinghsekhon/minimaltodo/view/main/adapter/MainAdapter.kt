package com.example.avjindersinghsekhon.minimaltodo.view.main.adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.avjindersinghsekhon.minimaltodo.ItemTouchHelperClass
import com.example.avjindersinghsekhon.minimaltodo.data.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.view.main.adapter.holder.MainViewHolder
import com.example.avjindersinghsekhon.minimaltodo.view.main.adapter.model.MainModel
import java.util.*

/**
 * Created by taehwankwon on 7/22/17.
 */

class MainAdapter(val context: Context) : RecyclerView.Adapter<MainViewHolder>(),
        ItemTouchHelperClass.ItemTouchHelperAdapter, MainModel.Model, MainModel.View {

    override lateinit var onClickItem: (Int) -> Unit
    override lateinit var sharedPreferences: SharedPreferences

    // todo 제거 필요(onTouch 변경 후 제거)
    override lateinit var removeAt: (ToDoItem, Int) -> Unit


    private val todoList = mutableListOf<ToDoItem>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int)
            = MainViewHolder(onClickItem, sharedPreferences, this, parent)

    override fun onBindViewHolder(holder: MainViewHolder?, position: Int) {
        holder?.onBindView(getItem(position))
    }

    override fun getItemCount() = todoList.size

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (index in fromPosition..toPosition - 1) {
                Collections.swap(todoList, index, index + 1)
            }
        } else {
            for (index in fromPosition..toPosition + 1) {
                Collections.swap(todoList, index, index - 1)
            }
        }
    }

    override fun onItemRemoved(position: Int) {
        removeAt(todoList.removeAt(position), position)
    }

    override fun getItem(position: Int) = todoList[position]

    override fun addItem(toDoItem: ToDoItem, position: Int) {
        todoList.add(position, toDoItem)
    }

    override fun addItem(toDoItem: ToDoItem) {
        todoList.add(toDoItem)
    }
}
