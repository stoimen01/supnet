package com.supnet.domain.connection

sealed class ConnectionManagerEvent {

    object Connected : ConnectionManagerEvent()

    object ConnectFailed : ConnectionManagerEvent()

}
