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
 * Created by yongju on 2017. 7. 27..
 */
open class CustomTextInputLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null):
        TextInputLayout(context, attrs) {

    private var isHintSet = false
    // 변수명을 hint로 하려고 했더니 TextInputLayout getHint(),
    private lateinit var _hint: CharSequence

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child is EditText) {
            _hint = child.hint //smart cast 로 as 로 casting 이 필요 없음.
        }

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