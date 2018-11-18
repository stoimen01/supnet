package com.supnet.signaling.rooms

import com.supnet.signaling.entities.Message
import com.supnet.signaling.entities.Room
import io.reactivex.Observable
import java.util.*

interface RoomsManager {

    fun connect()

    fun disconnect()

    fun createRoom(name: String)

    fun joinRoom(roomId: UUID)

    fun leaveRoom()

    fun getState(): Observable<RoomsState>

    fun getRoomData(): Observable<Pair<Room, Set<Message>>>

    fun getRooms(): Observable<List<Room>>

    fun sendMessage(msg: String)

}