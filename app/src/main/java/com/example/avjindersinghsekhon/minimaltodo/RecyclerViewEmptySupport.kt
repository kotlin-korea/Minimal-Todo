package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * Created by hardyeats on 2017-07-31.
 */

class RecyclerViewEmptySupport @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    private var emptyView: View? = null

    private val observer = object: AdapterDataObserver() {
        override fun onChanged() { showEmptyView() }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            showEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            showEmptyView()
        }
    }

    fun showEmptyView() {
        if(adapter != null && emptyView != null) {
            if(adapter.itemCount == 0) {
                emptyView!!.visibility = VISIBLE
                this.visibility = GONE
            } else {
                emptyView!!.visibility = GONE
                this.visibility = VISIBLE
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.run {
            registerAdapterDataObserver(observer)
            observer.onChanged()
        }
    }

    fun setEmptyView(v: View) { emptyView = v }
}