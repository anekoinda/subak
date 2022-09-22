package com.diskominfos.subakbali.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.diskominfos.subakbali.R

class PasswordEditText: AppCompatEditText, View.OnTouchListener {
    private lateinit var mShowIconButtonImage: Drawable
    private lateinit var mHideIconButtonImage: Drawable
    private lateinit var mLockButtonImage: Drawable
    internal var isChecked: Boolean = false
    internal var switched: Boolean = false

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

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val toggleButtonStart: Float
            val toggleButtonEnd: Float
            var isToggleButtonClicked = false
            when (layoutDirection) {
                View.LAYOUT_DIRECTION_RTL -> {
                    toggleButtonEnd = (mShowIconButtonImage.intrinsicWidth + paddingStart).toFloat()
                    when {
                        event.x < toggleButtonEnd -> isToggleButtonClicked = true
                    }
                }
                else -> {
                    toggleButtonStart =
                        (width - paddingEnd - mShowIconButtonImage.intrinsicWidth).toFloat()
                    when {
                        event.x > toggleButtonStart -> isToggleButtonClicked = true
                    }
                }
            }
            return if (isToggleButtonClicked) when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (error == null) {
                        switched = true
                        toggleVisibility()
                    }
                    true
                }
                else -> false
            } else false
        }
        return false
    }

    private fun init() {
        mShowIconButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_visibility) as Drawable
        mHideIconButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_visibility_off) as Drawable
        mLockButtonImage =
            ContextCompat.getDrawable(context, R.drawable.custom_lock_icon) as Drawable
        isSingleLine = true
        transformationMethod = PasswordTransformationMethod.getInstance()
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (isChecked && !switched
                ) {
                    transformationMethod = PasswordTransformationMethod.getInstance()
                    setSelection(length())
                    isChecked = false
                    toggleIcon(isChecked)
                }
                switched = false
                validate()
            }
        })
    }

    private fun toggleVisibility() {
        transformationMethod = if (!isChecked) null else PasswordTransformationMethod.getInstance()
        setSelection(length())
        isChecked = !isChecked
        toggleIcon(isChecked)
    }

    private fun toggleIcon(b: Boolean) {
        setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            if (b) mHideIconButtonImage else mShowIconButtonImage,
            null
        )
    }

    private fun validate() {
        when {
            isEmpty() -> {
                error = context.getString(R.string.empty_password)
            }
            isInvalidPassword() ->
                error = context.getString(R.string.incorrect_password)
        }
    }

    private fun isInvalidPassword(): Boolean {
        val length = text?.length
        if (length != null) return length < 6
        return false
    }

    private fun isEmpty(): Boolean {
        return text.isNullOrEmpty()
    }
}