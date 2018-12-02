package com.supnet.data

sealed class SupnetIntent {

    data class SignUpIntent(val email: String, val userName: String, val password: String) : SupnetIntent()

    data class SignInIntent(val email: String, val password: String) : SupnetIntent()

    object SignOutIntent : SupnetIntent()

    object SignOffIntent : SupnetIntent()

    data class InvitationIntent(val recipient: String, val message: String) : SupnetIntent()

}