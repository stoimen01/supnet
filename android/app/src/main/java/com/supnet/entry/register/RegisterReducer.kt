package com.supnet.entry.register

import com.supnet.common.ReduceResult
import com.supnet.common.StateReducer
import com.supnet.data.connection.ConnectionState.*
import com.supnet.entry.register.RegisterEffect.*
import com.supnet.entry.register.RegisterEvent.*
import com.supnet.entry.register.RegisterEvent.RegisterViewEvent.*
import com.supnet.entry.register.RegisterState.*

class RegisterReducer : StateReducer<RegisterState, RegisterEvent, RegisterEffect> {

    override fun reduce(
        lastResult: ReduceResult<RegisterState, RegisterEffect>,
        event: RegisterEvent
    ): ReduceResult<RegisterState, RegisterEffect> = when (val state = lastResult.state) {
        is Idle -> state.processEvent(event)
        is Loading -> state.processEvent(event)
    }

    private fun Idle.processEvent(
        event: RegisterEvent
    ): ReduceResult<RegisterState, RegisterEffect> = when (event) {

        is ConnectionStateChanged -> when (event.connState) {
            CONNECTED -> stateOnly(validate())
            DISCONNECTED -> stateOnly(copy(isCreateEnabled = false))
        }

        is UsernameChanged -> stateOnly(copy(username = event.username).validate())

        is EmailChanged -> stateOnly(copy(email = event.email).validate())

        is PasswordChanged -> stateOnly(copy(password = event.password).validate())

        RegisterClicked -> if (isCreateEnabled) {
            resultOf(Loading(this), TryToRegister(username, email, password))
        } else repeat()

        RegisterBackClicked -> effectOnly(NavigateBack)

        else -> throwInvalid(this, event)
    }

    private fun Loading.processEvent(
        event: RegisterEvent
    ): ReduceResult<RegisterState, RegisterEffect> = when (event) {

        OnRegisterSuccess -> effectOnly(NavigateIndoor)

        OnRegisterFailure -> stateOnly(backState)

        else -> repeat()
    }

    private fun Idle.validate(): Idle {
        val isValid = username.length in 1..63 &&
                email.length in 1..63 &&
                password.length in 1..32

        return copy(isCreateEnabled = isValid)
    }

}