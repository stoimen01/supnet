package com.supnet.data.credentials

import com.supnet.data.credentials.api.SignResult
import io.reactivex.Completable
import io.reactivex.Single

interface SupnetRepository {

    fun signIn(email: String, password: String) : Single<SignResult>

    fun signUp(email: String, userName: String, password: String) : Single<SignResult>

    fun signOut() : Completable

}