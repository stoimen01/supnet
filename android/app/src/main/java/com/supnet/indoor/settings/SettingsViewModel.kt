package com.supnet.indoor.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supnet.common.AutoDisposableViewModel
import com.supnet.common.Command
import com.supnet.common.SchedulersProvider
import com.supnet.data.SupnetResult
import com.supnet.data.SupnetResult.SignOffResult.*
import com.supnet.data.SupnetResult.SignOutResult.*
import com.supnet.device.connection.ConnectionState
import com.supnet.device.connection.ConnectionState.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class SettingsViewModel(
    private val connectionStates: Observable<ConnectionState>,
    private val signOutResults: Observable<SupnetResult.SignOutResult>,
    private val signOffResults: Observable<SupnetResult.SignOffResult>,
    private val onSignOut: () -> Unit,
    private val onSighOff: () -> Unit,
    private val schedulersProvider: SchedulersProvider
) : AutoDisposableViewModel() {

    private val liveState = MutableLiveData<SettingsState>()
    private val liveCommand = MutableLiveData<Command<SettingsCommand>>()

    init {
        liveState.value = SettingsState(false, true)

        disposables += connectionStates
            .observeOn(schedulersProvider.ui())
            .subscribe {
                when (it!!) {
                    CONNECTED -> {
                        liveState.value = liveState.value!!.copy(isSignOffEnabled = true)
                    }
                    DISCONNECTED -> {
                        liveState.value = liveState.value!!.copy(isSignOffEnabled = false)
                    }
                }
            }

        disposables += signOutResults
            .observeOn(schedulersProvider.ui())
            .subscribe {
                return@subscribe when (it) {
                    SignOutSuccess -> {}
                    SignOutFailure -> {
                        liveCommand.value = Command(SettingsCommand.SHOW_ERROR)
                        liveState.value = liveState.value!!.copy(isLoading = false)
                    }
                }
            }

        disposables += signOffResults
            .observeOn(schedulersProvider.ui())
            .subscribe {
                return@subscribe when (it) {
                    SignOffSuccess -> {}
                    SignOffFailure -> {
                        liveCommand.value = Command(SettingsCommand.SHOW_ERROR)
                        liveState.value = liveState.value!!.copy(isLoading = false)
                    }
                }
            }
    }

    fun signOut() {
        liveState.value = liveState.value!!.copy(isLoading = true)
        onSignOut()
    }

    fun signOff() {
        liveState.value = liveState.value!!.copy(isLoading = true)
        onSighOff()
    }

    fun getLiveState(): LiveData<SettingsState> = liveState

    fun getLiveCommand(): LiveData<Command<SettingsCommand>> = liveCommand

}