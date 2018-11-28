package com.supnet.model.credentials.api

import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.model.credentials.CredentialsEffect
import com.supnet.model.credentials.CredentialsEffect.ApiEffect.*
import com.supnet.model.credentials.CredentialsEvent
import com.supnet.model.credentials.CredentialsEvent.ApiEvent.*
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class AndroidCredentialsClient : CredentialsClient {

    private val events = PublishRelay.create<CredentialsEvent.ApiEvent>()

    override fun handleEffect(effect: CredentialsEffect.ApiEffect) = when (effect) {
        is RegisterUser -> {
            Observable.timer(3, TimeUnit.SECONDS)
                .subscribe {
                    events.accept(RegisterFailure)
                }
            Unit
        }
        is LoginUser -> {
            Observable.timer(3, TimeUnit.SECONDS)
                .subscribe {
                    events.accept(LoginSuccess(""))
                }
            Unit
        }
        LogoutUser -> events.accept(LogoutSuccess)
    }

    override fun getEvents(): Observable<CredentialsEvent.ApiEvent> = events
}