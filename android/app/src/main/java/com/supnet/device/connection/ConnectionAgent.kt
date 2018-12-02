package com.supnet.device.connection

import io.reactivex.Observable

interface ConnectionAgent {

    fun getConnectionStates(): Observable<ConnectionState>

}