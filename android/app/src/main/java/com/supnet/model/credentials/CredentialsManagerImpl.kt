package com.supnet.model.credentials

import io.reactivex.Observable
import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.common.ReduceResult
import com.supnet.common.SchedulersProvider
import com.supnet.common.StateReducer
import com.supnet.model.credentials.CredentialsEvent.UIEvent.*
import com.supnet.model.credentials.CredentialsState.*
import com.supnet.model.credentials.api.CredentialsClient
import com.supnet.model.credentials.store.CredentialsStore

class CredentialsManagerImpl(
    private val credentialsStore: CredentialsStore,
    private val credentialsClient: CredentialsClient,
    private val reducer: StateReducer<CredentialsState, CredentialsEvent, CredentialsEffect>,
    private val schedulersProvider: SchedulersProvider
) : CredentialsManager {

    private val uiEvents = PublishRelay.create<CredentialsEvent>()

    private val errorMessages = PublishRelay.create<String>()

    private val states by lazy {
        return@lazy Observable.merge(uiEvents, credentialsClient.getEvents())
            .observeOn(schedulersProvider.computation())
            .scan(ReduceResult<CredentialsState, CredentialsEffect>(LoggedOut), reducer::reduce)
            .observeOn(schedulersProvider.io())
            .doOnNext {
                it?.effects?.forEach { effect ->
                    return@forEach when(effect) {
                        is CredentialsEffect.ApiEffect -> credentialsClient.handleEffect(effect)
                        is CredentialsEffect.ErrorMessage -> errorMessages.accept(effect.data)
                    }
                }
            }
            .map { it.state }
            .distinctUntilChanged()
            .replay(1)
            .refCount()
    }

    override fun loginUser(
        email: String,
        password: String
    ) = uiEvents.accept(OnLogin(email, password))

    override fun registerUser(
        email: String,
        userName: String,
        password: String
    ) = uiEvents.accept(OnRegister(email, userName, password))

    override fun logoutUser() = uiEvents.accept(OnLogout)

    override fun getCredentialsStates() = states

    override fun getErrorMessages() = errorMessages
}