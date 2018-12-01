package com.supnet.entry.signin

import com.supnet.common.ReduceResult
import com.supnet.common.StateReducer
import com.supnet.data.connection.ConnectionState.*
import com.supnet.entry.signin.SignInEffect.*
import com.supnet.entry.signin.SignInState.Idle
import com.supnet.entry.signin.SignInState.Loading
import com.supnet.entry.signin.SignInEvent.*
import com.supnet.entry.signin.SignInEvent.SignInViewEvent.*

class SignInReducer : StateReducer<SignInState, SignInEvent, SignInEffect> {
    override fun reduce(
        lastResult: ReduceResult<SignInState, SignInEffect>,
        event: SignInEvent
    ): ReduceResult<SignInState, SignInEffect> = when (val state = lastResult.state) {
        is Idle -> state.processEvent(event)
        is Loading -> state.processEvent(event)
    }

    private fun Idle.processEvent(
        event: SignInEvent
    ): ReduceResult<SignInState, SignInEffect> = when (event) {

        is ConnectionStateChanged -> when (event.connState) {
            CONNECTED -> stateOnly(validate())
            DISCONNECTED -> stateOnly(copy(isSignInEnabled = false))
        }

        is EmailChanged -> stateOnly(copy(email = event.email).validate())

        is PasswordChanged -> stateOnly(copy(password = event.password).validate())

        SignInClicked -> if (isSignInEnabled) {
            resultOf(Loading(this), TryToSignIn(email, password))
        } else repeat()

        CreateAccountClicked -> effectOnly(NavigateToRegister)

        else -> throwInvalid(this, event)
    }

    private fun Loading.processEvent(
        event: SignInEvent
    ): ReduceResult<SignInState, SignInEffect> = when (event) {

        OnSignInSuccess -> repeat() /* no-op for now */

        OnSignInError -> stateOnly(backState)

        else -> repeat()
    }

    private fun Idle.validate(): Idle {
        val isValid = email.length in 1..63 && password.length in 1..32
        return copy(isSignInEnabled = isValid)
    }

}