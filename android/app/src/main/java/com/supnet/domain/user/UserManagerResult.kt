package com.supnet.domain.user

sealed class UserManagerResult {

    sealed class SignUpResult : UserManagerResult() {

        data class SignUpSuccess(val token: String) : SignUpResult()

        object SignUpFailure : SignUpResult()

    }

    sealed class SignInResult : UserManagerResult() {

        data class SignInSuccess(val token: String) : SignInResult()

        object SignInFailure : SignInResult()

    }

    sealed class SignOutResult : UserManagerResult() {

        object SignOutSuccess : SignOutResult()

        object SignOutFailure : SignOutResult()

    }

    sealed class SignOffResult : UserManagerResult() {

        object SignOffSuccess : SignOffResult()

        object SignOffFailure : SignOffResult()

    }

}