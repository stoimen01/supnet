package com.supnet.model.credentials

import com.supnet.common.ReduceResult
import com.supnet.common.StateReducer
import com.supnet.model.credentials.CredentialsEffect.ApiEffect.*
import com.supnet.model.credentials.CredentialsEffect.ErrorMessage
import com.supnet.model.credentials.CredentialsEvent.ApiEvent.*
import com.supnet.model.credentials.CredentialsEvent.UIEvent.*
import com.supnet.model.credentials.CredentialsState.*

class CredentialsReducer : StateReducer<CredentialsState, CredentialsEvent, CredentialsEffect> {

    override fun reduce(
        lastResult: ReduceResult<CredentialsState, CredentialsEffect>,
        event: CredentialsEvent
    ): ReduceResult<CredentialsState, CredentialsEffect> = when (val state = lastResult.state) {

        is LoggedIn -> when (event) {

            OnLogout -> resultOf(Loading, LogoutUser)

            else -> throwInvalid(state, event)
        }

        LoggedOut -> when (event) {

            is OnLogin -> resultOf(Loading, LoginUser(event.email, event.password))

            is OnRegister -> resultOf(Loading, RegisterUser(event.email, event.name, event.password))

            else -> throwInvalid(state, event)
        }

        Loading -> when (event) {

            LogoutSuccess -> resultOf(LoggedOut)
            LogoutFailure -> resultOf(LoggedOut, ErrorMessage("Log out error happened")) // add effect to show error

            is LoginSuccess -> resultOf(LoggedIn(event.token))
            LoginFailure -> resultOf(LoggedOut, ErrorMessage("Log in error happened")) // add effect to show error

            is RegisterSuccess -> resultOf(LoggedIn(event.token))
            RegisterFailure -> resultOf(LoggedOut, ErrorMessage("Register error happened")) // add effect to show error

            else -> throwInvalid(state, event)
        }

        Unknown -> throwInvalid(state, event)

    }

}