package com.supnet

import android.app.Application
import com.supnet.common.AndroidSchedulersProvider
import com.supnet.device.connection.AndroidConnectionAgent
import com.supnet.data.local.AndroidSupnetStore
import com.supnet.data.SupnetRepositoryImpl
import com.supnet.data.remote.rest.AndroidRestClient
import com.supnet.data.remote.rest.SupnetRestApi
import com.supnet.data.remote.ws.AndroidWsClient
import com.supnet.signaling.rooms.RxRoomsManager
import com.supnet.signaling.client.RxSignalingClient
import com.supnet.signaling.rooms.RoomsReducer
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
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

    val socketClient by lazy {
        AndroidWsClient(wsBuilder, supnetRepository.userStates(), connectionAgent.getConnectionStates())
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(schedulersProvider.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupnetRestApi::class.java)
        }

        val roomsManager by lazy { RxRoomsManager(signalingClient, RoomsReducer()) }

        private val store by lazy { AndroidSupnetStore() }

        private val restClient by lazy { AndroidRestClient(restApi) }

        private val wsBuilder = { token: String, listener: WebSocketListener ->
            val request = Request.Builder()
                .addHeader("Authorization: Bearer ", token)
                .url("ws://10.0.2.2:8080/signaling")
                .build()
            okHttpClient.newWebSocket(request, listener)
        }

        val schedulersProvider by lazy { AndroidSchedulersProvider() }

        val supnetRepository by lazy { SupnetRepositoryImpl(store, restClient) }

    }
}