package com.supnet.domain.user

sealed class UserManagerIntent {

    data class SignUpIntent(
        val email: String,
        val userName: String,
        val password: String
    ) : UserManagerIntent()

    data class SignInIntent(
        val email: String,
        val password: String
    ) : UserManagerIntent()

    object SignOutIntent : UserManagerIntent()

    object SignOffIntent : UserManagerIntent()

}