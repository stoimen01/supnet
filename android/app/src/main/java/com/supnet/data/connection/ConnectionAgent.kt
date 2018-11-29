package com.supnet.data.connection

import io.reactivex.Observable

interface ConnectionAgent {

    fun getConnectionStates(): Observable<ConnectionState>

}