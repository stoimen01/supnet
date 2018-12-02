package com.supnet.data

sealed class UserState {
    object Undefined : UserState()
    object Loading : UserState()
    object SignedOut : UserState()
    data class SignedIn(val token: String) : UserState()
}