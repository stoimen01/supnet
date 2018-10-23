package com.supnet

import android.app.Application
import com.supnet.rooms.SignalingClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class Supnet : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        val signalingClient by lazy {
            val httpClient = OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()
            SignalingClient(httpClient)
        }
    }
}