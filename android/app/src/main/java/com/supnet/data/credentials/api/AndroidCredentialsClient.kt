package com.supnet.data.credentials.api

import io.reactivex.Completable
import java.util.concurrent.TimeUnit

class AndroidCredentialsClient : CredentialsClient {

    override fun loginUser(email: String, password: String): Completable {
        return Completable.timer(3, TimeUnit.SECONDS)
            .andThen(Completable.complete())
    }

    override fun registerUser(email: String, userName: String, password: String): Completable =
        Completable.timer(3, TimeUnit.SECONDS)
            .andThen(Completable.complete())

    override fun logoutUser(): Completable =
        Completable.timer(3, TimeUnit.SECONDS)
            .andThen(Completable.complete())

}