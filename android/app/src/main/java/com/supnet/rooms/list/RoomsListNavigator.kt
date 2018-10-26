package com.supnet.rooms.list

import java.util.*

interface RoomsListNavigator {
    fun onRoomCreated(roomId: UUID)
    fun onRoomJoined(roomId: UUID)
}