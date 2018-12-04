package com.supnet.entry.signin

import android.util.Log
import com.supnet.data.SupnetResult
import com.supnet.data.SupnetResult.SignInResult.*
import com.supnet.common.BaseViewModel
import com.supnet.common.SchedulersProvider
import com.supnet.common.StateReducer
import com.supnet.device.connection.ConnectionState
import com.supnet.entry.signin.SignInEffect.*
import com.supnet.entry.signin.SignInEvent.*
import com.supnet.entry.signin.SignInState.Idle
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class SignInViewModel(
    private val connectionStates: Observable<ConnectionState>,
    private val signInEvents: Observable<SupnetResult.SignInResult>,
    private val onLogin: (email : String, password: String) -> Unit,
    private val navigator: SignInNavigator,
    schedulersProvider: SchedulersProvider,
    reducer: StateReducer<SignInState, SignInEvent, SignInEffect>
) : BaseViewModel<SignInState, SignInEvent, SignInViewEvent, SignInEffect> (
    initialState = Idle(),
    initialEffects = listOf(TrackConnectionChanges, TrackSignInEvents),
    schedulersProvider = schedulersProvider,
    reducer = reducer
) {

    override fun onEffect(effect: SignInEffect) = when (effect) {

        TrackConnectionChanges -> {
            disposables += connectionStates
                .subscribe { onEvent(ConnectionStateChanged(it)) }
        }

        TrackSignInEvents -> {
            disposables += signInEvents
                .subscribe {
                    Log.d("SignInResult", "$it")
                    return@subscribe when (it) {
                        is SignInSuccess -> onEvent(OnSignInSuccess)
                        SignInFailure -> onEvent(OnSignInError)
                    }
                }
        }

        is TryToSignIn -> onLogin(effect.email, effect.password)

        NavigateToRegister -> navigator.onSignUp()

    }
}
