package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context

/**
 * Created by yongju on 2017. 7. 19..
 */

class Utils {
    companion object {
        @JvmStatic
        fun getToolbarHeight(context: Context)
                = context.theme.obtainStyledAttributes(kotlin.IntArray(1) { R.attr.actionBarSize }).run {
            val dim = getDimension(0, 0f)
            recycle()
            dim.toInt()
        }
    }
}