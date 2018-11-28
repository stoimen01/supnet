package com.supnet.model.credentials.api

import com.supnet.model.credentials.CredentialsEffect
import com.supnet.model.credentials.CredentialsEvent
import io.reactivex.Observable

interface CredentialsClient {

    fun handleEffect(effect: CredentialsEffect.ApiEffect)

    fun getEvents(): Observable<CredentialsEvent.ApiEvent>

}