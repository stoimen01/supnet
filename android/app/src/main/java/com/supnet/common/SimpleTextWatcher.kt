package com.supnet.common

import android.text.Editable
import android.text.TextWatcher

class SimpleTextWatcher(val onTextChanged: (String) -> Unit) : TextWatcher {

    override fun afterTextChanged(s: Editable) = onTextChanged(s.toString())

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}