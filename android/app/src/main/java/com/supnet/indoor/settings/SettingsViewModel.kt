package com.supnet.indoor.settings

import com.supnet.common.AutoDisposableViewModel
import io.reactivex.Completable

class SettingsViewModel(
    private val onSignOut: Completable
) : AutoDisposableViewModel() {


    fun signOut() {
        onSignOut.subscribe()
    }

}