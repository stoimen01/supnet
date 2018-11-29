package com.supnet.data.credentials.api

import io.reactivex.Completable

interface CredentialsClient {

    fun loginUser(email: String, password: String) : Completable

    fun registerUser(email: String, userName: String, password: String) : Completable

    fun logoutUser() : Completable

}