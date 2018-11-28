package com.supnet.model.credentials

import io.reactivex.Observable

interface CredentialsManager {

    fun loginUser(email: String, password: String)

    fun registerUser(email: String, userName: String, password: String)

    fun logoutUser()

    fun getCredentialsStates(): Observable<CredentialsState>

    fun getErrorMessages(): Observable<String>

}