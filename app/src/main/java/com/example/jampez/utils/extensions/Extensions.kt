package com.example.jampez.utils.extensions

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.widget.AppCompatImageView
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

fun String.isEmailValid(): Boolean {
    if (isEmpty()) {
        return false
    }
    val p = Pattern.compile(
        "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}\$", CASE_INSENSITIVE
    )
    val m = p.matcher(this)
    return m.matches()
}

fun AppCompatImageView.startLoadingAnimation() {
    val animationDrawable = drawable as AnimationDrawable
    animationDrawable.callback = this
    animationDrawable.setVisible(true, true)
    post {
        animationDrawable.start()
    }
}

fun AppCompatImageView.stopLoadingAnimation() {
    val animationDrawable = drawable as AnimationDrawable
    post {
        animationDrawable.stop()
    }
}