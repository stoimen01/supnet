package com.supnet.model.credentials

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface CredentialsApi {

    fun register(email: String, userName: String, password: String): Single<String>

    fun login(email: String, password: String): Single<String>

    fun logout()

    fun validateToken(token: String): Completable

    fun getEvents(): Observable<CredentialsEvent.ApiEvent>

}