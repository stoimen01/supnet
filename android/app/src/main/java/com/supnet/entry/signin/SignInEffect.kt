package com.supnet.entry.signin

sealed class SignInEffect {

    object TrackConnectionChanges : SignInEffect()

    object TrackSignInEvents : SignInEffect()

    data class TryToSignIn(val email: String, val password: String) : SignInEffect()

    object NavigateToRegister : SignInEffect()

}