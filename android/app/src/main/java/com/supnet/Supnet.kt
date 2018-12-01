package com.supnet

import android.app.Application
import com.supnet.common.AndroidSchedulersProvider
import com.supnet.data.connection.AndroidConnectionAgent
import com.supnet.data.supnet.client.AndroidSupnetClient
import com.supnet.data.supnet.store.AndroidSupnetStore
import com.supnet.data.supnet.SupnetRepositoryImpl
import com.supnet.data.supnet.client.rest.AndroidRestClient
import com.supnet.data.supnet.client.rest.SupnetRestApi
import com.supnet.data.supnet.client.socket.AndroidSocketClient
import com.supnet.signaling.rooms.RxRoomsManager
import com.supnet.signaling.client.RxSignalingClient
import com.supnet.signaling.rooms.RoomsReducer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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
            RxSignalingClient(okHttpClient)
        }

        private val okHttpClient by lazy {
            OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()
        }

        private val restApi by lazy {
            Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupnetRestApi::class.java)
        }

        val roomsManager by lazy { RxRoomsManager(signalingClient, RoomsReducer()) }

        private val store by lazy { AndroidSupnetStore() }

        private val restClient by lazy { AndroidRestClient(restApi) }

        private val socketClient by lazy { AndroidSocketClient(okHttpClient, "ws://10.0.2.2:8080/signaling")}

        private val supnetClient by lazy { AndroidSupnetClient(restClient, socketClient) }

        val schedulersProvider by lazy { AndroidSchedulersProvider() }

        val supnetRepository by lazy { SupnetRepositoryImpl(store, supnetClient) }

    }
}