package com.example.avjindersinghsekhon.minimaltodo

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class ItemTouchHelperClass(val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    interface ItemTouchHelperAdapter {
        fun onItemMoved(fromPosition: Int, toPosition: Int)
        fun onItemRemoved(position: Int)
    }

    override fun isLongPressDragEnabled(): Boolean { return true }

    override fun isItemViewSwipeEnabled(): Boolean { return true }

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val upFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(upFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        adapter.onItemMoved(viewHolder?.adapterPosition!!, target?.adapterPosition!!)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        adapter.onItemRemoved(viewHolder?.adapterPosition!!)
    }
}