package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.graphics.Canvas
import android.support.design.widget.TextInputLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

/**
 * Created by yongju on 2017. 7. 28..
 */

open class CustomTextInputLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    :TextInputLayout(context, attrs) {
    private var isHintSet = false
    private lateinit var _hint: CharSequence

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child is EditText) _hint = child.hint

        super.addView(child, index, params)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!isHintSet && ViewCompat.isLaidOut(this)) {
            hint = null

            editText?.hint?.apply {
                if (length > 0) {
                    _hint = this
                }
            }

            hint = _hint
            isHintSet = true
        }
    }
}