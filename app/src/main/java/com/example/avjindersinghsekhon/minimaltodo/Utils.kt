package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context

/**
 * Created by yongju on 2017. 7. 19..
 */


/*
//original source
public class Utils {
        public static int getToolbarHeight(Context context) {
            final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                    new int[]{R.attr.actionBarSize});
            int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();

            return toolbarHeight;
        }
}
 */

class Utils {
    companion object {
        @JvmStatic
        fun getToolbarHeight(context: Context): Int {
            val obtainStyledAttributes = context.theme.obtainStyledAttributes(kotlin.IntArray(1){R.attr.actionBarSize})
            val dimension = obtainStyledAttributes.getDimension(0, 0.0f)
            obtainStyledAttributes.recycle()
            return dimension.toInt()
        }
    }
}