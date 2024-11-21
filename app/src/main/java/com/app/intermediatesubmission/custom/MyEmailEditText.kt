package com.app.intermediatesubmission.custom

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText

class MyEmailEditText: TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        text?.let {
            val isValidEmail: Boolean = !TextUtils.isEmpty(it) && Patterns.EMAIL_ADDRESS.matcher(it).matches()
            error = if (isValidEmail){
                null
            } else {
                "Invalid Email format"
            }
        }

    }
}