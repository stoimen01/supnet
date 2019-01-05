package com.supnet.domain.connection

import com.jakewharton.rxrelay2.PublishRelay
import com.supnet.domain.connection.ConnectionManagerEvent.*
import com.supnet.domain.connection.ConnectionManagerState.*
import io.reactivex.Observable

class ConnectionManagerImpl : ConnectionManager {

    private val intents = PublishRelay.create<ConnectionManagerIntent>()

    private val events: Observable<ConnectionManagerEvent> by lazy {
        return@lazy intents
            .flatMap {
                return@flatMap when (it) {
                    is ConnectionManagerIntent.Connect -> onConnect()
                }
            }
            .publish()
            .refCount()
    }

    private val states by lazy {
        return@lazy events
            .flatMap {
                return@flatMap when (it) {
                    Connected -> Observable.just(CONNECTED)
                    ConnectFailed -> Observable.just(IDLE)
                }
            }
            .startWith(Observable.just(IDLE))
            .replay(1)
            .refCount()
    }

    override fun sendIntent(intent: ConnectionManagerIntent) = intents.accept(intent)

    override fun events(): Observable<ConnectionManagerEvent> = events

    override fun states(): Observable<ConnectionManagerState> = states

    private fun onConnect(): Observable<ConnectionManagerEvent> {



        return Observable.just<ConnectionManagerEvent>(Connected)
    }

}