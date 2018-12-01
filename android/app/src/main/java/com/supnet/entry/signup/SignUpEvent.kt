package com.supnet.entry.signup

import com.supnet.data.connection.ConnectionState

sealed class SignUpEvent {

    data class ConnectionStateChanged(val connState: ConnectionState) : SignUpEvent()

    object OnSignUpSuccess: SignUpEvent()
    object OnSignUpFailure: SignUpEvent()

    sealed class SignUpViewEvent : SignUpEvent() {

        data class UsernameChanged(val username: String) : SignUpViewEvent()

        data class EmailChanged(val email: String) : SignUpViewEvent()

        data class PasswordChanged(val password: String) : SignUpViewEvent()

        object SignUpClicked : SignUpViewEvent()

        object SignUpBackClicked : SignUpViewEvent()

    }

}