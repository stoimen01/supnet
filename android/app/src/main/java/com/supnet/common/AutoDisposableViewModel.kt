package com.supnet.common

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class AutoDisposableViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    override fun onCleared() {
        disposables.clear()
    }
}