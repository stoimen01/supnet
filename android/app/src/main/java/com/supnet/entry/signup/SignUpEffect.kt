package com.supnet.entry.signup

sealed class SignUpEffect {

    data class TryToSignUp(
        val username: String,
        val email: String,
        val password: String
    ) : SignUpEffect()

    object TrackConnectionChanges : SignUpEffect()

    object TrackSignUpMessages: SignUpEffect()

    object NavigateBack : SignUpEffect()

}