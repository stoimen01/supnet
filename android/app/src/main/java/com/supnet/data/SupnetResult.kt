package com.supnet.data

sealed class SupnetResult {

    sealed class SignUpResult : SupnetResult() {

        data class SignUpSuccess(val token: String) : SignUpResult()

        object SignUpFailure : SignUpResult()

    }

    sealed class SignInResult : SupnetResult() {

        data class SignInSuccess(val token: String) : SignInResult()

        object SignInFailure : SignInResult()

    }

    sealed class SignOutResult : SupnetResult() {

        object SignOutSuccess : SignOutResult()

        object SignOutFailure : SignOutResult()

    }

    sealed class SignOffResult : SupnetResult() {

        object SignOffSuccess : SignOffResult()

        object SignOffFailure : SignOffResult()

    }

    sealed class InvitationResult : SupnetResult() {

        object InvitationSend : InvitationResult()

        object InvitationFailure : InvitationResult()

    }

}