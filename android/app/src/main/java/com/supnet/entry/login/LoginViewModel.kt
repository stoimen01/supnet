package com.supnet.entry.login

import com.supnet.common.BaseViewModel
import com.supnet.common.SchedulersProvider
import com.supnet.common.StateReducer
import com.supnet.data.connection.ConnectionState
import com.supnet.data.credentials.api.SignResult
import com.supnet.entry.login.LoginEffect.*
import com.supnet.entry.login.LoginEvent.*
import com.supnet.entry.login.LoginState.Idle
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class LoginViewModel(
    private val connectionStates: Observable<ConnectionState>,
    private val onLogin: (email : String, password: String) -> Single<SignResult>,
    private val navigator: LoginNavigator,
    schedulersProvider: SchedulersProvider,
    reducer: StateReducer<LoginState, LoginEvent, LoginEffect>
) : BaseViewModel<LoginState, LoginEvent, LoginViewEvent, LoginEffect> (
    initialState = Idle(),
    initialEffect = TrackConnectionChanges,
    schedulersProvider = schedulersProvider,
    reducer = reducer
) {

    override fun onEffect(effect: LoginEffect) = when (effect) {

        TrackConnectionChanges -> {
            disposables += connectionStates
                .observeOn(schedulersProvider.ui())
                .subscribe { onEvent(ConnectionStateChanged(it)) }
        }

        is TryToLogin -> {
            disposables += onLogin(effect.email, effect.password)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribeBy(
                    onSuccess = { onEvent(OnLoginSuccess) },
                    onError = { onEvent(OnLoginError) }
                )
        }

        NavigateToRegister -> navigator.onCreateAccount()

        NavigateIndoor -> navigator.onSuccessfulLogin()

    }
}
