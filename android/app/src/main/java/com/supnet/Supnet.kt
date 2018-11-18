package com.supnet

import android.app.Application
import com.supnet.signaling.rooms.RxRoomsManager
import com.supnet.signaling.client.RxSignalingClient
import com.supnet.signaling.rooms.RoomsReducer
import okhttp3.OkHttpClient
import org.webrtc.EglBase
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

    }
}