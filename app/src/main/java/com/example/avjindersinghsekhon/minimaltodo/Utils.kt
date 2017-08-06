package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.content.res.TypedArray

/**
 * Created by patternoid on 2017. 8. 3..
 */
class Utils {
    companion object {

        @JvmStatic
        fun getToolbarHeight(context : Context) : Int {

            val styledAttributes : TypedArray = context.theme.obtainStyledAttributes( intArrayOf(R.attr.actionBarSize ) )
            val toolbarHeight : Int = styledAttributes.getDimension(0,0f).toInt()

            styledAttributes.recycle()

            return toolbarHeight
        }

    }


}