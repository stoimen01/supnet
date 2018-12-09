package com.supnet.entry.signup

import com.supnet.domain.user.UserManagerResult
import com.supnet.domain.user.UserManagerResult.SignUpResult.*
import com.supnet.common.BaseViewModel
import com.supnet.common.SchedulersProvider
import com.supnet.common.StateReducer
import com.supnet.device.connection.ConnectionState
import com.supnet.entry.signup.SignUpEffect.*
import com.supnet.entry.signup.SignUpEvent.*
import com.supnet.entry.signup.SignUpState.Idle
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class SignUpViewModel(
    private val connectionStates: Observable<ConnectionState>,
    private val signUpEvents: Observable<UserManagerResult.SignUpResult>,
    private val onTryToSignUp: (email: String, username: String, password: String) -> Unit,
    private val signUpNavigator: SignUpNavigator,
    schedulersProvider: SchedulersProvider,
    stateReducer: StateReducer<SignUpState, SignUpEvent, SignUpEffect>
) : BaseViewModel <SignUpState, SignUpEvent, SignUpViewEvent, SignUpEffect> (
    initialState = Idle(),
    initialEffects = listOf(TrackConnectionChanges, TrackSignUpMessages),
    reducer = stateReducer,
    schedulersProvider = schedulersProvider
) {

    override fun onEffect(effect: SignUpEffect) = when (effect) {

        TrackConnectionChanges -> {
            disposables += connectionStates
                .subscribe { onEvent(ConnectionStateChanged(it)) }
        }

        TrackSignUpMessages -> {
            disposables += signUpEvents
                .subscribe {
                    return@subscribe when (it) {
                        is SignUpSuccess -> onEvent(OnSignUpSuccess)
                        SignUpFailure -> onEvent(OnSignUpFailure)
                    }
                }
        }

        is TryToSignUp -> onTryToSignUp(effect.email, effect.username, effect.password)

        NavigateBack -> signUpNavigator.onBackFromRegisterClicked()

    }

}