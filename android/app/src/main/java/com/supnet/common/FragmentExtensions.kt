package com.supnet.common

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.supnet.navigation.NavigationViewModel

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