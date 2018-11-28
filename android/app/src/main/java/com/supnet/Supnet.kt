package com.supnet

import android.app.Application
import com.supnet.common.AndroidSchedulersProvider
import com.supnet.model.credentials.api.AndroidCredentialsClient
import com.supnet.model.credentials.store.AndroidCredentialsStore
import com.supnet.model.credentials.CredentialsManagerImpl
import com.supnet.model.credentials.CredentialsReducer
import com.supnet.signaling.rooms.RxRoomsManager
import com.supnet.signaling.client.RxSignalingClient
import com.supnet.signaling.rooms.RoomsReducer
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class Supnet : Application() {

    companion object {

        private val signalingClient by lazy {
            val httpClient = OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()
            RxSignalingClient(httpClient)
        }

        val roomsManager by lazy { RxRoomsManager(signalingClient, RoomsReducer()) }

        private val credentialsStore by lazy { AndroidCredentialsStore() }

        private val credentialsClient by lazy { AndroidCredentialsClient() }

        private val credentialsReducer by lazy { CredentialsReducer() }

        private val schedulersProvider by lazy { AndroidSchedulersProvider() }

        val credentialsManager by lazy { CredentialsManagerImpl(credentialsStore, credentialsClient, credentialsReducer, schedulersProvider) }

    }
}