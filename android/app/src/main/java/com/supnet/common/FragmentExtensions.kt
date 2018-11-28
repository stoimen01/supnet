package com.supnet.common

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.supnet.root.RootViewModel
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity


fun AppCompatActivity.showToast(text: String) {
    Toast.makeText(baseContext, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.getNavigator(): RootViewModel {
    return ViewModelProviders.of(activity!!)
        .get(RootViewModel::class.java)
}

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, block: (T) -> Unit) {
    liveData.observe(this, Observer {
        if (it != null) block(it)
    })
}

fun Fragment.hideKeyboard() {
    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view!!.windowToken, 0)
}

fun <T> LifecycleOwner.observeCommands(liveData: LiveData<Command<T>>, block: (T) -> Unit) {
    liveData.observe(this, Observer { cmd ->
        cmd?.getData()?.let { block(it) }
    })
}