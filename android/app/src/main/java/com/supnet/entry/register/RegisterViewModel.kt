package com.supnet.entry.register

import com.supnet.common.BaseViewModel
import com.supnet.common.SchedulersProvider
import com.supnet.common.StateReducer
import com.supnet.data.connection.ConnectionState
import com.supnet.entry.register.RegisterEffect.*
import com.supnet.entry.register.RegisterEvent.*
import com.supnet.entry.register.RegisterState.Idle
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class RegisterViewModel(
    private val connectionStates: Observable<ConnectionState>,
    private val onRegister: (email: String, username: String, password: String) -> Completable,
    private val registerNavigator: RegisterNavigator,
    schedulersProvider: SchedulersProvider,
    stateReducer: StateReducer<RegisterState, RegisterEvent, RegisterEffect>
) : BaseViewModel <RegisterState, RegisterEvent, RegisterViewEvent, RegisterEffect> (
    initialState = Idle(),
    initialEffect = TrackConnectionChanges,
    reducer = stateReducer,
    schedulersProvider = schedulersProvider
) {

    override fun onEffect(effect: RegisterEffect) = when (effect) {

        TrackConnectionChanges -> {
            disposables += connectionStates
                .observeOn(schedulersProvider.ui())
                .subscribe { onEvent(ConnectionStateChanged(it)) }
        }

        is TryToRegister -> {
            disposables += onRegister(effect.email, effect.username, effect.password)
                .subscribe({
                    onEvent(OnRegisterSuccess)
                }, {
                    onEvent(OnRegisterFailure)
                })
        }

        NavigateBack -> registerNavigator.onBackFromRegisterClicked()

        NavigateIndoor -> registerNavigator.onRegisterSuccessful()

    }

}