package com.supnet.entry.register

import com.supnet.data.connection.ConnectionState

sealed class RegisterEvent {

    data class ConnectionStateChanged(val connState: ConnectionState) : RegisterEvent()

    object OnRegisterSuccess: RegisterEvent()
    object OnRegisterFailure: RegisterEvent()

    sealed class RegisterViewEvent : RegisterEvent() {

        data class UsernameChanged(val username: String) : RegisterViewEvent()

        data class EmailChanged(val email: String) : RegisterViewEvent()

        data class PasswordChanged(val password: String) : RegisterViewEvent()

        object RegisterClicked : RegisterViewEvent()

        object RegisterBackClicked : RegisterViewEvent()

    }

}