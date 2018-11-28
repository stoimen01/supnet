package com.supnet.model.credentials

sealed class CredentialsEffect {

    sealed class ApiEffect : CredentialsEffect() {

        data class RegisterUser(val email: String, val username: String, val password: String) : ApiEffect()

        data class LoginUser(val email: String, val password: String) : ApiEffect()

        object LogoutUser : ApiEffect()

    }

    sealed class StoreEffect : CredentialsEffect() {

    }

    data class ErrorMessage(val data: String) : CredentialsEffect()

}