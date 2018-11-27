package com.supnet.model.connection

import io.reactivex.Observable

interface ConnectionAgent {

    fun getConnectionStates(): Observable<ConnectionState>

}