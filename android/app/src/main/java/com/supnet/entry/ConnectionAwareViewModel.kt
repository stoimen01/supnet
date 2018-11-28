package com.supnet.entry

import androidx.lifecycle.LiveData
import com.supnet.model.connection.ConnectionState

interface ConnectionAwareViewModel {

    fun getConnectionState(): LiveData<ConnectionState>

}