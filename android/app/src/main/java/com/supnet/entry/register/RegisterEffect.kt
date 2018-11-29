package com.supnet.entry.register

sealed class RegisterEffect {

    data class TryToRegister(
        val username: String,
        val email: String,
        val password: String
    ) : RegisterEffect()

    object TrackConnectionChanges : RegisterEffect()

    object NavigateBack : RegisterEffect()

    object NavigateIndoor : RegisterEffect()

}