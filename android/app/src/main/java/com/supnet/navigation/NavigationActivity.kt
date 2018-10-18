package com.supnet.navigation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.supnet.R
import com.supnet.xirsys.Xirsys
import com.supnet.xirsys.XirsysResponse
import org.webrtc.PeerConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

    }

    private fun getIceServers() {

        val data = "Supnet:xxx".toByteArray(Charsets.UTF_8)

        val authToken = "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)

        Xirsys.client.getIceServers(authToken).enqueue(object : Callback<XirsysResponse> {
            override fun onFailure(call: Call<XirsysResponse>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<XirsysResponse>, response: Response<XirsysResponse>) {
                val body = response.body()
                val servers = body?.let {
                    val iceServers = body.data
                    iceServers.servers.map { iceServer ->
                        if (iceServer.credential == null) {
                            PeerConnection.IceServer.builder(iceServer.url)
                                .createIceServer()
                        } else {
                            PeerConnection.IceServer.builder(iceServer.url)
                                .setUsername(iceServer.username)
                                .setPassword(iceServer.credential)
                                .createIceServer()
                        }
                    }
                }

                if (servers == null) {
                    Log.d("ACTIVITY", "SERVERS NOT FOUND")
                } else {
                    Log.d("ACTIVITY", "SERVERS FOUND")
                }

            }
        })
    }







}
