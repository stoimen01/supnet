package com.supnet.model.credentials

sealed class CredentialsState {

    data class LoggedIn(val username: String) : CredentialsState()

    object LoggedOut : CredentialsState()

    object Loading : CredentialsState()

    object Unknown : CredentialsState()

}