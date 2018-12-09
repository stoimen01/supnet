package com.supnet.domain.user

sealed class UserState {

    abstract fun token(): String?

    object Undefined : UserState() {
        override fun token(): String? = null
    }

    object Loading : UserState() {
        override fun token(): String? = null
    }

    object SignedOut : UserState() {
        override fun token(): String? = null
    }

    data class SignedIn(val token: String) : UserState() {
        override fun token() = token
    }
}