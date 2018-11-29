package com.supnet.entry.login

sealed class LoginEffect {

    object TrackConnectionChanges : LoginEffect()

    data class TryToLogin(val email: String, val password: String) : LoginEffect()

    object NavigateToRegister : LoginEffect()

    object NavigateIndoor : LoginEffect()

}