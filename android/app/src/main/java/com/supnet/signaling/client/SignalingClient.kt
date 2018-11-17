package com.supnet.signaling.client

import com.supnet.signaling.rooms.RoomsManager
import io.reactivex.Observable
import java.util.*

interface SignalingClient {
    
    sealed class SignalingIntent {
        object Connect: SignalingIntent()
        data class CreateRoom(val name: String): SignalingIntent()
        data class JoinRoom(val roomId: UUID): SignalingIntent()
        data class LeaveRoom(val roomId: UUID): SignalingIntent()
        data class SendMessage(val data: String) : SignalingIntent()
        object Disconnect: SignalingIntent()
    }

    fun getEvents(): Observable<RoomsManager.Event.ConnectionEvent>
    fun processIntent(intent: SignalingIntent)
}