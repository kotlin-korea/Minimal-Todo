package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class RecyclerViewEmptySupport @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0):
        RecyclerView(context, attrs, defStyle) {
    private var emptyView: View? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            showEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            onChanged()
        }
    }

    fun showEmptyView() {
        val adapter = adapter
        if (adapter != null && emptyView != null) {
            if (adapter.itemCount == 0) {
                emptyView!!.visibility = View.VISIBLE
                visibility = View.GONE
            } else {
                emptyView!!.visibility = View.GONE
                visibility = View.VISIBLE
            }
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.run {
            registerAdapterDataObserver(observer)
            observer.onChanged()
        }
    }

    fun setEmptyView(v: View?) {
        emptyView = v
        showEmptyView()
    }
}