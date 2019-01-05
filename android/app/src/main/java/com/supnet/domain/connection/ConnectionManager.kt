package com.supnet.domain.connection

import io.reactivex.Observable

interface ConnectionManager {

    fun sendIntent(intent: ConnectionManagerIntent)

    fun events(): Observable<ConnectionManagerEvent>

    fun states(): Observable<ConnectionManagerState>

}