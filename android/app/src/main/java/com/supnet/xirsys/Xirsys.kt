package com.supnet.xirsys

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.PUT

object Xirsys {

    interface XirsysAPI {
        @PUT("/_turn/Supnet/")
        fun getIceServers(@Header("Authorization") key: String): Call<XirsysResponse>
    }

    private const val xirsysUrl = "https://global.xirsys.net"

    val client: XirsysAPI by lazy {
        Retrofit.Builder()
            .baseUrl(xirsysUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XirsysAPI::class.java)
    }

    /*private fun getIceServers() {

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
    }*/

}