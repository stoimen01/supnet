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

}