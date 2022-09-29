package com.diskominfos.subakbali.customview
import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.diskominfos.subakbali.R

class EmailEditText : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        isSingleLine = true
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                validate()
            }
        })
    }

    private fun validate() {
//        when {
//            isEmpty() -> {
//                error = context.getString(R.string.empty_email)
//            }
//            isInvalidEmail() ->
//                error = context.getString(R.string.incorrect_email)
//        }
    }

    private fun isInvalidEmail(): Boolean {
        return !Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()
    }

//    private fun isEmpty(): Boolean {
//        return text.isNullOrEmpty()
//    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }
}