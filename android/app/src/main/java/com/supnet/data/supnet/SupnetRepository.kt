package com.supnet.data.supnet

import com.supnet.SupnetEvent
import io.reactivex.Completable
import io.reactivex.Observable

interface SupnetRepository {

    fun signIn(email: String, password: String)

    fun signInEvents(): Observable<SupnetEvent.SignInEvent>

    fun signUp(email: String, userName: String, password: String)

    fun signUpEvents(): Observable<SupnetEvent.SignUpEvent>

    fun sendInvitation(recipient: String, message: String): Completable

    fun signOut(): Completable

    fun removeAccount(): Completable

    fun openSocket(): Observable<SocketState>

}