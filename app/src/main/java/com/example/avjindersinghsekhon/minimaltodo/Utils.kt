package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context

/**
 * Created by kgj11 on 2017-07-23.
 */
class Utils {

    companion object {
        @JvmStatic fun getToolbarHeight(context: Context): Int {
            val styledAttributes = context.theme.obtainStyledAttributes(
                    intArrayOf(R.attr.actionBarSize))
            val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
            styledAttributes.recycle()
            return toolbarHeight
        }
    }
}