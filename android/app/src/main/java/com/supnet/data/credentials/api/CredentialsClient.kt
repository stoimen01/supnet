package com.supnet.data.credentials.api

import io.reactivex.Completable
import io.reactivex.Single

interface CredentialsClient {

    fun loginUser(email: String, password: String) : Single<SignResult>

    fun registerUser(email: String, userName: String, password: String) : Single<SignResult>

    fun logoutUser() : Completable

}