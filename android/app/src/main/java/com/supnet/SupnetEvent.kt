package com.supnet

sealed class SupnetEvent {

    sealed class SignUpEvent : SupnetEvent() {

        object SignUpSuccess: SignUpEvent()

        object SignUpFailure: SignUpEvent()

    }

    sealed class SignInEvent : SupnetEvent() {

        object SignInSuccess: SignInEvent()

        object SignInFailure: SignInEvent()

    }


}