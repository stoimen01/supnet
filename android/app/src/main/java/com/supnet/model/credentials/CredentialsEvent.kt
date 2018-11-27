package com.supnet.model.credentials

sealed class CredentialsEvent {

    sealed class ApiEvent: CredentialsEvent() {

        data class LoginSuccess(val token: String) : ApiEvent()
        object LoginFailure : ApiEvent()

        data class RegisterSuccess(val token: String) : ApiEvent()
        object RegisterFailure : ApiEvent()

        object LogoutSuccess : ApiEvent()
        object LogoutFailure : ApiEvent()

    }

    sealed class UIEvent: CredentialsEvent() {

        data class OnLogin(val email: String, val password: String) : UIEvent()

        data class OnRegister(val email: String, val name: String, val password: String) : UIEvent()

        object OnLogout : UIEvent()

    }

}