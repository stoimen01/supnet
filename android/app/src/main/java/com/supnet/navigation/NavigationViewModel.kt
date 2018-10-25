package com.supnet.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supnet.common.Command
import com.supnet.signaling.SignalingClient
import com.supnet.signaling.SignalingClient.SignalingState.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class NavigationViewModel(
    private val signalingClient: SignalingClient
) : ViewModel() {

    enum class NavigationCommand {
        SHOW_LOADING, SHOW_ROOMS, SHOW_ERROR
    }

    private val liveCommands = MutableLiveData<Command<NavigationCommand>>()
    private val disposables = CompositeDisposable()

    init {
        signalingClient.connect()
        disposables += signalingClient.getStates()
            .subscribe(::onSignalingState)
    }

    fun getCommands(): LiveData<Command<NavigationCommand>> = liveCommands

    private fun onSignalingState(state: SignalingClient.SignalingState) = when (state) {
        Idle -> { }
        Connecting -> {
            liveCommands.postValue(Command(NavigationCommand.SHOW_LOADING))
        }
        Connected -> {
            liveCommands.postValue(Command(NavigationCommand.SHOW_ROOMS))
        }
        Closed -> {
            liveCommands.postValue(Command(NavigationCommand.SHOW_ERROR))
        }
    }

    override fun onCleared() {
        super.onCleared()
        signalingClient.close()
        disposables.dispose()
    }

}