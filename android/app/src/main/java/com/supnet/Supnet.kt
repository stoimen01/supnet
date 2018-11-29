package com.supnet

import android.app.Application
import com.supnet.common.AndroidSchedulersProvider
import com.supnet.data.connection.AndroidConnectionAgent
import com.supnet.data.credentials.api.AndroidCredentialsClient
import com.supnet.data.credentials.store.AndroidCredentialsStore
import com.supnet.data.credentials.CredentialsRepositoryImpl
import com.supnet.signaling.rooms.RxRoomsManager
import com.supnet.signaling.client.RxSignalingClient
import com.supnet.signaling.rooms.RoomsReducer
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class Supnet : Application() {


    override fun onCreate() {
        super.onCreate()
        app = this
    }

    val connectionAgent by lazy {
        AndroidConnectionAgent(applicationContext)
    }

    companion object {

        lateinit var app: Supnet

        private val signalingClient by lazy {
            val httpClient = OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()
            RxSignalingClient(httpClient)
        }

        val roomsManager by lazy { RxRoomsManager(signalingClient, RoomsReducer()) }

        private val credentialsStore by lazy { AndroidCredentialsStore() }

        private val credentialsClient by lazy { AndroidCredentialsClient() }

        val schedulersProvider by lazy { AndroidSchedulersProvider() }

        val credentialsManager by lazy { CredentialsRepositoryImpl(credentialsStore, credentialsClient) }

    }
}