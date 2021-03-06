package com.supnet.common

import android.view.View

fun View.hide() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun View.show() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

var View.isVisible : Boolean
get() = visibility == View.VISIBLE
set(value) = if (value) show()
else hide()