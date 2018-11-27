package com.supnet.model.credentials

import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.model.credentials.CredentialsEvent.UIEvent.*

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.PublishSubject

class CredentialsManagerImpl(
    private val credentialsStore: CredentialsStore,
    private val credentialsApi: CredentialsApi
) : CredentialsManager {

    private val uiEvents = PublishRelay.create<CredentialsEvent>()

    override fun loginUser(
        email: String,
        password: String
    ) = uiEvents.accept(OnLogin(email, password))

    override fun registerUser(
        email: String,
        userName: String,
        password: String
    ) = uiEvents.accept(OnRegister(email, userName, password))

    override fun logoutUser()  = uiEvents.accept(OnLogout)

    /*private val credentialsStates by lazy {
        return@lazy Observable.merge(uiEvents, credentialsApi.getEvents())
            .scan { state, event ->

            }


    }*/




    override fun getCredentialsStates(): Observable<CredentialsState> {


        return Observable.just<CredentialsState>(CredentialsState.LoggedOut)
            .replay(1)
            .autoConnect()
    }
}