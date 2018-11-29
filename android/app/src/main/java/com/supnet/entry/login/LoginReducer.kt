package com.supnet.entry.login

import com.supnet.common.ReduceResult
import com.supnet.common.StateReducer
import com.supnet.data.connection.ConnectionState.*
import com.supnet.entry.login.LoginEffect.*
import com.supnet.entry.login.LoginState.Idle
import com.supnet.entry.login.LoginState.Loading
import com.supnet.entry.login.LoginEvent.*
import com.supnet.entry.login.LoginEvent.LoginViewEvent.*

class LoginReducer : StateReducer<LoginState, LoginEvent, LoginEffect> {
    override fun reduce(
        lastResult: ReduceResult<LoginState, LoginEffect>,
        event: LoginEvent
    ): ReduceResult<LoginState, LoginEffect> = when (val state = lastResult.state) {
        is Idle -> state.processEvent(event)
        is Loading -> state.processEvent(event)
    }

    private fun Idle.processEvent(
        event: LoginEvent
    ): ReduceResult<LoginState, LoginEffect> = when (event) {

        is ConnectionStateChanged -> when (event.connState) {
            CONNECTED -> stateOnly(validate())
            DISCONNECTED -> stateOnly(copy(isSignInEnabled = false))
        }

        is EmailChanged -> stateOnly(copy(email = event.email).validate())

        is PasswordChanged -> stateOnly(copy(password = event.password).validate())

        SignInClicked -> if (isSignInEnabled) {
            resultOf(Loading(this), TryToLogin(email, password))
        } else repeat()

        CreateAccountClicked -> effectOnly(NavigateToRegister)

        else -> throwInvalid(this, event)
    }

    private fun Loading.processEvent(
        event: LoginEvent
    ): ReduceResult<LoginState, LoginEffect> = when (event) {

        OnLoginSuccess -> effectOnly(NavigateIndoor)

        OnLoginError -> stateOnly(backState)

        else -> repeat()
    }

    private fun Idle.validate(): Idle {
        val isValid = email.length in 1..63 && password.length in 1..32
        return copy(isSignInEnabled = isValid)
    }

}