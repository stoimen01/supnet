package com.supnet.entry.signin

import com.supnet.SupnetEvent
import com.supnet.SupnetEvent.SignInEvent.*
import com.supnet.common.BaseViewModel
import com.supnet.common.SchedulersProvider
import com.supnet.common.StateReducer
import com.supnet.data.connection.ConnectionState
import com.supnet.entry.signin.SignInEffect.*
import com.supnet.entry.signin.SignInEvent.*
import com.supnet.entry.signin.SignInState.Idle
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class SignInViewModel(
    private val connectionStates: Observable<ConnectionState>,
    private val signInEvents: Observable<SupnetEvent.SignInEvent>,
    private val onLogin: (email : String, password: String) -> Unit,
    private val navigator: SignInNavigator,
    schedulersProvider: SchedulersProvider,
    reducer: StateReducer<SignInState, SignInEvent, SignInEffect>
) : BaseViewModel<SignInState, SignInEvent, SignInViewEvent, SignInEffect> (
    initialState = Idle(),
    initialEffect = TrackConnectionChanges,
    schedulersProvider = schedulersProvider,
    reducer = reducer
) {

    override fun onEffect(effect: SignInEffect) = when (effect) {

        TrackConnectionChanges -> {
            disposables += connectionStates
                .observeOn(schedulersProvider.ui())
                .subscribe { onEvent(ConnectionStateChanged(it)) }
        }

        TrackSignInEvents -> {
            disposables += signInEvents
                .observeOn(schedulersProvider.ui())
                .subscribe {
                    return@subscribe when (it) {
                        SignInSuccess -> onEvent(OnSignInSuccess)
                        SignInFailure -> onEvent(OnSignInError)
                    }
                }
        }

        is TryToSignIn -> onLogin(effect.email, effect.password)

        NavigateToRegister -> navigator.onSignUp()

    }
}
