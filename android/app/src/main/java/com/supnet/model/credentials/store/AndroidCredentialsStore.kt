package com.supnet.model.credentials.store

import io.reactivex.Completable
import io.reactivex.Single

class AndroidCredentialsStore : CredentialsStore {

    override fun saveToken(token: String): Completable {
        return Completable.complete()
    }

    override fun getToken(): Single<String> {
        return Single.never()
    }

}