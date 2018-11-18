package com.supnet.common

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.supnet.navigation.NavigationViewModel
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService



fun Fragment.showToast(text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.getNavigator(): NavigationViewModel {
    return ViewModelProviders.of(activity!!)
        .get(NavigationViewModel::class.java)
}

fun <T> Fragment.observe(liveData: LiveData<T>, block: (T) -> Unit) {
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