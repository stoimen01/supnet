package com.supnet.data.credentials

import io.reactivex.Completable

interface CredentialsRepository {

    fun signIn(email: String, password: String) : Completable

    fun signUp(email: String, userName: String, password: String) : Completable

    fun signOut() : Completable

}