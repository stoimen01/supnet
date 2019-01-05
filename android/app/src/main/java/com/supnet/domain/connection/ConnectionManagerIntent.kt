package com.supnet.domain.connection

sealed class ConnectionManagerIntent {

    data class Connect(val id: Int) : ConnectionManagerIntent()

}