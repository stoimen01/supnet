package com.supnet.xirsys

import com.google.gson.annotations.SerializedName

data class XirsysResponse(
    @SerializedName("s") val status: String,
    @SerializedName("v") val data: XirsysData
) {

    data class XirsysData(
        @SerializedName("iceServers") val servers: List<IceServer>
    )

    data class IceServer(
        @SerializedName("url") val url: String,
        @SerializedName("username") val username: String,
        @SerializedName("credential") var credential: String? = null
    )

}