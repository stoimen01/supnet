package com.supnet.data.credentials.store

import io.reactivex.Completable
import io.reactivex.Single

interface CredentialsStore {

    fun saveToken(token: String): Completable

    fun getToken(): Single<String>

}