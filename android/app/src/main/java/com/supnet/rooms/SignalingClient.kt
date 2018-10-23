package com.supnet.rooms

import okhttp3.*
import okio.ByteString

class SignalingClient(client: OkHttpClient) : WebSocketListener(), CookieJar {

    init {

        /*val request = Request.Builder()
            .url("ws://echo.websocket.org:10000")
            .build()

        client.newWebSocket(request, this)

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown()*/

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {

    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {

    }

    override fun onMessage(webSocket: WebSocket, text: String) {

    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {

    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {

    }


    /* Cookie jar implementation */
    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {

    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return mutableListOf()
    }
}