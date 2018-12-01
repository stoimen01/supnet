package com.supnet.data.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.jakewharton.rxrelay2.PublishRelay

class AndroidConnectionAgent(private val context: Context) : BroadcastReceiver(), ConnectionAgent {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkStateRelay = PublishRelay.create<ConnectionState>()

    private val networkObservable = networkStateRelay
        .doOnSubscribe { context.registerReceiver(this, filter) }
        .doOnDispose { context.unregisterReceiver(this) }
        .startWith(getConnectionState())
        .replay(1)
        .refCount()

    override fun getConnectionStates() = networkObservable

    override fun onReceive(context: Context?, intent: Intent?) = networkStateRelay.accept(getConnectionState())

    private fun getConnectionState(): ConnectionState {
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = networkInfo?.isConnectedOrConnecting == true
        return if (isConnected) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED
    }

    companion object {
        private val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    }

}