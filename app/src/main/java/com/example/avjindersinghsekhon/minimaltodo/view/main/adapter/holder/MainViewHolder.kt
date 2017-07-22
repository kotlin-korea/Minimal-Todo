package com.example.avjindersinghsekhon.minimaltodo.view.main.adapter.holder

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amulyakhare.textdrawable.TextDrawable
import com.example.avjindersinghsekhon.minimaltodo.AddToDoActivity
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.data.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.contract.Contract
import com.example.avjindersinghsekhon.minimaltodo.view.main.adapter.MainAdapter
import kotlinx.android.synthetic.main.list_circle_try.view.*

/**
 * Created by taehwankwon on 7/22/17.
 */

class MainViewHolder(onClickItem: (Int) -> Unit,
                     val sharedPreferences: SharedPreferences,
                     val adapter: MainAdapter,
                     parent: ViewGroup?) :
        RecyclerView.ViewHolder(LayoutInflater.from(adapter.context).inflate(R.layout.list_circle_try, parent, false)) {

    init {
        itemView.setOnClickListener {
            onClickItem(adapterPosition)
        }
    }

    fun onBindView(item: ToDoItem) {
        val bgColor: Int
        val todoTextColor: Int
        if (sharedPreferences.getString(Contract.THEME_SAVED, Contract.LIGHT_THEME) == Contract.LIGHT_THEME) {
            bgColor = Color.WHITE
            todoTextColor = adapter.context.resources.getColor(R.color.secondary_text)
        } else {
            bgColor = Color.DKGRAY
            todoTextColor = Color.WHITE
        }

        with(itemView) {
            listItemLinearLayout.setBackgroundColor(bgColor)

            if (item.hasReminder()) {
                toDoListItemTextview.maxLines = 1
                todoListItemTimeTextView.visibility = View.VISIBLE
            } else {
                toDoListItemTextview.maxLines = 2
                todoListItemTimeTextView.visibility = View.GONE
            }

            toDoListItemTextview.text = item.toDoText
            toDoListItemTextview.setTextColor(todoTextColor)

            toDoListItemColorImageView.setImageDrawable(TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.toDoText.substring(0, 1), item.todoColor))

            item.toDoDate?.let {
                todoListItemTimeTextView.text = if (android.text.format.DateFormat.is24HourFormat(context)) {
                    AddToDoActivity.formatDate(Contract.DATE_TIME_FORMAT_24_HOUR, item.toDoDate)
                } else {
                    AddToDoActivity.formatDate(Contract.DATE_TIME_FORMAT_12_HOUR, item.toDoDate)
                }
            }
        }
    }
}