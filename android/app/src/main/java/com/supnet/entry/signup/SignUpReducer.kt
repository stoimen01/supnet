package com.supnet.entry.signup

import com.supnet.common.ReduceResult
import com.supnet.common.StateReducer
import com.supnet.device.connection.ConnectionState.*
import com.supnet.entry.signup.SignUpEffect.*
import com.supnet.entry.signup.SignUpEvent.*
import com.supnet.entry.signup.SignUpEvent.SignUpViewEvent.*
import com.supnet.entry.signup.SignUpState.*

class SignUpReducer : StateReducer<SignUpState, SignUpEvent, SignUpEffect> {

    override fun reduce(
        lastResult: ReduceResult<SignUpState, SignUpEffect>,
        event: SignUpEvent
    ): ReduceResult<SignUpState, SignUpEffect> = when (val state = lastResult.state) {
        is Idle -> state.processEvent(event)
        is Loading -> state.processEvent(event)
    }

    private fun Idle.processEvent(
        event: SignUpEvent
    ): ReduceResult<SignUpState, SignUpEffect> = when (event) {

        is ConnectionStateChanged -> when (event.connState) {
            CONNECTED -> stateOnly(validate())
            DISCONNECTED -> stateOnly(copy(isCreateEnabled = false))
        }

        is UsernameChanged -> stateOnly(copy(username = event.username).validate())

        is EmailChanged -> stateOnly(copy(email = event.email).validate())

        is PasswordChanged -> stateOnly(copy(password = event.password).validate())

        SignUpClicked -> if (isCreateEnabled) {
            resultOf(Loading(this), TryToSignUp(username, email, password))
        } else repeat()

        SignUpBackClicked -> effectOnly(NavigateBack)

        else -> throwInvalid(this, event)
    }

    private fun Loading.processEvent(
        event: SignUpEvent
    ): ReduceResult<SignUpState, SignUpEffect> = when (event) {

        OnSignUpSuccess -> repeat() /* no op for now */

        OnSignUpFailure -> stateOnly(backState)

        else -> repeat()
    }

    private fun Idle.validate(): Idle {
        val isValid = username.length in 1..63 &&
                email.length in 1..63 &&
                password.length in 1..32

        return copy(isCreateEnabled = isValid)
    }

}